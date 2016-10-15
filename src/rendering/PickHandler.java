package rendering;

import entity.Entity3D;
import entity.SimpleEntity3D;
import entity.predefined.BraitenBergVehicle;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import util.Util;

import java.util.Observable;

/**
 * Created by s113958 on 18-3-2015.
 */
public class PickHandler extends Observable {

    public Entity3D picked = null;
    public static Entity3D ent = null;

    public void click(int id) {
        this.clear();

        this.picked = Entity3D.get(id);
        PickHandler.ent = this.picked;

        if (this.picked != null) {
            this.picked.setPicked(true);
            setChanged();
            notifyObservers(this.picked);
            if (this.picked instanceof BraitenBergVehicle) {
                ((BraitenBergVehicle)this.picked).clearTrail();
            }
        }
    }

    public void clear() {
        if (this.picked != null)
            this.picked.setPicked(false);

        this.picked = null;
        PickHandler.ent = null;
    }

    public void moveHorizontal(float[] origin, float[] direction) {

        if (this.picked == null)
            return;

        if (direction[1] == 0)
            return;

        float t = (this.picked.position.getY() - origin[1])/direction[1];

        this.picked.position.setX(origin[0] + t*direction[0]);
        this.picked.position.setZ(origin[2] + t*direction[2]);
    }

    public void moveVertical(float[] origin, float[] direction, float[] horizon) {
        if (this.picked == null)
            return;

        Vector3f v0 = new Vector3f(origin[0], origin[1], origin[2]);
        Vector3f p0 = this.picked.position;

        Vector3f w = new Vector3f();
        Vector3f.sub(v0, p0, w);

        Vector3f u = new Vector3f(0,1,0);

        Vector3f n = new Vector3f();
        Vector3f.cross(new Vector3f(direction[0], direction[1], direction[2]), new Vector3f(horizon[0], horizon[1], horizon[2]), n);

        float s = (Vector3f.dot(n, w))/(Vector3f.dot(n,u));

        this.picked.position.x = p0.x + s*u.x;
        this.picked.position.y = p0.y + s*u.y;
        this.picked.position.z = p0.z + s*u.z;
    }

    public void rotate(float[] origin, float[] direction) {

        if (this.picked == null)
            return;

        if (direction[1] == 0)
            return;

        float t = (this.picked.position.getY() - origin[1])/direction[1];

        float x = origin[0] + t*direction[0];
        float z = origin[2] + t*direction[2];

        float dx;
        float dz;

        dx = x - this.picked.position.x;
        dz = z - this.picked.position.z;

        float degrees = (float)Math.toDegrees(Math.atan(dx / dz));

        if (Float.isNaN(degrees))
            return;

        if (dz < 0)
            degrees += 180;

        Matrix4f rotation = new Matrix4f();
        rotation.rotate((float) Math.toRadians(degrees), new Vector3f(0,1,0));

        this.picked.setRotation(rotation);
        this.picked.resetHeading();
        Util.rotateVector3f(this.picked.heading, rotation, this.picked.heading);
    }
}
