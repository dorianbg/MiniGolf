package AI.utils;

/**
 * Created by Dorian on 27-Apr-16.
 * MiniGolf
 */
public class Vector{
    public int xDir;
    public int yDir;

    public Vector(int x, int y){
        xDir = x;
        yDir = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector vector = (Vector) o;

        if (xDir != vector.xDir) return false;
        return yDir == vector.yDir;

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        int result = xDir;
        result = 31 * result + yDir;
        return result;
    }

    public String toString(){
        return (xDir + "," + yDir + " ");
    }
}
