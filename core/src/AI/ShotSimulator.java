package AI;

import GameplayObjects.GolfBall;
import PhysicsEngine.CollisionDetector;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Dorian on 13-May-16.
 * MiniGolfv1_2
 */
public class ShotSimulator {
    private GolfBall ball;
    private CollisionDetector collisionDetector;
    public boolean NOISE_ON;

    public ShotSimulator(GolfBall ball, CollisionDetector collisionDetector, boolean noise){
        this.ball = ball;
        this.collisionDetector = collisionDetector;
        this.NOISE_ON = noise;
    }

    public Vector3 simulateShot(int differenceX, int differenceZ){
        if(NOISE_ON == true){
            ball.hitNoise(differenceX,differenceZ);
        }
        else {
            ball.hitBall(differenceX,differenceZ);
        }
        while(Math.abs(ball.direction.x) > 0.0001f && Math.abs(ball.direction.z) > 0.0001f){
            moveBall();
        }
        return ball.getPosition();
    }

    public void moveBall(){
        Vector3 vector = ball.updatePosition();
        ball.transform.setToTranslation(vector.x, vector.y, vector.z);
        ball.setPosition(ball.transform.getTranslation(new Vector3()));
        collisionDetector.check(ball);
    }


}
