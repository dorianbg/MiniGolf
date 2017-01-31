package Game.Gameplay;

import GameplayObjects.GolfBall;
import GameplayObjects.Hole;
import GameplayObjects.Obstacle;
import PhysicsEngine.CollisionDetector;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

/**
 * Created by Dorian on 09-Apr-16.
 * MiniGolf
 */
public abstract class Game implements Screen,InputProcessor {
    protected PerspectiveCamera camera;
    protected Environment environment;
    protected MiniGolfExecutor game;
    protected ModelBatch modelBatch = new ModelBatch();
    protected CollisionDetector collisionDetector;
    protected Dialog score;
    protected Skin skin;
    protected Stage stage;
    protected ArrayList<Obstacle> allObstacles;
    protected Hole hole;
    protected ArrayList<ModelInstance> allModelInstances;
    protected ArrayList<GolfBall> allGolfBalls;
    protected Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
    protected Vector3 intersection = new Vector3();
    protected boolean gameEnd;
    protected final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;



    protected final int BASE_HEIGHT = 5;
    protected Image arrow;
    protected float relativeY = 0, maxY, minY=0;
    protected boolean useCover = false;

    protected Vector3 startingPosition = new Vector3(0,0,0);


    public void initializeCamera() {
        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0.7f, 0.7f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 200f;
    }
    public void initializeEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,
                0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -
                0.8f, 0.3f, -1f));
    }

    //Outside this range, the ball will be returned to the starting position
    private final int outOfBoundsRange = 5;

    public void moveBall(GolfBall ball){
        Vector3 vector = ball.updatePosition();
        ball.transform.setToTranslation(vector.x, vector.y, vector.z);
        ball.setPosition(ball.transform.getTranslation(new Vector3()));
        collisionDetector.check(ball);
    }


    public void checkBallOutOfRange(GolfBall ball){
        if(ball.getPosition().len() > outOfBoundsRange)
        {
            System.out.println("Ball out of bounds. Returning to the field.");
            ball.setPosition(startingPosition.cpy());
            ball.setDirection(new Vector3(0,0,0));
            ball.setSolid(false);
        }
    }
    // here we initialize all obstacles for the collision detector
    public void initializeCollisionDetector(){
        collisionDetector = new CollisionDetector(allModelInstances);
    }


    public abstract void initializeModelInstances();

    public abstract void initializeVisual();

    public abstract void updatePlayerLabels();

    public abstract void getWinner();

    public abstract void changePlayer() ;

    public void dispose(){
        modelBatch.dispose();
        stage.dispose();
        skin.dispose();
        game.dispose();
    }

    public Hole getHole(){
        return hole;
    }

    @Override
    public boolean keyUp(int keycode) {  return false;  }
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {   }

    @Override
    public void resize(int width, int height) {    }

    @Override
    public void pause() {    }

    @Override
    public void resume() {    }

    @Override
    public void hide() {    }

}
