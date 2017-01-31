package Game.Gameplay;

import AI.AiEngine;
import AI.utils.MoveCommand;
import CourseParser.CourseBuilder;
import CourseParser.PathUtil;
import Game.Gameplay.Dialogs.aiDialog;
import GameplayObjects.GolfBall;
import GameplayObjects.Obstacle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Dorian on 11-Mar-16.
 * MiniGolfExecutor
 */
public class AIPlayer extends Game{

    private Label player;
    private Label aiPlayer;
    private int currentPlayer;
    private GolfBall playerGolfBall;
    private GolfBall aiGolfBall;

    private AsyncExecutor asyncExecutor;
    private AiEngine aiEngine;

    private long computationTime;




    public AIPlayer(final MiniGolfExecutor game, long computationTime){
        this.game = game;
        currentPlayer = 1;
        initializeCamera();
        initializeEnvironment();
        initializeModelInstances();
        initializeVisual();
        initializeCollisionDetector();

        this.computationTime = computationTime;
        this.aiEngine = new AiEngine(this);
        this.aiEngine.setComputationTime(computationTime);

        asyncExecutor = new AsyncExecutor(2);
//        buildAIBackbone();

        Gdx.input.setInputProcessor(this);
        golfPositionTimer();
    }

    public void startTimer(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                aiEngine.cancel();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,computationTime*1000);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        modelBatch.begin(camera);
        for(ModelInstance modelInstance: allModelInstances){
            modelBatch.render(modelInstance, environment);
        }
        for (GolfBall golfBall : allGolfBalls) {
            moveBall(golfBall);
            checkBallOutOfRange(golfBall);
            modelBatch.render(golfBall,environment);
        }
        updatePlayerLabels();
        if(!gameEnd){
            stage.draw();
        }

        if(playerGolfBall.getFinished()) makePlayerwinner();
        if(aiGolfBall.getFinished()) makeAIwinner();

        if(Math.abs(aiGolfBall.direction.x) < 10e-5 && Math.abs(aiGolfBall.direction.z) < 10e-5){
            System.out.println("Distance to HOLE of AI GOLF BALL");
            double distance = Math.sqrt(Math.pow(hole.getPosition().x - aiGolfBall.getPosition().x, 2)
                    + Math.pow(hole.getPosition().z - aiGolfBall.getPosition().z, 2));
            System.out.println(distance);
        }

