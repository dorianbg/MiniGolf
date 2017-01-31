package CourseParser;

import GameplayObjects.GolfBall;
import GameplayObjects.Hole;
import GameplayObjects.Obstacle;
import GameplayObjects.Terrain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Dorian on 13-Mar-16.
 * MiniGolfExecutor
 *
 * THIS CLASS WILL USE A BUILDER PATTERN TOGETHER WITH THE COURSE CREATOR CLASS
 */
public class CourseBuilder implements Screen,InputProcessor {
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private PerspectiveCamera camera;
    private Environment environment;
    private ArrayList<ModelInstance> instances;
    private JPanel menubar;
    private ArrayList<Obstacle> allObstacles;

    public CourseBuilder(String name) {
        initializeCamera();
        initializeEnvironment();
        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        instances = new ArrayList<>();

        String location = PathUtil.assetsPath();
        File file = new File(location + name + ".txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(line != null){
            String[] data = line.split(" ");

            switch (data[0]) {
                case "Terrain":
                    if (data[1].equals("Box")) {
//                        ModelInstance terrain = new Terrain(
//                                createBox(data[0], Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4])),
//                                Float.parseFloat(data[5]), Float.parseFloat(data[6]), Float.parseFloat(data[7]));
                        ModelInstance terrain = new Obstacle(
                                createBox(data[0], Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4])),
                                Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4]),
                                Float.parseFloat(data[5]), Float.parseFloat(data[6]), Float.parseFloat(data[7]),
                                0f,0f,0f,0f);
                        instances.add(terrain);
                    } else {
                        ModelInstance terrain = new Terrain(
                                createSphere(data[0], Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4])),
                                Float.parseFloat(data[5]), Float.parseFloat(data[6]), Float.parseFloat(data[7]));
                        instances.add(terrain);
                    }
                    render(Gdx.graphics.getDeltaTime());
                    break;
                case "Hole": {
                    ModelInstance instance = new Hole(
                            createSphere(data[0], Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4])),
                            Float.parseFloat(data[5]), Float.parseFloat(data[6]), Float.parseFloat(data[7]));
                    instances.add(instance);
                    render(Gdx.graphics.getDeltaTime());
                    break;
                }
                case "Ball": {
                    ModelInstance instance = new GolfBall(
                            createSphere(data[0], Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4])),
                            Float.parseFloat(data[5]), Float.parseFloat(data[6]), Float.parseFloat(data[7]));
                    instances.add(instance);
                    render(Gdx.graphics.getDeltaTime());
                    break;
                }
                case "Obstacle": {
                    // this is an obstacle  without rotation
                    if(data.length == 12) {
                        ModelInstance instance = new Obstacle(createBox(data[0],
                                Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4])),
                                Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4]),
                                Float.parseFloat(data[5]), Float.parseFloat(data[6]), Float.parseFloat(data[7]),
                                Float.parseFloat(data[8]), Float.parseFloat(data[9]), Float.parseFloat(data[10]), Float.parseFloat(data[11]));
                        instances.add(instance);
                    }
                    // this osbtacle has rotation
                    else{
                        ModelInstance instance = new Obstacle(createBox(data[0],
                                Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4])),
                                Float.parseFloat(data[2]), Float.parseFloat(data[3]), Float.parseFloat(data[4]),
                                Float.parseFloat(data[5]), Float.parseFloat(data[6]), Float.parseFloat(data[7]),
                               0f,0f,0f,0f);
                        instances.add(instance);
                    }
                    render(Gdx.graphics.getDeltaTime());
                    break;
                }
                default:
                    throw new IllegalArgumentException("bad format of input");
            }
            Gdx.input.setInputProcessor(this);
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<ModelInstance> getAllModelInstances(){
        return instances;
    }



    public GolfBall getGolfBall(){
        for (ModelInstance instance : instances){
            if (instance instanceof GolfBall){
                return (GolfBall) instance;
            }
        }
        return null;
    }

    public Hole getHole(){
        for (ModelInstance instance : instances){
            if (instance instanceof Hole){
                return (Hole) instance;
            }
        }
        return null;
    }

    public Terrain getTerrain(){
        for (ModelInstance instance : instances){
            if (instance instanceof Terrain){
                return (Terrain) instance;
            }
        }
        return null;
    }

    public ArrayList<Obstacle> getObstacles(){
        ArrayList<Obstacle> allObstacles = new ArrayList<>();
        for (ModelInstance instance : instances){
            if (instance instanceof Obstacle){
                allObstacles.add((Obstacle) instance);
            }
        }
        return allObstacles;
    }


    // FACTORY METHODS
    public Model createSphere(String type, float x, float y, float z) {
        Color color = new Color();
        if (type.equals("Terrain")) {
            color = Color.GREEN;
        }
        else if (type.equals("Ball") ){
            color = Color.WHITE;
        }
        else if (type.equals("Hole") ){
            color = Color.DARK_GRAY;
        }
        return modelBuilder.createSphere(x, y, z,100,100, new Material(ColorAttribute.createDiffuse(color)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }


    public Model createBox(String type, float x, float y, float z) {
        Color color = new Color();
        if (type.equals("Terrain")) {
            color = Color.GREEN;
        }
        else if (type.equals("Obstacle")){
            color = Color.YELLOW;
        }
        return modelBuilder.createBox(x, y, z, new Material(ColorAttribute.createDiffuse(color)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }
    // this will make the model instance along with its position
    public ModelInstance createModelInstance(Model model,float x, float y, float z) {
        return new ModelInstance(model,x,y,z);
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
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        modelBatch.begin(camera);

        for (ModelInstance instance : instances){
            modelBatch.render(instance,environment);
        }

        modelBatch.end();
    }
    @Override
    public void show() {
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
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
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
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
