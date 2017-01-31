package Game.Gameplay;

import CourseParser.CourseBuilder;
import CourseParser.PathUtil;
import Game.Gameplay.Dialogs.spDialog;
import GameplayObjects.GolfBall;
import GameplayObjects.Obstacle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
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
public class SinglePlayer extends Game {

    private Label player;
    private Label controls;
    private GolfBall playerGolfBall;
    private String explain;
    private Dialog control;
    private float arrowX, arrowY;

    public SinglePlayer(final MiniGolfExecutor game) {
        this.game = game;
        gameEnd = false;

        initializeCamera();
        initializeEnvironment();
        initializeModelInstances();
        initializeVisual();
        initializeCollisionDetector();
        initializeSlider();
        playerGolfBall.setForceScalarSlider(relativeY);

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


    public void initializeModelInstances() {
        CourseBuilder courseBuilder = new CourseBuilder("course");
        allModelInstances = courseBuilder.getAllModelInstances();
        allObstacles = courseBuilder.getObstacles();
        for (Obstacle obstacle : allObstacles) {
            obstacle.transform.rotate(obstacle.getxRotation(), obstacle.getyRotation(),
                    obstacle.getzRotation(), obstacle.getRotationDegree());
        }
        hole = courseBuilder.getHole();
        playerGolfBall = courseBuilder.getGolfBall();
        allGolfBalls = new ArrayList<>();
        allGolfBalls.add(playerGolfBall);
        startingPosition = courseBuilder.getGolfBall().getPosition().cpy();
    }


    public void initializeVisual() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal(PathUtil.assetsPath()+"uiskin.json"));
        player = new Label("Player 1 : " + playerGolfBall.getHits(), skin);

        explain = new String("\nzoom in or out use: ALT & arrowkeys \n move camera use: TAB & arromkeys \n  rotate camera use: arrowkeys");
        controls = new Label(explain, skin);

        score = new Dialog("Score:", skin, "dialog");
        control = new Dialog("Controls", skin, "dialog");
        score.setSize(stage.getWidth() / 6, stage.getHeight() / 6);
        score.setPosition(stage.getWidth() - stage.getWidth(), stage.getHeight() - stage.getHeight() / 8);
        score.getContentTable().add(player);
        score.getContentTable().row();
        stage.addActor(score);
        stage.draw();
        control.setSize(stage.getWidth() - stage.getWidth() / 6, stage.getHeight() / 6);
        control.setPosition(stage.getWidth() - stage.getWidth() / 2, stage.getHeight() - stage.getHeight() / 8);
        control.getContentTable().add(explain);
        control.getContentTable().row();
        stage.addActor(control);
        stage.draw();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        modelBatch.begin(camera);

        for (ModelInstance modelInstance : allModelInstances) {
            modelBatch.render(modelInstance, environment);
        }
        for (GolfBall golfBall : allGolfBalls) {
            moveBall(golfBall);
            checkBallOutOfRange(golfBall);
        }
        updatePlayerLabels();
        // these are labels for each playes number of shots
        if (!gameEnd) stage.draw();
        if(playerGolfBall.getFinished()) getWinner();

        modelBatch.end();
    }

    public void updatePlayerLabels() {
        player.setText("Player 1 : " + playerGolfBall.getHits());
    }

    public void getWinner() {
        System.out.println(playerGolfBall.getHits());
        game.setScreen(new spDialog(game,playerGolfBall.getHits()));
        gameEnd = true;
    }


    public void changePlayer() {
    }

    private boolean tabPressed = false;

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.EQUALS) {
            if (relativeY + 5 < maxY) {
                relativeY = relativeY + 5;
                useCover = true;
            } else {
                relativeY = maxY;
                useCover = true;
            }
            arrow.setPosition(arrow.getX(), BASE_HEIGHT + relativeY);
            playerGolfBall.setForceScalarSlider(relativeY);
        }
        if (keycode == Input.Keys.MINUS) {
            if (relativeY - 5 > minY) {
                relativeY = relativeY - 5;
                useCover = true;
            } else {
                relativeY = minY;
                useCover = true;
            }
            arrow.setPosition(arrow.getX(), BASE_HEIGHT + relativeY);
            playerGolfBall.setForceScalarSlider(relativeY);
        }
        if (keycode == Input.Keys.ESCAPE) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }

        /*
        Vector3 position = camera.position;
        if (keycode == Input.Keys.L) {
            camera.lookAt(playerGolfBall.getPosition());
            System.out.println("looked");
        }
        if (keycode == Input.Keys.SPACE) {
            camera.project(position);
            System.out.print("back");
        }
        */


        Vector3 rotation = new Vector3(0, 0, 0);


        if (keycode == Input.Keys.UP) {
            if (tabPressed) {
                camera.translate(0, +0.25f, 0);
            } else
                camera.rotateAround(rotation, Vector3.X, 15);
        }

        if (keycode == Input.Keys.DOWN) {
            if (tabPressed) {
                camera.translate(0, -0.25f, 0);
            } else
                camera.rotateAround(rotation, Vector3.X, -15);
        }

        if (keycode == Input.Keys.RIGHT) {
            if (tabPressed) {
                camera.translate(0.25f, 0, 0);
            } else
                camera.rotateAround(rotation, Vector3.Y, 15);
        }

        if (keycode == Input.Keys.LEFT) {
            if (tabPressed) {
                camera.translate(-0.25f, 0, 0);
            } else
                camera.rotateAround(rotation, Vector3.Y, -15);
        }

        if (keycode == Input.Keys.TAB) {
            tabPressed = true;
        }


        return true;
    }

    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.TAB)
            tabPressed = false;

        mouseMoved(1, 1);
        return true;
    }

    private boolean updateSlider = false;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateSlider = true;
        Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);

        playerGolfBall.setPressed(intersection.x, intersection.z);
        System.out.println("Touch down" + screenX + " " + screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateSlider = false;
        Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);

        if (Math.abs(playerGolfBall.direction.x) < 0.001 && Math.abs(playerGolfBall.direction.z) < 0.001) {
            playerGolfBall.setReleased(intersection.x, intersection.z);
            playerGolfBall.hitBall();
        }
        return false;
    }




    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        float dif, newY;
        if (updateSlider) {
            Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Intersector.intersectRayPlane(pickRay, xzPlane, intersection);
            float[] coords = new float[2];
            coords = playerGolfBall.getPressed();
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
}