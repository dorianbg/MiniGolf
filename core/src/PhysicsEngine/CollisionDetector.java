package PhysicsEngine;

import GameplayObjects.GolfBall;
import GameplayObjects.Hole;
import GameplayObjects.Obstacle;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;

public class CollisionDetector
{

    private ArrayList<ModelInstance> listOfObjects;
    private GolfBall ball;
    private Obstacle previousCollision;
    private ArrayList<Command> cornerQueue = new ArrayList<>();

    //////////////////////////////////////////////
    /*///////////////////
     *///////////////////
    public float FRICTION_FACTOR = 0.985f;
    public float absorptionFactor = 0.1f;
    /*///////////////////
     *//////////////////

    private interface Command
    {
        public void execute();
        public void setN(Vector3D n);
        public void setIntPoint(Vector3D intPoint);
    }

    /** Generic constructor for collision detector
     *
     * @param listOfObjects List of object in the game world
     */
    public CollisionDetector(ArrayList<ModelInstance> listOfObjects)
    {
        this.listOfObjects = listOfObjects;
    }

    /** Executes the collision check for the given ball
     *
     * @param ball The golf ball for which the collisions are checked
     */
    public void check(GolfBall ball)
    {
        this.ball = ball;
        if(ball.getDirection().len() != 0)// && !collided)
            radiusCheck();
        for (Command command : cornerQueue){
            command.execute();
        }
        cornerQueue = new ArrayList<>();
    }

    private void simpleCheck(final Polygon[] polyList, final Obstacle o)
    {
        Vector3D ballPosition = new Vector3D(ball.getPosition().x,ball.getPosition().y,ball.getPosition().z);
        Vector3D intersectionPoint = null;
        double collisionDistance = 100;
        Polygon collisionPolygon = null;

        for(Polygon p : polyList)
        {
            Line collisionLine = new Line(ballPosition,ballPosition.add(p.getPlane().getNormal()));
            Vector3D collisionIntersection = p.getPlane().intersection(collisionLine);
            if(onSquareCheck(p,collisionIntersection) &&
                    ballPosition.distance(collisionIntersection) < collisionDistance)
            {
                intersectionPoint = collisionIntersection;
                collisionDistance = ballPosition.distance(collisionIntersection);
                collisionPolygon = p;
            }
        }
        Vector3D normal = null;
        //Check that collision is outside the obstacle and not inside to avoid some corner issues
        boolean collision = true;
        if(collisionPolygon != null)
        {
            Vector3 normal3 = new Vector3(
                    (float) collisionPolygon.getPlane().getNormal().getX(),
                    (float) collisionPolygon.getPlane().getNormal().getY(),
                    (float) collisionPolygon.getPlane().getNormal().getZ()
            );

            Vector3 intPoint = new Vector3(
                    (float) intersectionPoint.getX(),
                    (float) intersectionPoint.getY(),
                    (float) intersectionPoint.getZ()
            );

            Vector3 obstaclePosition = new Vector3(
                    (float) o.getPosition()[0],
                    (float) o.getPosition()[1],
                    (float) o.getPosition()[2]
            );

            normal = collisionPolygon.getPlane().getNormal();

            if (normal3.scl(normal3.dot(ball.getDirection())).dot(intPoint.sub(obstaclePosition)) > 0)
            {
                collision = false;
                if(ballPosition.distance(intersectionPoint) < ball.getRadius() + 0.003f)
                {
                    normal3 = new Vector3(
                            (float) collisionPolygon.getPlane().getNormal().getX(),
                            (float) collisionPolygon.getPlane().getNormal().getY(),
                            (float) collisionPolygon.getPlane().getNormal().getZ()
                    );
                    normal3.scl(normal3.dot(ball.getDirection()));
                    normal3.nor();
                    normal3.scl((float) ball.getRadius()+0.003f);
                    intPoint = new Vector3(
                            (float) intersectionPoint.getX(),
                            (float) intersectionPoint.getY(),
                            (float) intersectionPoint.getZ()
                    );
                    ball.setPosition(intPoint.add(normal3));
                }
            }
        }

        else
        {
            Vector3D[] collisionProperties = cornerCheck(polyList, o);
            if(collisionProperties != null)
            {
                cornerQueue.add(new Command()
                {
                    Polygon[] polys = polyList;
                    Obstacle obstacle = o;
                    Vector3D n;
                    Vector3D intPoint;

                    @Override
                    public void execute()
                    {
                        if(cornerCheck(polys, obstacle) != null)
                        {
                            //ball.getDirection().scl(1 - absorptionFactor);
                            CollisionHandler handler = new CollisionHandler(n, intPoint, ball);
                        }
                    }

                    @Override
                    public void setN(Vector3D n)
                    {
                        this.n = n;
                    }

                    @Override
                    public void setIntPoint(Vector3D intPoint)
                    {
                        this.intPoint = intPoint;
                    }
                });
                cornerQueue.get(cornerQueue.size()-1).setN(collisionProperties[0]);
                cornerQueue.get(cornerQueue.size()-1).setIntPoint(collisionProperties[1]);
            }
        }



        //Collide
        if(collisionPolygon != null && ballPosition.distance(intersectionPoint) < ball.getRadius() + 0.003f && collision)// && !collided)
        {
            if(o == previousCollision)
            {
                ball.getDirection().y *= (1 - absorptionFactor);
                ball.getDirection().x *= FRICTION_FACTOR;
                ball.getDirection().z *= FRICTION_FACTOR;
            }
            else
            {
                ball.getDirection().scl(1 - absorptionFactor);
            }
            previousCollision = o;
            CollisionHandler handler = new CollisionHandler(normal, intersectionPoint, ball);
        }
    }

