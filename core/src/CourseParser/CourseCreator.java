package CourseParser;

import Game.Gameplay.MainMenuScreen;
import Game.Gameplay.MiniGolfExecutor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Created by Dorian on 12-Mar-16.
 * MiniGolfExecutor
 */
public class CourseCreator implements InputProcessor,Screen {
    private MiniGolfExecutor game;
    private PerspectiveCamera camera;
    private Environment environment;
    private PositionPicker positionPicker;
    private Dialog terrainDialog;
    private Dialog holeDialog;
    private Dialog ballDialog;
    private Dialog obstacleDialog;
    final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
    private Stage stage;
    private String fileName = "course";
    final private Skin skin = new Skin(Gdx.files.internal(PathUtil.assetsPath()+"uiskin.json"));

    private Label controls;
    private String explain;
    private Dialog control;

    private ImageButton[] courseOptions;


    public CourseCreator(){
        stage = new Stage();
        initializeCamera();
        initializeEnvironment();
        courseOptions = new ImageButton[10];
    }

    public CourseCreator(final MiniGolfExecutor game) {
        this();
        this.game = game;
        initializeTerrainDialog();
    }

    public CourseCreator(final MiniGolfExecutor game, String type) {
        this();
        this.game = game;
        courseOptions = new ImageButton[10];
        if (type.equals("Ball")){
            initializeBallDialog();
        }
        if(type.equals("Terrain")){
            initializeTerrainDialog();
        }
        if(type.equals("Hole")){
            initializeHoleDialog();
        }
        if(type.equals("Obstacle")){
            initializeObstacleDialog();
        }
    }


