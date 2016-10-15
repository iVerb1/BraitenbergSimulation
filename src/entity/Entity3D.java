package entity;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by s113427 on 4-3-2015.
 */
public abstract class Entity3D implements Serializable {

    public static int idGen = 1;
    protected static Map<Integer, Entity3D> entityIdMap = new HashMap<Integer, Entity3D>();
    public static HashSet<Entity3D> renderSet = new HashSet<Entity3D>();

    public int id;
    public boolean pickable;

    public Vector3f position;
    public float speed = 0f;
    public Vector3f heading = new Vector3f(0f, 0f, -1f);

    public float scalingFactor = 1f;
    public Matrix4f rotation;
    protected transient FloatBuffer rotationFloatBuffer;
    public boolean doShade = true;

    protected boolean picked = false;

    public static void clearIdMap() {
        Entity3D.entityIdMap.clear();
    }

    public void reload() {
        Entity3D.entityIdMap.put(this.id, this);
        this.rotationFloatBuffer = BufferUtils.createFloatBuffer(16);
        this.setRotation(this.rotation);
    }

    public Entity3D(Vector3f position, boolean pickable) {
        this.id = Entity3D.idGen++;
        this.pickable = pickable;
        Entity3D.entityIdMap.put(this.id, this);

        this.position = position;

        this.rotationFloatBuffer = BufferUtils.createFloatBuffer(16);
        this.rotation = new Matrix4f();
        this.setRotation(this.rotation);
    }

    public Entity3D(Vector3f position) {
        this(position, true);
    }

    public static boolean exists(int id) {
        return Entity3D.entityIdMap.containsKey(id);
    }

    public static Entity3D get(int id) {
        return Entity3D.entityIdMap.get(id);
    }

    public static Collection<Entity3D> getEntities() {
        return Entity3D.entityIdMap.values();
    }

    public void delete() {
        Entity3D.entityIdMap.remove(this.id);
        renderSet.remove(this);
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public void resetHeading() {
        this.heading = new Vector3f(0f, 0f, -1f);
    }

    public void update(float delta, float timeScale) {
        position.translate(
                heading.x * speed * timeScale * delta,
                heading.y * speed * timeScale * delta,
                heading.z * speed * timeScale * delta
        );
    }

    public void setRotation(Matrix4f rotation) {
        this.rotation = rotation;
        this.rotationFloatBuffer.rewind();
        rotation.store(this.rotationFloatBuffer);
        this.rotationFloatBuffer.flip();
    }

    public void render() {
        glPushMatrix();

        glTranslatef(position.x, position.y, position.z);
        glScalef(scalingFactor, scalingFactor, scalingFactor);
        glMultMatrix(rotationFloatBuffer);
        draw();

        glPopMatrix();
    }

    public void translate(Vector3f d) {
        Vector3f.add(this.position, d, this.position);
    }

    abstract protected void draw();

}
