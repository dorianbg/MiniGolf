package Game.Gameplay;

import CourseParser.CourseBuilder;
import CourseParser.PathUtil;
import Game.Gameplay.Dialogs.mpDialog;
import Game.PlayerObjects.Player;
import Game.PlayerObjects.PlayerQueue;
import GameplayObjects.GolfBall;
import GameplayObjects.Obstacle;
import PhysicsEngine.CollisionDetector;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;


/**
 * Created by Dorian on 11-Mar-16.
 * MiniGolfExecutor
 */
public class MultiPlayer extends Game {
    private ArrayList<Label> playerLabels;

    private ArrayList<GolfBall> allGolfBalls;

    private PlayerQueue playerQueue;
    private Player currentPlayer;

    private String explain;
    private Dialog control;
    private Label controls;

    private float arrowX, arrowY;

    private boolean altPressed = false;

    //--------------------------------------------------------------------------------------
    public MultiPlayer(final MiniGolfExecutor game)  {
        getNumberOfPlayers();
        // this gives us enough time for user to input the number of players
        try {
            while(playerQueue == null) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.game = game;
        gameEnd = false;

        modelBatch = new ModelBatch();

//        createPlayers(numberOfPlayers);
        initializeModelInstances();
        initializeVisual();
        initializeCamera();
        initializeEnvironment();
        initializeCollisionDetector();
        initializeSlider();
        for (GolfBall ball: allGolfBalls  ) {
            ball.setForceScalarSlider(relativeY);
        }

        Gdx.input.setInputProcessor(this);

    }



    public void initializeSlider() {
        Texture sliderTexture = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"slider.png"));
        Image slider = new Image(sliderTexture);
        slider.setPosition(Gdx.graphics.getWidth() - slider.getWidth() - 10, 10);

