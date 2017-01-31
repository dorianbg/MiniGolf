package PhysicsEngine;

import GameplayObjects.GolfBall;
import com.badlogic.gdx.math.Vector3;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

//TODO: Documentation

public class CollisionHandler {

    Vector3D normal, interPoint;
    GolfBall ball;

    /** Constructor for a collision handling case
     *
     * @param normal Normal of the collision surface.
     * @param intersectionPoint The point at which the ball and another obstacle are colliding.
     * @param ball The ball that is colliding.
     */
    public CollisionHandler(Vector3D normal, Vector3D intersectionPoint, GolfBall ball)
    {
        this.normal = normal;
        interPoint = intersectionPoint;
        this.ball = ball;
        getNewDirection();
    }

    private void getNewDirection()
    {
        //Change vectors to Vector3's to use their logic and to make input to GolfBall easier
        //Normal of the surface n
        Vector3 n = new Vector3(
                (float)normal.getX(),
                (float)normal.getY(),
                (float)normal.getZ());
        n.nor();
        //The collision point between the GolfBall and the surface
        Vector3 intPoint = new Vector3(
                (float)interPoint.getX(),
                (float)interPoint.getY(),
                (float)interPoint.getZ());
        //Ball Position Pb
        Vector3 Pb = ball.getPosition().cpy();
        //Ball Direction Db
        Vector3 Db = ball.getDirection().cpy();
        //The component of Db to the direction of n
        Vector3 a = n.cpy().scl(Db.dot(n));
        a.scl(2);
        //Direction from Pb to "New position"
        Vector3 b = Db.scl(2).sub(a);
        //Reset the Db to ball direction
        Db = ball.getDirection().cpy();
        //"New position" of the ball. Used to calculate the new direction vector
        Vector3 Pn = Pb.add(b);
        //Reset the Pb to old ball position
        Pb = ball.getPosition().cpy();
        //The new direction vector for the ball
        Vector3 newDirection = Pn.sub(Pb.add(Db));


        n.scl(-Db.dot(n));
        n.nor();
        n.scl((float)ball.getRadius());
        ball.setDirection(newDirection);
        ball.setPosition(intPoint.add(n));

    }
}