    public void initializeTerrainDialog(){
        explain = new String("move obstacle: arrowkeys\nchange obstacle: spacebar & arrowkeys\n\nchose axes for rotation : X, Y or Z key\n\nmove camera use: TAB & arromkeys\nrotate camera use: SHIFT & arrowkeys\n zoom in or out use: ALT & arrowkeys ");
        controls = new Label(explain, skin);
        control = new Dialog("Controls", skin, "dialog");
        control.setSize(stage.getWidth() - stage.getWidth()/4,stage.getHeight()/2);
        control.setPosition(stage.getWidth()-stage.getWidth()/6, stage.getHeight()-stage.getHeight()/8);
        control.getContentTable().add(explain);
        control.getContentTable().row();
        stage.addActor(control);
        stage.draw();

        for(int i = 0; i < 4; i++) {
            Texture t = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"Terrain"+i+".jpg"));
            SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(t));
            courseOptions[i] = new ImageButton(spriteDrawable);
            int height = 0;
            for(int j = 0; j<i; j++) {
                height += courseOptions[j].getHeight() +10;
            }
            courseOptions[i].setPosition(0, height);
        }

        courseOptions[0].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                initializeObstacleDialog();
                dispose();
            }
        });
        courseOptions[1].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {

                CourseWriter.write("course", false, "Terrain", "Sphere", 1.1f, 0.000001f, 1.1f,
                        0f,0f,0f);

                // Constructs the boundaries of the course
                for (int i = 0; i < 101; i++) {
                    CourseWriter.write("course", true, "Obstacle", "Box", 0.01f, 0.01f, 0.01f,
                            0f + i * 0.0055f, 0f, (float) (Math.sqrt(0.4025f - (0f +i * 0.0055f)*(0f +i * 0.0055f))));
                }
                for (int i = 0; i < 101; i++) {
                    CourseWriter.write("course", true, "Obstacle", "Box", 0.01f, 0.01f, 0.01f,
                            0f - i * 0.0055f, 0f,
                            (float) (Math.sqrt(0.4025f - (0f +i * 0.0055f)*(0f +i * 0.0055f))));
                }
                for (int i = 0; i < 101; i++) {
                    CourseWriter.write("course", true, "Obstacle", "Box", 0.01f, 0.01f, 0.01f,
                            0f + i * 0.0055f, 0f, -(float) (Math.sqrt(0.4025f - (0f +i * 0.0055f)*(0f +i * 0.0055f))));
                }
                for (int i = 0; i < 101; i++) {
                    CourseWriter.write("course", true, "Obstacle", "Box", 0.01f, 0.01f, 0.01f,
                            0f - i * 0.0055f, 0f, -(float) (Math.sqrt(0.4025f - (0f +i * 0.0055f)*(0f +i * 0.0055f))));
                }
                game.setScreen(new PositionPicker("Terrain",game));
            }
        });
        courseOptions[2].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                CourseWriter.write("course", false, "Terrain", "Box", 1f,0.00001f,1f,
                        0f,0f,0f);
                CourseWriter.write("course", true, "Obstacle", "Box", 0.01f, 0.13f, 1f,
                        0.5f, 0f, 0f);
                CourseWriter.write("course", true, "Obstacle", "Box", 0.01f, 0.13f, 1f,
                        -0.5f, 0f, 0f);
                CourseWriter.write("course", true, "Obstacle", "Box", 1f, 0.13f, 0.01f,
                        0f, 0f, 0.5f);
                CourseWriter.write("course", true, "Obstacle", "Box", 1f, 0.13f, 0.01f,
                        0f, 0f, -0.5f);
                game.setScreen(new PositionPicker("Terrain",game));
            }
        });

 // HELP
        courseOptions[3].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PositionPicker("Obstacle", game, 0.2f, 0.1f, 0.1f));
            }
        });



        terrainDialog = new Dialog("Terrain type", skin, "dialog") {
            public void result(Object obj) {
            }
        }.show(stage);
        for(int i = 0; i < 4; i++)terrainDialog.addActor(courseOptions[i]);
        terrainDialog.setSize(stage.getWidth()/4,stage.getHeight());
        terrainDialog.setPosition(0, stage.getHeight());
    }

    /////-----------------------------------------------------------------------------/////

    public void initializeHoleDialog(){
        explain = new String("move obstacle: arrowkeys\nchange obstacle: spacebar & arrowkeys\n\nchose axes for rotation : X, Y or Z key\n\nmove camera use: TAB & arromkeys\nrotate camera use: SHIFT & arrowkeys\n zoom in or out use: ALT & arrowkeys ");
        controls = new Label(explain, skin);
        control = new Dialog("Controls", skin, "dialog");
        control.setSize(stage.getWidth() - stage.getWidth()/4,stage.getHeight()/2);
        control.setPosition(stage.getWidth()-stage.getWidth()/6, stage.getHeight()-stage.getHeight()/8);
        control.getContentTable().add(explain);
        control.getContentTable().row();
        stage.addActor(control);
        stage.draw();

        for(int i = 0; i < 2; i++) {
            Texture t = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"Hole.png"));
            if(i == 0) {
                t = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"Terrain"+i+".jpg"));
            }
            SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(t));
            courseOptions[i] = new ImageButton(spriteDrawable);
            int height = 0;
            for(int j = 0; j<i; j++) height += courseOptions[j].getHeight() +10;
            courseOptions[i].setPosition(0, height);
        }

        courseOptions[0].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                initializeBallDialog();
                dispose();
            }
        });
        courseOptions[1].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PositionPicker("Hole",game));
            }
        });

        holeDialog = new Dialog("Hole", skin, "dialog") {
            public void result(Object obj) {
            }
        }.show(stage);
        for(int i = 0; i < 2; i++)holeDialog.addActor(courseOptions[i]);
        holeDialog.setSize(stage.getWidth()/4,stage.getHeight());
        holeDialog.setPosition(0, stage.getHeight());
    }

    //---------------------------------------------------------------------------------------------------//


    public void initializeBallDialog(){
        explain = new String("move obstacle: arrowkeys\nchange obstacle: spacebar & arrowkeys\n\nchose axes for rotation : X, Y or Z key\n\nmove camera use: TAB & arromkeys\nrotate camera use: SHIFT & arrowkeys\nzoom in or out use: ALT & arrowkeys");
        controls = new Label(explain, skin);
        control = new Dialog("Controls", skin, "dialog");
        control.setSize(stage.getWidth() - stage.getWidth()/4,stage.getHeight()/2);
        control.setPosition(stage.getWidth()-stage.getWidth()/6, stage.getHeight()-stage.getHeight()/8);
        control.getContentTable().add(explain);
        control.getContentTable().row();
        stage.addActor(control);
        stage.draw();

        for(int i = 0; i < 2; i++) {
            Texture t = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"GolfBall.png"));
            if(i == 0) {
                t = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"Terrain"+i+".jpg"));
            }
            SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(t));
            courseOptions[i] = new ImageButton(spriteDrawable);
            int height = 0;
            for(int j = 0; j<i; j++) height += courseOptions[j].getHeight() +10;
            courseOptions[i].setPosition(0, height);
        }

        courseOptions[0].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
        courseOptions[1].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PositionPicker("Ball",game));
            }
        });

        ballDialog = new Dialog("Golfball", skin, "dialog") {
            public void result(Object obj) {
            }
        }.show(stage);
        for(int i = 0; i < 2; i++)ballDialog.addActor(courseOptions[i]);
        ballDialog.setSize(stage.getWidth()/4,stage.getHeight());
        ballDialog.setPosition(0, stage.getHeight());
    }


    ///----------------------------------------------------------------------------------//////



    public void initializeObstacleDialog(){
        explain = new String("move obstacle: arrowkeys\nchange obstacle: spacebar & arrowkeys\n\nchose axes for rotation : X, Y or Z key\n\nmove camera use: TAB & arromkeys\nrotate camera use: SHIFT & arrowkeys\n" +
                " zoom in or out use: ALT & arrowkeys");
        controls = new Label(explain, skin);
        control = new Dialog("Controls", skin, "dialog");
        control.setSize(stage.getWidth() - stage.getWidth()/4,stage.getHeight()/2);
        control.setPosition(stage.getWidth()-stage.getWidth()/6, stage.getHeight()-stage.getHeight()/8);
        control.getContentTable().add(explain);
        control.getContentTable().row();
        stage.addActor(control);
        stage.draw();

        Texture t = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"Terrain"+0+".jpg"));
        for(int i = 0; i < 3; i++) {
            if(i>=1) {
                t = new Texture(Gdx.files.internal(PathUtil.assetsPath()+"obstacle"+(i-1)+".png"));
            }

            SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(t));
            courseOptions[i] = new ImageButton(spriteDrawable);
            int height = 0;
            for(int j = 0; j<i; j++) height += courseOptions[j].getHeight() +10;
            courseOptions[i].setPosition(0, height);
        }

        courseOptions[0].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                initializeHoleDialog();
                dispose();
            }
        });
        courseOptions[1].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PositionPicker("Obstacle",game, 0.2f, 0.1f, 0.1f));
            }
        });
        courseOptions[2].addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PositionPicker("Obstacle",game, 0.3f, 0.2f, 0.3f));
            }
        });

        holeDialog = new Dialog("Obstacles", skin, "dialog") {
            public void result(Object obj) {
            }
        }.show(stage);
        for(int i = 0; i < 3; i++)holeDialog.addActor(courseOptions[i]);
        holeDialog.setSize(stage.getWidth()/4,stage.getHeight());
        holeDialog.setPosition(0, stage.getHeight());
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        Gdx.input.setInputProcessor(stage);

        game.batch.begin();
        stage.draw();

        game.batch.end();
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            game.setScreen(new MainMenuScreen(game));
        }

        return true;
    }

    public void initializeCamera() {
        this.camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

    // UNIMPLEMENTED AKA USELESS
    @Override
    public void resize(int width, int height) {

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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
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

}