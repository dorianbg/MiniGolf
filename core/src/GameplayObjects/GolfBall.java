package GameplayObjects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.Random;

/**
 * Created by Dorian on 14-Mar-16.
 * MiniGolf
 */
public class GolfBall extends ModelInstance {

    private Vector3 position;
    public Vector3 direction;
    private float pressedX, pressedZ, releasedX, releasedZ, differenceX, differenceZ;
    private int  hits;
    private double force = 0;
    private double radius;
    private float yForce;
    private final float GRAVITATIONAL_CONSTANT = (float)0.95;
    private BoundingBox bBox, tmpBox;
    boolean gravity = false;
    private float relY = 25;
    final Vector3 gravityVector = new Vector3(0,-0.0015f,0);
    private boolean solid;
    private boolean finished;
    Random generator;
    //In degrees. The range will always exclude both ends. E.g. with 5f => range = (-5,5)
    public float directionNoisePercent;
    public float forceNoisePercent;
    public float directionNoise = 360f/100f * directionNoisePercent;
    public float forceNoise = 100f/forceNoisePercent;
    private Vector3 wind = new Vector3(0.0001f,0,0);



    public GolfBall(Model model, float x, float y, float z)
    {
        super(model, x, y+0.005f, z);
        generator = new Random();
        hits = 0;
        position = new Vector3(x,y,z);
        direction = new Vector3(0,0,0);
        bBox = model.calculateBoundingBox(new BoundingBox());
        radius = bBox.getWidth()/2f;
        setPosition(new Vector3(getPosition().x, (float)((getRadius()*2)+y), getPosition().z));
        solid = false;
    }



    public void setWind(Vector3 windVector) {wind = windVector;}

    public Vector3 getWind() {return wind;}

    public void setFinished(){
        finished = true;
    }

    public void setUnfinished(){
        finished = false;
    }

    public boolean getFinished(){
        return finished;
    }

    public void move(){
    }


    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getDirection() {return direction;}

    public void setRadius(double radius) {this.radius = radius;}

    public double getRadius() {return radius;}

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setPressed(float X, float Z)
    {
        pressedX = X;
        pressedZ = Z;
    }

    public void setReleased(float X, float Z)
    {
        releasedX = X;
        releasedZ = Z;
    }
    public boolean getSolid(){
        return solid;
    }

    public void hitBall()
    {
        if(!solid) solid = true;
        hits++;
        differenceX = -releasedX + pressedX;
        differenceZ = -releasedZ + pressedZ;
        direction = new Vector3(differenceX, 0,differenceZ);
        direction.scl(0.05f);

        if(direction.len() > radius*4)
            direction.nor().scl((float)radius*4);
    }

    public void hitBall(float differenceX, float differenceZ)
    {
        if(!solid) solid = true;
        hits++;
        direction = new Vector3(differenceX, 0,differenceZ);
        direction.scl(0.05f);
//                direction.scl(0.1f);

        if(direction.len() > radius*4)
            direction.nor().scl((float)radius*4);
    }

    public void hitBall(int differenceX, int differenceZ){
//        if(!solid) solid = true;
        hits++;
//        direction = new Vector3(differenceX,0f,differenceZ).nor().scl(0.0001f);
//        force = (Math.sqrt(differenceX*differenceX + differenceZ*differenceZ ));
        direction = new Vector3(-differenceX, 0,-differenceZ).scl(0.0001f);
        if(direction.len() > radius*4)
            direction.nor().scl((float)radius*4);
    }

    public void hitNoise(int differenceX, int differenceZ)
    {
        hits++;
        direction = new Vector3(-differenceX, 0,-differenceZ).scl(0.0001f);
        // this is first the direction noise
        float noise = generator.nextFloat() * directionNoise;
        if(generator.nextBoolean())
            noise = -noise;
        direction.rotate(new Vector3(0,1,0),noise);
        // this is the direction noise
        noise = generator.nextFloat() * direction.len() * forceNoise ;
        if(generator.nextBoolean())
            noise = -noise;
        direction.scl(1-noise);

        if(direction.len() > radius*4)
            direction.nor().scl((float)radius*4);
    }


    public void setSolid(boolean bool){ solid = bool;};
    public void setForceScalarSlider(float y){
        System.out.println("y = [" + y + "]");
        relY = y;
    }

    public float[] getPressed()
    {
        float[] pressed = new float[2];
        pressed[0] = pressedX;
        pressed[1] = pressedZ;
        return pressed;
    }

    public int getHits(){
        return hits;
    }

    public double getForce(){
        return force;
    }

    public Vector3 updatePosition()
    {
//        if(force>=10)force *= 0.985;
//        if(force<10)force = 0.0;
//        if(force > 200) force = 200;
        direction.scl(0.985f);
        //Gravity
        direction.add(gravityVector);
//        System.out.println("Ball direction components:\n" + direction.x + "\n" + direction.z);
        if(Math.abs(direction.x) < 0.0005 &&
                Math.abs(direction.z)  < 0.0005) {
//            System.out.println("Time to stop");
            direction.x = 0f;
            direction.z = 0f;
        }
        if(Math.abs(direction.x) > 0.4) direction.x = 0.4f;
        if(Math.abs(direction.z) > 0.4) direction.z = 0.4f;

        return direction.cpy().add(position);
    }



    public void setForce(double force){
        this.force = force;
    }

    public void setDirection(Vector3 newDir){
        direction.set(newDir.x, newDir.y, newDir.z);

    }

}