    private class Pair
    {
        Vector3D[] pair = null;
        public Pair(Vector3D pair1, Vector3D pair2) {pair = new Vector3D[]{pair1, pair2};}
        public Vector3D[] getPair() {return pair;}
        public Line getLine() {return new Line(pair[0],pair[1]);}
    }

    private Vector3D[] cornerCheck(Polygon[] polyList, Obstacle o)
    {
        Vector3D ballPosition = new Vector3D(ball.getPosition().x,ball.getPosition().y,ball.getPosition().z);
        Vector3D intersectionPoint = null;
        Vector3D normal = null;
        //START CORNER CHECK

        //Construct all edge lines for the obstacle
        ArrayList<Pair> lineList = new ArrayList<>();
        //4 from the top polygon:
        lineList.add(new Pair(polyList[4].a,polyList[4].b));
        lineList.add(new Pair(polyList[4].a,polyList[4].c));
        lineList.add(new Pair(polyList[4].b,polyList[4].d));
        lineList.add(new Pair(polyList[4].c,polyList[4].d));
        //4 from the bottom polygon:
        lineList.add(new Pair(polyList[5].a,polyList[5].b));
        lineList.add(new Pair(polyList[5].a,polyList[5].c));
        lineList.add(new Pair(polyList[5].b,polyList[5].d));
        lineList.add(new Pair(polyList[5].c,polyList[5].d));
        //Connections with the points
        lineList.add(new Pair(polyList[4].a,polyList[5].a));
        lineList.add(new Pair(polyList[4].b,polyList[5].b));
        lineList.add(new Pair(polyList[4].c,polyList[5].c));
        lineList.add(new Pair(polyList[4].d,polyList[5].d));


        //Check distance to all (loop) lines, if < ball radius
        for(Pair p : lineList)
        {
            //  create plane from the line vector and ball point
            Plane plane = new Plane(ballPosition,p.getLine().getDirection());
            //  create collision point from the intersection point of the plane and the line
            Vector3D cPoint = plane.intersection(p.getLine());
            //  Check that the collision point is in the line segment with a.dst(b) > a.dst(cPoint)
            if(ballPosition.distance(cPoint) < ball.getRadius() &&
                    p.getPair()[0].distance(p.getPair()[1]) > p.getPair()[0].distance(cPoint) &&
                    p.getPair()[1].distance(p.getPair()[0]) > p.getPair()[1].distance(cPoint))
            {
                System.out.println(p.getLine().getDirection());
                normal = ballPosition.subtract(cPoint);
                System.out.println(normal);
                intersectionPoint = cPoint;
            }
        }
        if(intersectionPoint != null && ballPosition.distance(intersectionPoint) < ball.getRadius() + 0.003f)// && !collided)
        {
            if(normal != null)
            {
                Vector3D[] returnVector;
                returnVector = new Vector3D[]{normal,intersectionPoint};
                return returnVector;
            }
        }
        return null;
    }





