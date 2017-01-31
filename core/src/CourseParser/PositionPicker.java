package CourseParser;

import Game.Gameplay.MainMenuScreen;
import Game.Gameplay.MiniGolfExecutor;
import GameplayObjects.GolfBall;
import GameplayObjects.Hole;
import GameplayObjects.Obstacle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import java.util.ArrayList;

/**
 * Created by Dorian on 15-Mar-16.
 * MiniGolf
 */
public class PositionPicker implements Screen,InputProcessor {
    private Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
    private Vector3 intersection = new Vector3();
    private PerspectiveCamera camera;
    private Environment environment;
    private ArrayList<ModelInstance> instances;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private String type;
    private ModelInstance placePostition;
    private ArrayList<Obstacle> allObstacles;
    // the
    final MiniGolfExecutor game;

    private  float width, height, depth, heightBall = 0.05f, heightHole=0.001f;

    public PositionPicker(String type, MiniGolfExecutor game) {
        this.game = game;
        this.type = type;
        initialize();
        initializeCamera();
        initializeEnvironment();
        Gdx.input.setInputProcessor(this);
    }

    public PositionPicker(String type, MiniGolfExecutor game ,float width, float height, float depth){
        this.type = type;this.game = game;this.width = width;this.height = height;this.depth = depth;
        initialize();
        initializeCamera();
        initializeEnvironment();
        Gdx.input.setInputProcessor(this);
    }


