package entity;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by s113427 on 4-3-2015.
 */
public class CompositeEntity3D extends Entity3D {

    Set<Entity3D> entities;

    public CompositeEntity3D(Vector3f position, boolean pickable) {
        super(position, pickable);
        entities = new HashSet<Entity3D>();
    }

    public CompositeEntity3D(Vector3f position) {
        this(position, true);
    }

    public void addEntity(Entity3D entity) {
        entities.add(entity);
    }

    public void addEntities(Collection entities) {
        this.entities.addAll(entities);
    }

    public void removeEntity(Entity3D entity) {
        entities.remove(entity);
    }

    @Override
    public void reload() {
        Entity3D.entityIdMap.put(this.id, this);
        this.rotationFloatBuffer = BufferUtils.createFloatBuffer(16);
        this.setRotation(this.rotation);

        for (Entity3D e : this.entities) {
            e.reload();
        }
    }

    @Override
    protected void draw() {
        for (Entity3D entity : entities) {
            entity.render();
        }
    }

}
