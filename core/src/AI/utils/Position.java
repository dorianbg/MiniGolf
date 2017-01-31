package AI.utils;

/**
 * Created by Dorian on 27-Apr-16.
 * MiniGolf
 */
public class Position {
    public int xCoord;
    public int yCoord;
    public int pathValue;

    public Position(int xCoord, int yCoord, int pathValue){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.pathValue = pathValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (xCoord != position.xCoord) return false;
        return yCoord == position.yCoord;

    }

    @Override
    public String toString() {
        return "Position{" +
                "yCoord=" + yCoord +
                ", xCoord=" + xCoord +
                '}';
    }

    @Override
    public int hashCode() {
        int result = xCoord;
        result = 31 * result + yCoord;
        return result;
    }

    public void updatePosition(int changeVertical, int changeHorizontal){
        this.xCoord += changeVertical;
        this.yCoord += changeHorizontal;
    }

}
