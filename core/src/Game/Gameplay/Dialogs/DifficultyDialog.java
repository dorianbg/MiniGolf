package Game.Gameplay.Dialogs;

import CourseParser.PathUtil;
import Game.Gameplay.AIPlayer;
import Game.Gameplay.MiniGolfExecutor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Dorian on 17-May-16.
 * MiniGolfv1_2
 */
public class DifficultyDialog implements Screen{
    int computationTime;
    protected MiniGolfExecutor game;
    private Stage stage;

    public DifficultyDialog(final MiniGolfExecutor game){
        this.game = game;
        initializeDifficultyDialog();
    }

    public void initializeDifficultyDialog(){
        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal( PathUtil.assetsPath()+"uiskin.json"));
        TextButton easyButton = new TextButton("EASY", skin, "default");
        easyButton.setWidth(100);
        easyButton.setHeight(50);
        easyButton.setPosition(Gdx.graphics.getWidth()/2 - 150, Gdx.graphics.getHeight()/2 );
        easyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                computationTime = 1;
                game.setScreen(new AIPlayer(game,computationTime));
                dispose();
            }
        });
        stage.addActor(easyButton);

        TextButton mediumButton = new TextButton("MEDIUM", skin, "default");
        mediumButton.setWidth(100);
        mediumButton.setHeight(50);
        mediumButton.setPosition(Gdx.graphics.getWidth()/2 - 50, Gdx.graphics.getHeight()/2 );
        mediumButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                computationTime = 4;
                game.setScreen(new AIPlayer(game,computationTime));
                dispose();
            }
        });
        stage.addActor(mediumButton);

        TextButton hardButton = new TextButton("HARD", skin, "default");
        hardButton.setWidth(100);
        hardButton.setHeight(50);
        hardButton.setPosition(Gdx.graphics.getWidth()/2 + 50, Gdx.graphics.getHeight()/2 );
        hardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                computationTime = 10;
                game.setScreen(new AIPlayer(game,computationTime));
            }
        });
        stage.addActor(hardButton);

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