        modelBatch.end();
    }


    public void golfPositionTimer(){
        System.out.println("STARTS");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(Math.abs(aiGolfBall.direction.x) < 10e-5 && Math.abs(aiGolfBall.direction.z) < 10e-5){
                    System.out.println("Distance to HOLE of AI GOLF BALL");
                    double distance = Math.sqrt(Math.pow(hole.getPosition().x - aiGolfBall.getPosition().x, 2)
                            + Math.pow(hole.getPosition().z - aiGolfBall.getPosition().z, 2));
                    System.out.println(distance);
                }
            }
        };
        Timer timer = new Timer();
        if(computationTime < 3){
            timer.schedule(timerTask,computationTime * 5000);
        }
        else if (computationTime < 6){
            timer.schedule(timerTask,computationTime*3000);
        }
        else{
            timer.schedule(timerTask,computationTime*1500);
        }
    }




    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);
        if(Math.abs(playerGolfBall.direction.x) < 0.00001f && Math.abs(playerGolfBall.direction.z) < 0.00001f
                &&Math.abs(aiGolfBall.direction.x) < 0.00001f && Math.abs(aiGolfBall.direction.z) < 0.00001f) {
            if (currentPlayer == 1) {
                playerGolfBall.setReleased(intersection.x, intersection.z);
                playerGolfBall.hitBall();
            }
            if(Math.abs(playerGolfBall.direction.x) > 0 || Math.abs(playerGolfBall.direction.z) > 0){
                changePlayer();
                startTimer();
                asyncExecutor.submit(new AsyncTask<Void>() {
                    @Override
                    public Void call() throws Exception {
                        MoveCommand command = aiEngine.findNextMove();
                        System.out.println("The best commands is this");
                        System.out.println(command);
                        System.out.println("Top score " + command.score);
                        aiGolfBall.hitBall(command.xDifference,command.yDifference);
                        return null;
                    }
                });
                aiEngine.unCancel();
                System.out.println("AI ball "+aiGolfBall.getPosition());
                System.out.println("Hole" + hole.getPosition());
                changePlayer();
            }
        }
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button){
        Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);
        playerGolfBall.setPressed(intersection.x, intersection.z);
        return false;
    }


    public void moveBall(GolfBall ball){
        Vector3 newPosition = ball.updatePosition();
        ball.transform.setToTranslation(newPosition.x, newPosition.y, newPosition.z);
        ball.setPosition(ball.transform.getTranslation(new Vector3()));
        collisionDetector.check(ball);
    }


    public void initializeModelInstances(){
        CourseBuilder courseBuilder = new CourseBuilder("course");
        allModelInstances = courseBuilder.getAllModelInstances();
        allObstacles = courseBuilder.getObstacles();
        hole = courseBuilder.getHole();
        playerGolfBall = courseBuilder.getGolfBall();
        ModelBuilder modelBuilder = new ModelBuilder();
        aiGolfBall = new GolfBall( modelBuilder.createSphere(0.05f, 0.05f, 0.05f, 100, 100,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)), attributes),
                playerGolfBall.getPosition().x, playerGolfBall.getPosition().y, playerGolfBall.getPosition().z);
        aiGolfBall.setPosition(playerGolfBall.getPosition());
        allGolfBalls = new ArrayList<>();
        allGolfBalls.add(aiGolfBall);
        allModelInstances.add(aiGolfBall);
        allGolfBalls.add(playerGolfBall);
        startingPosition = playerGolfBall.getPosition().cpy();
        for (Obstacle obstacle : allObstacles){
            obstacle.transform.rotate(obstacle.getxRotation(),obstacle.getyRotation(),
                    obstacle.getzRotation(),obstacle.getRotationDegree());
        }
    }

    public void buildAIBackbone(){
        asyncExecutor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                aiEngine.buildStrat();
                return null;
            }
        });
    }

    public void makePlayerwinner(){
        game.setScreen(new aiDialog(game,playerGolfBall.getHits(),1));
        gameEnd = true;
    }

    public void makeAIwinner(){
        game.setScreen(new aiDialog(game,aiGolfBall.getHits(),0));
        gameEnd = true;
    }



    public void initializeVisual(){
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal(PathUtil.assetsPath()+"uiskin.json"));
        player = new Label("Player 1 : " + playerGolfBall.getHits(), skin);
        aiPlayer = new Label("AI bot : " + aiGolfBall.getHits(), skin);
        score = new Dialog("Score:", skin, "dialog");
        score.setSize(stage.getWidth()/6,stage.getHeight()/4);
        score.setPosition(stage.getWidth()-stage.getWidth()/6, stage.getHeight()-stage.getHeight()/4);
        score.getContentTable().add(player);
        score.getContentTable().row();
        score.getContentTable().add(aiPlayer);
        score.getContentTable().row();
        stage.addActor(score);
        stage.draw();
    }

    public void changePlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;
        }
        else{
            currentPlayer = 1;
        }
    }

    public void updatePlayerLabels(){
        player.setText("Player 1 : " + playerGolfBall.getHits());
        aiPlayer.setText("AI Player : " + aiGolfBall.getHits());
    }

    @Override
    public void getWinner() {

    }
    public ArrayList<ModelInstance> getModelWorld(){
        return this.allModelInstances;
    }

    public GolfBall getAIball(){ return this.aiGolfBall;}
    public GolfBall getPlayerBall(){ return this.playerGolfBall;}

    public void dispose(){
        modelBatch.dispose();
        stage.dispose();
        skin.dispose();
        game.dispose();
        asyncExecutor.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            game.setScreen(new MainMenuScreen(game));
            aiEngine.cancel();
        }
        return true;
    }


}