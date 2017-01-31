package PhysicsEngine;

import com.badlogic.gdx.math.Vector3;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


public class Polygon
{
    public Vector3D a,b,c,d;
    public String side = "";

    /** Constructor for square constructors.
     *
     * @param a Top right corner
     * @param b Top left corner
     * @param c Bottom right corner
     * @param d Bottom left corner
     */
    public Polygon(Vector3 a, Vector3 b, Vector3 c, Vector3 d)
    {
        this.a = new Vector3D(a.x,a.y,a.z);
        this.b = new Vector3D(b.x,b.y,b.z);
        this.c = new Vector3D(c.x,c.y,c.z);
        this.d = new Vector3D(d.x,d.y,d.z);
    }

    /** Getter for plane along which the polygon lies.
     * Point d isn't monitored for this and should be adjusted on construction.
     * @return Plane which contains a, b and c
     */
    public Plane getPlane()
    {
        return new Plane(a,b,c);
    }
}
