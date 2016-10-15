package entity.predefined;

import entity.SimpleEntity3D;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class LightSensor extends SimpleEntity3D {

    public Vector3f direction;

    public LightSensor(Vector3f position, Vector3f direction) {
        super("sphere", position);
        this.scalingFactor = 0.25f;
        this.material.color = new Vector4f(0.9f, 0.9f, 0.9f, 1f);

        direction.normalise();
        this.direction = direction;
    }

}