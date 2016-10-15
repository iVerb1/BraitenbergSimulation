package entity.predefined;

import entity.SimpleEntity3D;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by iVerb on 14-4-2015.
 */
public class LightSource extends SimpleEntity3D {

    public Vector4f diffuseColor = new Vector4f(0.8f, 0.8f, 0.8f, 1f);
    public Vector4f specularColor = new Vector4f(0.6f, 0.6f, 0.6f, 1f);

    private float lightIntensity = 1f;

    public LightSource(String modelName, Vector3f position, boolean pickable) {
        super(modelName, position, pickable);

        doShade = false;
        material.color = new Vector4f(1f, 1f, 1f, 1f);
        LightSourceManager.addLightSource(this);
    }

    public LightSource(Vector3f position) {
        this("sphere", position, true);
    }

    public void setLightIntensity(float lightIntensity) {
        this.lightIntensity = lightIntensity;
        LightSourceManager.rebuildListIntensityList();
    }

    public float getLightIntensity() {
        return this.lightIntensity;
    }

    @Override
    public void delete() {
        super.delete();
        LightSourceManager.removeLightSource(this);
    }

}
