package entity.predefined;

import entity.CompositeEntity3D;
import entity.Entity3D;
import entity.SimpleEntity3D;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import org.apache.commons.collections.buffer.*;
import util.Util;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_CURRENT_PROGRAM;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Created by s113427 on 4-3-2015.
 */
public class BraitenBergVehicle extends CompositeEntity3D {

    public SimpleEntity3D body;
    private SimpleEntity3D[] wheels;

    public boolean MOVE_TOWARDS_LIGHT = true;
    public float TURNING_FORCE = 0.3f;
    private LightSensor lightSensorLeft;
    private LightSensor lightSensorRight;

    public static double LIGHT_ATTENUATION = 1;

    private CircularFifoBuffer trail;
    private int NUM_SAMPLES = 750;


    public BraitenBergVehicle(Vector3f position) {
        super(position);

        SimpleEntity3D w1 = new SimpleEntity3D("car_wheel", new Vector3f(1.85f, 0f, 1.85f));
        SimpleEntity3D w2 = new SimpleEntity3D("car_wheel", new Vector3f(-1.85f, 0f, 1.85f));
        SimpleEntity3D w3 = new SimpleEntity3D("car_wheel", new Vector3f(1.85f, 0f, -1.85f));
        SimpleEntity3D w4 = new SimpleEntity3D("car_wheel", new Vector3f(-1.85f, 0f, -1.85f));
        wheels = new SimpleEntity3D[] {w1, w2, w3, w4};

        lightSensorLeft = new LightSensor(new Vector3f(-1.25f, 0.65f, -2f), new Vector3f(-1f, 0f, -1f));
        lightSensorRight = new LightSensor(new Vector3f(1.25f, 0.65f, -2f), new Vector3f(1f, 0f, -1f));

        body = new SimpleEntity3D("car_body", new Vector3f(0f, 0f, 0f));
        body.material.color = new Vector4f(1f, 0f, 0f, 1f);

        addEntity(body);
        addEntities(Arrays.asList(wheels));
        addEntity(lightSensorLeft);
        addEntity(lightSensorRight);

        speed = 3f;
        trail = new CircularFifoBuffer(NUM_SAMPLES);
    }

    public void clearTrail() {
        trail.clear();
    }

    @Override
    public void update(float delta, float timeScale) {
        if (!this.picked) {
            super.update(delta, timeScale);
            rotateWheels(delta, timeScale);
            senseLight(delta, timeScale);
            trail.add(Util.cloneVector3f(position));
        }
    }

    private void rotateWheels(float delta, float timeScale) {
        for (Entity3D wheel : wheels) {
            Matrix4f wheelRotation = wheel.rotation;
            float angle = -(delta * speed * timeScale) % (float)Math.PI;
            wheelRotation.rotate(angle, new Vector3f(1f, 0f, 0f));
            wheel.setRotation(wheelRotation);
        }
    }

    private void senseLight(float delta, float timeScale) {
        float lightIntensityLeft = 0f;
        float lightIntensityRight = 0f;
        for (LightSource lightSource : LightSourceManager.lightSources) {
            lightIntensityLeft += getLightIntensity(lightSensorLeft, lightSource);
            lightIntensityRight += getLightIntensity(lightSensorRight, lightSource);
        }

        if (!MOVE_TOWARDS_LIGHT) {
            float tempLightIntensityLeft = lightIntensityLeft;
            lightIntensityLeft = lightIntensityRight;
            lightIntensityRight = tempLightIntensityLeft;
        }

        //rotating heading and model by the same angle
        float rotationAngle = (delta * timeScale * TURNING_FORCE * (lightIntensityLeft - lightIntensityRight)) % (float)Math.PI;

        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotate(rotationAngle, new Vector3f(0f, 1f, 0f));

        Matrix4f newRotation = new Matrix4f();
        Matrix4f.mul(rotationMatrix, rotation, newRotation);
        setRotation(newRotation);

        Util.rotateVector3f(heading, rotationMatrix, heading);
    }

    private float getLightIntensity(LightSensor lightSensor, LightSource lightSource) {
        Vector3f sensorToLight = new Vector3f();
        Vector3f sensorPositionInWorld = new Vector3f();
        Vector3f sensorDirectionInWorld = new Vector3f();

        Vector3f.add(position, lightSensor.position, sensorPositionInWorld);
        Vector3f.sub(lightSource.position, sensorPositionInWorld, sensorToLight);

        float lightIntensity = lightSource.getLightIntensity() * (float)Math.max(0, -(Math.pow(sensorToLight.length(), 2) * (0.001f * LIGHT_ATTENUATION)) + 1);

        Util.rotateVector3f(lightSensor.direction, rotation, sensorDirectionInWorld);
        sensorToLight.normalise();
        sensorDirectionInWorld.normalise();
        return Math.max(0, Math.min(Vector3f.dot(sensorDirectionInWorld, sensorToLight) * lightIntensity, 1));
    }

    @Override
    public void render() {
        super.render();

        Vector4f color = body.material.color;
        int shaderProgram = glGetInteger(GL_CURRENT_PROGRAM);

        glUseProgram(0);
        glLineWidth(4.5f);
        glColor3f(color.x, color.y, color.z);
        glBegin(GL_LINE_STRIP);
        for (Object o : trail) {
            Vector3f point = (Vector3f) o;
            glVertex3f(point.x, point.y, point.z);
        }
        glEnd();
        glUseProgram(shaderProgram);
    }

}
