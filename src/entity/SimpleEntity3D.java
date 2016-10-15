package entity;

import entity.predefined.BraitenBergVehicle;
import entity.predefined.LightSourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static util.Util.asArray;
import static util.Util.asFloatBuffer;

/**
 * Created by s113427 on 4-3-2015.
 */
public class SimpleEntity3D extends Entity3D {

    public transient Model model;
    public String modelName;
    public Material material;

    public SimpleEntity3D(String modelName, Vector3f position, boolean pickable) {
        super(position, pickable);
        setModel(modelName);
        material = new Material();
    }

    public SimpleEntity3D(String modelName, Vector3f position) {
        this(modelName, position, true);
    }

    private void setModel(String modelName) {
        this.modelName = modelName;

        this.reloadModel();
    }

    @Override
    public void reload() {
        Entity3D.entityIdMap.put(this.id, this);
        this.rotationFloatBuffer = BufferUtils.createFloatBuffer(16);
        this.setRotation(this.rotation);
    }

    private void reloadModel() {
        try {
            model = Model.fromObj(modelName);
            model.createVBOs();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw() {
        if (model != null) {
            glBindBuffer(GL_ARRAY_BUFFER, model.vertexBufferId);
            glVertexPointer(3, GL_FLOAT, 0, 0L);

            if (glGetInteger(GL_RENDER_MODE) == GL_RENDER) {
                int curProg = glGetInteger(GL_CURRENT_PROGRAM);

                int shadeLoc = glGetUniformLocation(curProg, "shade");
                glUniform1i(shadeLoc, (doShade) ? 1 : 0);

                int numLightsLoc = glGetUniformLocation(curProg, "numLights");
                glUniform1i(numLightsLoc, LightSourceManager.lightSources.size());

                int colorLoc = glGetUniformLocation(curProg, "baseColor");
                glUniform4(colorLoc, asFloatBuffer(asArray(material.color)));

                int matDiffuseLoc = glGetUniformLocation(curProg, "matDiffuse");
                glUniform4(matDiffuseLoc, asFloatBuffer(asArray(material.diffuseColor)));

                int matSpecularLoc = glGetUniformLocation(curProg, "matSpecular");
                glUniform4(matSpecularLoc, asFloatBuffer(asArray(material.specularColor)));

                int shininessLoc = glGetUniformLocation(curProg, "shininess");
                glUniform1f(shininessLoc, material.shininess);

                int attenuationLoc = glGetUniformLocation(curProg, "lightAttenuation");
                glUniform1f(attenuationLoc, (float)BraitenBergVehicle.LIGHT_ATTENUATION);

                FloatBuffer lightIntensities = BufferUtils.createFloatBuffer(LightSourceManager.lightIntensities.length);
                lightIntensities.put(LightSourceManager.lightIntensities);
                lightIntensities.rewind();
                int lightIntensitiesLoc = glGetUniformLocation(curProg, "lightIntensities");
                glUniform1(lightIntensitiesLoc, lightIntensities);

                glBindBuffer(GL_ARRAY_BUFFER, model.normalBufferId);
                glNormalPointer(GL_FLOAT, 0, 0L);
            }

            glDrawArrays(GL_TRIANGLES, 0, model.getFaces().size() * 3);
        }
        else {
            if (modelName != null) {
                reloadModel();
            }
        }
    }
}
