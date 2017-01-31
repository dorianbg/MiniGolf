package GameplayObjects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by Dorian on 14-Mar-16.
 * MiniGolf
 */
public class Obstacle extends ModelInstance {
    private float xPosition;
    private float yPosition;
    private float zPosition;
    private float xLength, yLength, zLength;
    private float xRotation = 0;
    private float yRotation = 0;
    private float zRotation = 0;
    private float rotationDegree;
    private BoundingBox bBox;

    public Obstacle(Model model, float xLength, float yLength, float zLength,
                    float xPos, float yPos, float zPos,
                    float xRot, float yRot, float zRot, float deg)
    {
        super(model, xPos, yPos, zPos);

        this.xLength = xLength;
        this.yLength = yLength;
        this.zLength = zLength;
        xPosition = xPos;
        yPosition = yPos;
        zPosition = zPos;
        this.xRotation = xRot;
        this.yRotation = yRot;
        this.zRotation = zRot;
        this.rotationDegree = deg;
    }

    public BoundingBox getBoundingBox()
    {
        if(bBox == null)
            calculateBoundingBox();
        return bBox;
    }

    public void calculateBoundingBox() {
        bBox = model.calculateBoundingBox(new BoundingBox());
        Vector3 min = bBox.getMin(new Vector3()).add(new Vector3(this.getPosition()));
        Vector3 max = bBox.getMax(new Vector3()).add(new Vector3(this.getPosition()));
        bBox.set(min,max);
    }

    public float getxLength() {return xLength;}

    public float getyLength() {return yLength;}

    public float getzLength() {return zLength;}

    public float[] getPosition() {return new float[] {xPosition,yPosition,zPosition};}

    public float[] getRotationAxis(){return new float[] {xRotation,yRotation,zRotation};}

    public float getRotationDegree(){ return rotationDegree;}


    public float getxPosition(){ return xPosition;}
    public float getyPosition(){ return yPosition;}
    public float getzPosition(){ return zPosition;}

    public float getxRotation(){ return xRotation;}
    public float getyRotation(){ return yRotation;}
    public float getzRotation(){ return zRotation;}

    }
