package util;

import entity.Entity3D;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by s113958 on 19-4-2015.
 */
public class SerializableBraitenbergSimulation implements Serializable {

    public HashSet<Entity3D> entities;
    public double simulationSpeed;
    public double lightAttenuation;

    public SerializableBraitenbergSimulation(HashSet<Entity3D> entities, double simulationSpeed, double lightAttenuation) {
        this.entities = entities;
        this.simulationSpeed = simulationSpeed;
        this.lightAttenuation = lightAttenuation;
    }

}
