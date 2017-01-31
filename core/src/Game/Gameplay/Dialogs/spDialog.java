package Game.Gameplay.Dialogs;

import CourseParser.PathUtil;
import Game.Gameplay.MainMenuScreen;
import Game.Gameplay.MiniGolfExecutor;
import Game.Gameplay.SinglePlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Dorian on 17-May-16.
 * MiniGolfv1_2
 */
public class spDialog implements Screen{
    protected MiniGolfExecutor game;
    private Stage stage;
    private int hits;

    public spDialog (final MiniGolfExecutor game, int hits){
        this.game = game;
        this.hits = hits;
        initializeSPdialog();
    }

    public void initializeSPdialog(){
        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal( PathUtil.assetsPath()+"uiskin.json"));

        Label textButton = new Label("You won with " + hits + " hits", skin, "default");
        textButton.setWidth(200);
        textButton.setHeight(50);
        textButton.setPosition(Gdx.graphics.getWidth()/2 - 120, Gdx.graphics.getHeight()/2 + 100 );
        stage.addActor(textButton);

        TextButton easyButton = new TextButton("Play again", skin, "default");
        easyButton.setWidth(100);
        easyButton.setHeight(50);
        easyButton.setPosition(Gdx.graphics.getWidth()/2 - 120, Gdx.graphics.getHeight()/2 );
        easyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SinglePlayer(game));
                dispose();
            }
        });
        stage.addActor(easyButton);

        TextButton mediumButton = new TextButton("Main menu", skin, "default");
        mediumButton.setWidth(100);
        mediumButton.setHeight(50);
        mediumButton.setPosition(Gdx.graphics.getWidth()/2 - 20, Gdx.graphics.getHeight()/2 );
        mediumButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
        stage.addActor(mediumButton);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.draw();
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
}
