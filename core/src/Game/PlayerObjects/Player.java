package Game.PlayerObjects;

import GameplayObjects.GolfBall;

/**
 * Created by Daniel on 19.03.2016.
 */
public class Player {

    private String name;
    private int score;
    private GolfBall golfBall;

    public Player(){
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public GolfBall getGolfBall() {
        return golfBall;
    }

    public void setGolfBall(GolfBall golfBall) {
        this.golfBall = golfBall;
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }
}