        Texture arrowTexture = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"arrow.png"));
        arrow = new Image(arrowTexture);
        arrowX = slider.getX() - arrow.getWidth();
        arrowY = minY + arrow.getHeight()/2;
        minY = arrow.getHeight()/2;
        arrow.setPosition(arrowX, arrowY);
        relativeY = slider.getHeight() / 2;
        maxY = slider.getHeight();
        stage.addActor(slider);
        stage.addActor(arrow);
        stage.draw();
    }

    public void getNumberOfPlayers(){
        Gdx.input.getTextInput(new Input.TextInputListener() {
            Integer message;
            @Override
            public void input (String text) {
                message = Integer.parseInt(text);
                createPlayers(message);
            }
            @Override
            public void canceled () {
                message = 2;
                createPlayers(message);
            }
        },"Enter the number of players ","", "Max number is 7");

    }

    public void createPlayers(int numberOfPlayers){
        playerQueue = new PlayerQueue(numberOfPlayers);
        currentPlayer = playerQueue.first();
    }

    public void initializeModelInstances(){
        CourseBuilder courseBuilder = new CourseBuilder("course");
        allModelInstances = courseBuilder.getAllModelInstances();
        allObstacles = courseBuilder.getObstacles();
        hole = courseBuilder.getHole();
        GolfBall tempModelBall = courseBuilder.getGolfBall();
        ModelBuilder modelBuilder = new ModelBuilder();
        allGolfBalls = new ArrayList<>();
        // Now we have to initialize a special ball for every player in the player queue
        // and also we will add it to the collection of all balls
        for (Player player : playerQueue.getAllPlayers()){
            player.setGolfBall(new GolfBall( modelBuilder.createSphere(0.05f, 0.05f, 0.05f, 100, 100,
                    new Material(ColorAttribute.createDiffuse(Color.WHITE)), attributes),
                    tempModelBall.getPosition().x, tempModelBall.getPosition().y, tempModelBall.getPosition().z));
            allGolfBalls.add(player.getGolfBall());
        }
        for (Obstacle obstacle : allObstacles){
            obstacle.transform.rotate(obstacle.getxRotation(),obstacle.getyRotation(),
                    obstacle.getzRotation(),obstacle.getRotationDegree());
        }
        startingPosition = courseBuilder.getGolfBall().getPosition().cpy();
    }


    public void initializeVisual(){
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal( PathUtil.assetsPath()+"uiskin.json"));
        score = new Dialog("Score:", skin, "dialog");
        /////////////////////////////////////////////////////
        // change size of score dialog depending on the number of players/////
        //////////////////////////////////////////////////////
        score.setSize(stage.getWidth()/6,stage.getHeight()/2);
        score.setPosition(stage.getWidth()-stage.getWidth(), stage.getHeight()-stage.getHeight()/8);

        explain = new String("zoom in or out use: ALT & arrowkeys\n move camera use: TAB & arromkeys \n  rotate camera use: arrowkeys");
        controls = new Label(explain, skin);
        control = new Dialog("Controls", skin, "dialog");
        control.setSize(stage.getWidth() - stage.getWidth()/6,stage.getHeight()/6);
        control.setPosition(stage.getWidth()-stage.getWidth()/2, stage.getHeight()-stage.getHeight()/8);
        control.getContentTable().add(explain);
        control.getContentTable().row();
        stage.addActor(control);
        stage.draw();

        // here we will add all the playerQueue from the player queue
        playerLabels = new ArrayList<>();
        for (Player player : playerQueue.getAllPlayers()){
            Label temp = new Label(player.getName()+" : " + player.getGolfBall().getHits(),skin);
            playerLabels.add(temp);
            score.getContentTable().add(temp);
            score.getContentTable().row();
        }

        stage.addActor(score);
        stage.draw();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        modelBatch.begin(camera);


        for(ModelInstance modelInstance: allModelInstances){
            if(!(modelInstance instanceof GolfBall)){
                modelBatch.render(modelInstance, environment);
            }
        }
        for (GolfBall golfBall : allGolfBalls) {
            moveBall(golfBall);
            checkBallOutOfRange(golfBall);
            modelBatch.render(golfBall,environment);
        }
        updatePlayerLabels();
        // these are labels for each playes number of shots
        if(!gameEnd)  stage.draw();
        for(Player player : playerQueue.getAllPlayers()){
            if(player.getGolfBall().getFinished()) getWinner();
        }

        modelBatch.end();
    }

    public void updatePlayerLabels(){
        for (int i = 0; i < playerQueue.getSize(); i++){
            playerLabels.get(i).setText(playerQueue.getAllPlayers().get(i).getName()+
                    " : " + playerQueue.getAllPlayers().get(i).getGolfBall().getHits());
        }
    }

    public ArrayList<GolfBall> getAllBall(){
        return allGolfBalls;
    }


    public void getWinner(){
        System.out.println(currentPlayer.getGolfBall().getHits());
        game.setScreen(new mpDialog(game,currentPlayer.getGolfBall().getHits(),
                playerQueue.getAllPlayers().indexOf(currentPlayer)+1));
        gameEnd = true;
    }

    @Override
    public void changePlayer() {
        currentPlayer = playerQueue.next();
    }


    boolean updateSlider = false;
    private Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
    private Vector3 intersection = new Vector3();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        updateSlider = true;
        Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);

        changePlayer();
        System.out.println(currentPlayer.getName());
        currentPlayer.getGolfBall().setPressed(intersection.x, intersection.z);
        for(GolfBall golfBall : allGolfBalls) System.out.println("balls" + golfBall.getSolid());
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        updateSlider = false;
        Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);

        if(Math.abs(currentPlayer.getGolfBall().getDirection().x) < 0.0001f &&
                Math.abs(currentPlayer.getGolfBall().getDirection().z) < 0.0001f     ) {
            currentPlayer.getGolfBall().setReleased(intersection.x, intersection.z);
            //System.out.println(currentPlayer.getGolfBall().getPosition());
            currentPlayer.getGolfBall().hitBall();
            // System.out.println("hit the ball");
        }
        return false;
    }

    private boolean tabPressed = false;


    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.PLUS)
        {
            if (relativeY + 5 < maxY) {
                relativeY = relativeY + 5;
                useCover = true;
                System.out.println("++" + " " + relativeY + " " + maxY);
            } else {
                relativeY = maxY;
                useCover = true;
            }
            arrow.setPosition(arrow.getX(), BASE_HEIGHT + relativeY);
            for(GolfBall ball : allGolfBalls){
                ball.setForceScalarSlider(relativeY);
            }
        }
        if (keycode == Input.Keys.MINUS)
        {
            if(relativeY - 5 > minY)
            {
                relativeY = relativeY - 5;
                useCover = true;
            }
            else
            {
                relativeY = minY;
                useCover = true;
            }
            arrow.setPosition(arrow.getX(), BASE_HEIGHT + relativeY);
            for(GolfBall ball : allGolfBalls){
                ball.setForceScalarSlider(relativeY);
            }
        }
        if (keycode == Input.Keys.ESCAPE) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }

        Vector3 rotation = new Vector3(0, 0, 0);


        if (keycode == Input.Keys.UP) {
            if(altPressed){
                camera.translate(0,0,0.25f);
            }
            if(tabPressed){
                camera.translate(0, +0.25f, 0);
            }
            else
                camera.rotateAround(rotation, Vector3.X, 15);
        }

        if (keycode == Input.Keys.DOWN) {
            if(altPressed){
                camera.translate(0,0,-0.25f);
            }
            if(tabPressed){
                camera.translate(0, -0.25f, 0);
            }
            else
                camera.rotateAround(rotation, Vector3.X, -15);
        }

        if (keycode == Input.Keys.RIGHT) {
            camera.rotateAround(rotation, Vector3.Y, 15);
        }

        if (keycode == Input.Keys.LEFT) {
            camera.rotateAround(rotation, Vector3.Y, -15);
        }

        if (keycode == Input.Keys.TAB) {
            tabPressed = true;
        }
        if(keycode == Input.Keys.ALT_LEFT)
            altPressed = false;

        if(keycode == Input.Keys.ALT_LEFT)
            altPressed = true;

        return true;
    }


    public boolean keyUP(int keycode) {
        if (keycode == Input.Keys.TAB)
            tabPressed = false;

        mouseMoved(1, 1);
        return true;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        float dif, newY;



        if (updateSlider) {
            Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Intersector.intersectRayPlane(pickRay, xzPlane, intersection);
            float[] coords = new float[2];
            coords = currentPlayer.getGolfBall().getPressed();
            float distance = (float) (Math.sqrt(Math.pow(coords[0] - intersection.x, 2) + Math.pow(coords[1] - intersection.z, 2)));
            if (distance > 1) distance = 1;
            dif = maxY - minY;
            newY = distance*dif + minY;
            arrowY = newY;
            if(arrowY > maxY) arrowY = maxY;
            if(arrowY < minY) arrowY = minY;
            arrow.setPosition(arrowX, arrowY);


        } else
        {
            arrowY = minY + arrow.getHeight()/2;
            arrow.setPosition(arrowX, arrowY);

        }
        return false;
    }

    // here we initialize all obstacles for the collision detector
    public void initializeCollisionDetector(){
        ArrayList<ModelInstance> collisionObjects = new ArrayList<>();
        collisionObjects.add(hole);
        for (ModelInstance obstacle : allObstacles){
            collisionObjects.add(obstacle);
        }
        for (ModelInstance golfBall : allGolfBalls){
            collisionObjects.add(golfBall);
        }
        collisionDetector = new CollisionDetector(collisionObjects);
    }

}