package AI.utils;

/**
 * Created by Dorian on 14-May-16.
 * MiniGolfv1_2
 */
public class MoveCommand{
    public int xDifference;
    public int yDifference;
    public double score;

    public MoveCommand(int xDifference, int yDifference, double score) {
        this.xDifference = xDifference;
        this.yDifference = yDifference;
        this.score = score;
    }

    @Override
    public String toString() {
        return "MoveCommand{" +
                "xDifference=" + xDifference +
                ", yDifference=" + yDifference +
                ", score=" + score +
                '}';
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


}