    public void initialize()  {
        CourseBuilder courseBuilder = new CourseBuilder("course");
        allObstacles = courseBuilder.getObstacles();
        instances = courseBuilder.getAllModelInstances();
        modelBuilder = new ModelBuilder();
        placePostition = new Obstacle(
                modelBuilder.createBox(10f, 10f, 10f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
                10f,10f,10f,
                10, 10, 10,0f,0f,0f,0f) ;
        modelBatch = new ModelBatch();
        for (Obstacle obstacle : allObstacles){
            obstacle.transform.rotate(obstacle.getxRotation(),obstacle.getyRotation(),
                    obstacle.getzRotation(),obstacle.getRotationDegree());
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        modelBatch.begin(camera);

        modelBatch.render(placePostition, environment);
        for (ModelInstance instance : instances){
            modelBatch.render(instance,environment);
        }


        modelBatch.end();

    }



    // the mouseMoved method is responsible for visualizing the process of building the items

    private float xRotation = 0f;
    private float yRotation = 0;
    private float zRotation = 1f;
    private float degreesRotated = 0f;
    private boolean shiftPressed = false;
    private boolean spacePressed = false;
    private boolean tabPressed = false;
    private boolean altPressed = false;


    private float yHeight = height;


    @Override
    public boolean keyDown(int keycode) {
        Vector3 rotation = new Vector3(0, 0, 0);

        if (keycode == Input.Keys.DOWN) {
            if(altPressed){
                camera.translate(0,0,-0.25f);
            }
            if (shiftPressed) {
                camera.rotateAround(rotation, Vector3.X, -15);
            } else if (tabPressed) {
                camera.translate(0, +0.25f, 0);
            } else if (spacePressed) {
                if (xRotation == 1f && width > 7.4505806E-9) {
                    width = width - 0.05f;
                }
                if (yRotation == 1f && height > 0) {
                    height = height - 0.05f;
                }
                if (zRotation == 1f && depth > 7.4505806E-9) {
                    depth = depth - 0.05f;
                }
            }
            else {
                switch (type) {
                    case "Ball":
                        heightBall -= 0.005f;
                        break;
                    case "Obstacle":
                        yHeight -= 0.01f;
                        break;
                    case "Hole":
                        heightHole -= 0.025f;
                        break;
                }
            }
        }

        if (keycode == Input.Keys.UP) {
            if(altPressed){
                camera.translate(0,0,0.25f);
            }
            if(shiftPressed){
                    camera.rotateAround(rotation, Vector3.X, 15);
                }
            else if(tabPressed){
                camera.translate(0, -0.25f, 0);
            }
            else if(spacePressed) {
                if (xRotation == 1f) {
                    width = width + 0.05f;
                }
                if (yRotation == 1f) {
                    height = height + 0.05f;
                }
                if (zRotation == 1f) {
                    depth = depth + 0.05f;
                }
            }
            else{

            switch (type) {
                case "Ball":
                    heightBall += 0.01f;
                    break;
                case "Obstacle":
                    yHeight += 0.025f;
                    break;
                case "Hole":
                    heightHole += 0.0025f;
                    break;
                 }
            }
        }

        if (keycode == Input.Keys.LEFT) {
            if(shiftPressed) {
                camera.rotateAround(rotation, Vector3.Y, -15);
            }
            else if(tabPressed){
                camera.translate(-0.25f, 0, 0);
            }
             else
                degreesRotated -= 5;
        }

        if (keycode == Input.Keys.RIGHT) {
            if(shiftPressed){
                camera.rotateAround(rotation, Vector3.Y, 15);
        }
            else if(tabPressed){
                camera.translate(0.25f, 0, 0);
            }
            else
                degreesRotated += 5;
        }

        if (keycode == Input.Keys.SHIFT_LEFT) {
            shiftPressed = true;
        }

        if (keycode == Input.Keys.SPACE) {
            spacePressed = true;
        }

        if(keycode == Input.Keys.TAB)
        {
            tabPressed = true;
        }
        if(keycode == Input.Keys.ALT_LEFT)
            altPressed = true;

        if (keycode == Input.Keys.Z)
        {
            xRotation = 0f;
            yRotation = 0f;
            zRotation = 1f;
            degreesRotated = 0f;
        }

        if (keycode == Input.Keys.Y)
        {
            xRotation = 0f;
            yRotation = 1f;
            zRotation = 0f;
            degreesRotated = 0f;
        }

        if (keycode == Input.Keys.X)
        {
            xRotation = 1f;
            yRotation = 0f;
            zRotation = 0f;
            degreesRotated = 0f;
        }

        mouseMoved(1,1);

        return true;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);
        switch (type) {
            case "Obstacle":
                placePostition = new Obstacle(modelBuilder.createBox(width, height, depth,
                        new Material(ColorAttribute.createDiffuse(Color.BLUE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
                        width,height,depth,
                        intersection.x, yHeight, intersection.z, xRotation, yRotation, zRotation,degreesRotated);
                placePostition.transform.rotate(xRotation,yRotation,zRotation,degreesRotated);
                break;
            case "Ball":
                placePostition = new GolfBall(
                        modelBuilder.createSphere(0.04f, 0.04f, 0.04f, 100, 100,
                                new Material(ColorAttribute.createDiffuse(Color.WHITE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
                        intersection.x, heightBall, intersection.z);
                //GolfBall.setPosition(intersection.x, heightBall, intersection.z);

                break;
            case "Hole":
                placePostition = new Hole(modelBuilder.createSphere(0.05f, 0.0001f, 0.05f, 100, 100,
                        new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
                        intersection.x, heightHole, intersection.z);
                break;
        }

        render(Gdx.graphics.getDeltaTime());
        return true;
    }


    // this method actually writes where the obstacle was placed
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);

        Vector3 pointOnNearPlane = camera.unproject(new Vector3(intersection.x,0f,intersection.z));
        render(Gdx.graphics.getDeltaTime());


        switch (type) {
            case "Terrain":
                game.setScreen(new CourseCreator(game, "Terrain"));
                break;
            case "Hole":
                if(button == 0) {
                    CourseWriter.write("course", true, "Hole", "Sphere", 0.07f, 0.0001f, 0.07f,
                            intersection.x, heightHole, intersection.z);
                    game.setScreen(new CourseCreator(game, "Ball"));
                }
                if(button == 1){
                    game.setScreen(new CourseCreator(game, "Hole"));
                }
                break;
            case "Ball":
                if(button == 0){
                    CourseWriter.write("course", true, "Ball", "Sphere", 0.05f, 0.05f, 0.05f,
                            intersection.x, heightBall, intersection.z);
                    // HERE WE SPECIFY THE DIMENSIONS OF A HOLE
                    game.setScreen(new MainMenuScreen(game));
                }
                if(button == 1){
                    game.setScreen(new CourseCreator(game, "Ball"));
                }
                break;
            case "Obstacle":
                if(button == 0){
                    CourseWriter.write("course", true, "Obstacle", "Box", width, height, depth,
                            intersection.x, yHeight, intersection.z, xRotation, yRotation, zRotation, degreesRotated);
                    game.setScreen(new CourseCreator(game, "Obstacle"));
                }
                if(button == 1){
                    game.setScreen(new CourseCreator(game, "Obstacle"));
                }
                break;
        }

        return true;
    }



    @Override
    public boolean scrolled(int amount) {
        float temp = width;
        width = depth;
        depth = temp;
        render(Gdx.graphics.getDeltaTime());
        return true;
    }


    // INITIALIZERS
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

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public void hide() {

    }
    @Override
    public void show() {
    }


    @Override
    public void dispose() {

    }
    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SHIFT_LEFT)
            shiftPressed = false;

        if (keycode == Input.Keys.SPACE)
            spacePressed = false;

        if(keycode == Input.Keys.TAB)
            tabPressed = false;

        if(keycode == Input.Keys.ALT_LEFT)
            altPressed = false;


        mouseMoved(1,1);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        mouseMoved(1,1);
        return true;
    }

}
