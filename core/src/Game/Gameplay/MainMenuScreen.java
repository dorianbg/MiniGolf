package Game.Gameplay;

/**
 * Created by Dorian on 13-Mar-16.
 * MiniGolfExecutor
 */

//import CourseParser.CourseCreator;

import CourseParser.CourseCreator;
import CourseParser.PathUtil;
import Game.Gameplay.Dialogs.DifficultyDialog;
import Game.PlayerObjects.Player;
import Game.PlayerObjects.PlayerQueue;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;


public class MainMenuScreen implements Screen {

    final private MiniGolfExecutor game;
    final private Stage stage;

    private PlayerQueue playerPlayerQueue;
    private ArrayList<Player> players;

    private OrthographicCamera camera;

    public MainMenuScreen(final MiniGolfExecutor game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal(PathUtil.assetsPath()+"uiskin.json"));
        Texture texture = new Texture( "miniG.png");
        Image image = new Image(texture);
        stage.addActor(image);



        TextButton courseCreator = new TextButton("Course creator", skin, "default");
        courseCreator.setWidth(200);
        courseCreator.setHeight(50);
        courseCreator.setPosition(Gdx.graphics.getWidth()/2 - 200/2, Gdx.graphics.getHeight()/2 + 160 );
        courseCreator.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CourseCreator(game));
                dispose();
            }
        });
        stage.addActor(courseCreator);


        TextButton singlePlayer = new TextButton("Single player", skin, "default");
        singlePlayer.setWidth(200);
        singlePlayer.setHeight(50);
        singlePlayer.setPosition(Gdx.graphics.getWidth()/2 - 200/2, Gdx.graphics.getHeight()/2 + 100 );
        singlePlayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SinglePlayer(game));
              //  game.setScreen(new PlayerSelection(game, true));
                dispose();
            }
        });
        stage.addActor(singlePlayer);


        TextButton multiPlayer = new TextButton("Multi player", skin, "default");
        multiPlayer.setWidth(200);
        multiPlayer.setHeight(50);
        multiPlayer.setPosition(Gdx.graphics.getWidth()/2 - 200/2, Gdx.graphics.getHeight()/2 + 40);
        multiPlayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
               game.setScreen(new MultiPlayer(game));
                dispose();
            }
        });
        stage.addActor(multiPlayer);


        TextButton AIplayer = new TextButton("AI player", skin, "default");
        AIplayer.setWidth(200);
        AIplayer.setHeight(50);
        AIplayer.setPosition(Gdx.graphics.getWidth()/2 - 200/2, Gdx.graphics.getHeight()/2 -20);
        AIplayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new AIPlayer(game));
                game.setScreen(new DifficultyDialog(game));
                dispose();
            }
        });
        stage.addActor(AIplayer);


        TextButton exit = new TextButton("EXIT", skin, "default");
        exit.setWidth(200);
        exit.setHeight(50);
        exit.setPosition(Gdx.graphics.getWidth()/2 - 200/2, Gdx.graphics.getHeight()/2 -180);
        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(exit);




        Gdx.input.setInputProcessor(stage);

    }




    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        stage.draw();
        game.batch.end();

 /*       if (Gdx.input.isTouched()) {
            game.setScreen(new SinglePlayer(game));
            dispose();
        }
*/
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

    //Rest of class still omitted...

}