    private void radiusCheck()
    {
        double checkRadius = 0;
        for(Object o : listOfObjects)
        {
            if(o instanceof Obstacle)
            {
                double obstacleDiagonal = Math.sqrt(
                        Math.pow(((Obstacle) o).getxLength(),2) +
                                Math.pow(((Obstacle) o).getyLength(),2) +
                                Math.pow(((Obstacle) o).getzLength(),2))/(double)2;
                if(obstacleDiagonal > checkRadius)
                    checkRadius = obstacleDiagonal;
                checkRadius += ball.getRadius();
                if(ball.getPosition().dst(new Vector3(
                        ((Obstacle) o).getPosition()[0],
                        ((Obstacle) o).getPosition()[1],
                        ((Obstacle) o).getPosition()[2]
                )) < checkRadius)
                {
                    simpleCheck(createPolygons((Obstacle)o), (Obstacle) o);
                }
            }
            else if(o instanceof GolfBall && o != ball)
            {
                if(((GolfBall) o).getSolid() && ball.getSolid())
                {
                    if (ball.getPosition().dst(((GolfBall) o).getPosition()) < 2 * ball.getRadius() + 0.001f)
                    {
                        //Collision is true
                        System.out.println(ball.getPosition() + "\n" + ((GolfBall) o).getPosition() + "\n" + ((GolfBall) o).getPosition().cpy().sub(ball.getPosition()));
                        Vector3D normal = new Vector3D(
                                ((GolfBall) o).getPosition().cpy().sub(ball.getPosition()).x,
                                ((GolfBall) o).getPosition().cpy().sub(ball.getPosition()).y,
                                ((GolfBall) o).getPosition().cpy().sub(ball.getPosition()).z
                        );
                        Vector3D intersectionPoint = new Vector3D(
                                ball.getPosition().x,
                                ball.getPosition().y,
                                ball.getPosition().z
                        );
                        intersectionPoint.add(normal.scalarMultiply(0.5));
                        normal = normal.normalize();
                        float momentum = ((GolfBall) o).getDirection().len() + ball.getDirection().len();
                        ((GolfBall) o).setDirection(new Vector3(
                                (float) normal.scalarMultiply(momentum*0.5).getX(),
                                (float) normal.scalarMultiply(momentum*0.5).getY(),
                                (float) normal.scalarMultiply(momentum*0.5).getZ()
                        ));
                        ball.direction.scl(0.5f);

                        //TODO: Make handler updatable
                        CollisionHandler handler = new CollisionHandler(
                                normal,
                                intersectionPoint,
                                ball);
                    }
                }
            }
            else if(o instanceof Hole) {
                if(((Hole) o).getPosition().dst(ball.getPosition()) < ((Hole) o).getRadius() * 1.75f ) {
                    if((ball.getDirection().x + ball.getDirection().z) < 0.2f){
                        ball.setFinished();
                    }
                }
            }
        }
    }






    //Checks if the point is in the square defined by the polygon
    protected boolean onSquareCheck(Polygon polygon, Vector3D v3d)
    {
        Vector3D sideA = polygon.b.subtract(polygon.a);
        Vector3D sideB = polygon.c.subtract(polygon.a);
        if(sideA.dotProduct(polygon.a) <= sideA.dotProduct(v3d) &&
                sideA.dotProduct(v3d) <= sideA.dotProduct(polygon.b))
        {
            if(sideB.dotProduct(polygon.a) <= sideB.dotProduct(v3d) &&
                    sideB.dotProduct(v3d) <= sideB.dotProduct(polygon.c))
            {
                return true;
            }
        }
        return false;
    }

    private Polygon[] createPolygons(Obstacle obstacle)
    {
        Vector3[] edgeList = new Vector3[8];
        int counter = 0;
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 2; j++)
            {
                for(int k = 0; k < 2; k++)
                {
                    Vector3 edge = new Vector3();
                    if(i == 0)
                    {
                        edge.add(obstacle.getxLength()/2,0,0);
                    }
                    else
                    {
                        edge.sub(obstacle.getxLength()/2,0,0);
                    }
                    if(j == 0)
                    {
                        edge.add(0,obstacle.getyLength()/2,0);
                    }
                    else
                    {
                        edge.sub(0,obstacle.getyLength()/2,0);
                    }
                    if(k == 0)
                    {
                        edge.add(0,0,obstacle.getzLength()/2);
                    }
                    else
                    {
                        edge.sub(0,0,obstacle.getzLength()/2);
                    }
                    edgeList[counter] = edge;
                    counter++;
                }
            }
        }
        for(Vector3 v : edgeList)
        {
            v.rotate(obstacle.getRotationDegree(),
                    obstacle.getRotationAxis()[0],obstacle.getRotationAxis()[1],obstacle.getRotationAxis()[2]);
            v.add(obstacle.getPosition()[0],obstacle.getPosition()[1],obstacle.getPosition()[2]);
        }
        Polygon[] polygonList = new Polygon[6];
        polygonList[0] = new Polygon(edgeList[0],edgeList[1],edgeList[2],edgeList[3]); //East
        polygonList[0].side = "East";
        polygonList[1] = new Polygon(edgeList[4],edgeList[5],edgeList[6],edgeList[7]); //West
        polygonList[1].side = "West";
        polygonList[2] = new Polygon(edgeList[4],edgeList[0],edgeList[6],edgeList[2]); //South
        polygonList[2].side = "South";
        polygonList[3] = new Polygon(edgeList[5],edgeList[1],edgeList[7],edgeList[3]); //North
        polygonList[3].side = "North";
        polygonList[4] = new Polygon(edgeList[0],edgeList[1],edgeList[4],edgeList[5]); //Top
        polygonList[4].side = "Top";
        polygonList[5] = new Polygon(edgeList[2],edgeList[3],edgeList[6],edgeList[7]); //Bottom
        polygonList[5].side = "Bottom";
        return polygonList;
    }


}
