package util;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iVerb on 18-3-2015.
 */
public class Util {

    public static float[] asArray3(Vector3f v) {
        return new float[] {v.x, v.y, v.z};
    }

    public static float[] asArray4(Vector3f v) {
        return new float[] {v.x, v.y, v.z, 1f};
    }

    public static float[] asArray(Vector4f v) {
        return new float[] {v.x, v.y, v.z, v.w};
    }

    public static FloatBuffer asFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }

    public static Vector3f cloneVector3f(Vector3f v) {
        return new Vector3f(v.x, v.y, v.z);
    }

    public static List<Vector3f> cloneVector3fList(List<Vector3f> vectors) {
        List<Vector3f> result = new ArrayList<Vector3f>();
        for (Vector3f v : vectors) {
            result.add(cloneVector3f(v));
        }
        return result;
    }

    public static List<?> cloneList(List<? extends Cloneable> list) {
        List result = new ArrayList();
        for (Cloneable c : list) {
            result.add(c.clone());
        }
        return result;
    }

    public static void rotateVector3f(Vector3f v, Matrix4f m, Vector3f dest) {
        float x = (m.m00 * v.x) + (m.m10 * v.y) + (m.m20 * v.z);
        float y = (m.m01 * v.x) + (m.m11 * v.y) + (m.m21 * v.z);
        float z = (m.m02 * v.x) + (m.m12 * v.y) + (m.m22 * v.z);

        dest.set(x, y, z);
    }

    public static Vector3f intersectHorizontalPlane (float[] origin, float[] direction, float y) {
        if (direction[1] == 0)
            return null;

        float t = (y - origin[1])/direction[1];

        return new Vector3f(origin[0] + t*direction[0], y, origin[2] + t*direction[2]);
    }

}
