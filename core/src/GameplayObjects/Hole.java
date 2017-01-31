package GameplayObjects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by Dorian on 14-Mar-16.
 * MiniGolf
 */
public class Hole extends ModelInstance {
    private float xPosition;
    private float yPosition;
    private float zPosition;
    private float radius;

    public Hole(Model model, float x, float y, float z) {
        super(model, x, y, z);
        xPosition = x;
        yPosition = y;
        zPosition = z;
        radius = calculateBoundingBox(new BoundingBox()).getWidth()/2;
    }

    public float getRadius() {return radius;}

    public float getxPosition() {
        return xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public float getzPosition() {
        return zPosition;
    }

    public Vector3 getPosition() {return new Vector3(xPosition,yPosition,zPosition);}

}
