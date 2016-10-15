package entity.predefined;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static util.Util.*;


public class LightSourceManager {

    private final static int[] LIGHT_SOURCES = new int[] { GL_LIGHT0, GL_LIGHT1, GL_LIGHT2, GL_LIGHT3, GL_LIGHT4, GL_LIGHT5, GL_LIGHT6, GL_LIGHT7 };
    public static List<LightSource> lightSources = new ArrayList<LightSource>();
    public static float[] lightIntensities = new float[8];

    public static void addLightSource(LightSource lightSource) {
        if (lightSources.size() < LIGHT_SOURCES.length) {
            lightSources.add(lightSource);
            rebuildListIntensityList();
        }
        else {
            throw new IllegalStateException("No more light sources possible.");
        }
    }

    public static void removeLightSource(LightSource lightSource) {
        glDisable(getLightSourceId(lightSource));
        lightSources.remove(lightSource);
        rebuildListIntensityList();
    }

    public static int getLightSourceId(LightSource lightSource) {
        return LIGHT_SOURCES[lightSources.indexOf(lightSource)];
    }

    public static void positionLights() {
        for (LightSource s : lightSources) {
            int lightId = getLightSourceId(s);
            glEnable(lightId);
            glLight(lightId, GL_DIFFUSE, asFloatBuffer(asArray(s.diffuseColor)));
            glLight(lightId, GL_SPECULAR, asFloatBuffer(asArray(s.specularColor)));
            glLight(lightId, GL_POSITION, asFloatBuffer(asArray4(s.position)));
        }
    }

    public static void clearLightSources() {
        lightSources.clear();
    }

    public static void rebuildListIntensityList() {
        lightIntensities = new float[8];
        for (int i = 0; i < lightSources.size(); i++) {
            lightIntensities[i] = lightSources.get(i).getLightIntensity();
        }
    }

}