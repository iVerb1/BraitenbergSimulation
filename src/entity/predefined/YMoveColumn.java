package entity.predefined;

import entity.Entity3D;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class YMoveColumn extends Entity3D {

    private Entity3D entity;

    public YMoveColumn() {
        super(new Vector3f());
    }

    public Entity3D getEntity() {
        return this.entity;
    }

    public void setEntity(Entity3D entity) {
        this.entity = entity;
    }

    public void updatePosition() {
        if (this.entity == null)
            return;

        this.position.x = this.entity.position.x;
        this.position.y = this.entity.position.y;
        this.position.z = this.entity.position.z;
    }

    @Override
    protected void draw() {
        if (this.entity == null)
            return;

        glBegin(GL_QUADS);                // Begin drawing the color cube with 6 quads
        // Top face (y = 0.05f)
        // Define vertices in counter-clockwise (CCW) order with normal pointing out
        glColor3f(0.0f, 0.05f, 0.0f);     // Green
        glVertex3f( 0.05f, 200.0f, -0.05f);
        glVertex3f(-0.05f, 200.0f, -0.05f);
        glVertex3f(-0.05f, 200.0f,  0.05f);
        glVertex3f( 0.05f, 200.0f,  0.05f);

        // Bottom face (y = -0.05f)
        glColor3f(0.0f, 0.05f, 0.0f);     // Orange
        glVertex3f( 0.05f, -200.0f,  0.05f);
        glVertex3f(-0.05f, -200.0f,  0.05f);
        glVertex3f(-0.05f, -200.0f, -0.05f);
        glVertex3f( 0.05f, -200.0f, -0.05f);

        // Front face  (z = 0.05f)
        glColor3f(0.0f, 0.05f, 0.0f);     // Red
        glVertex3f( 0.05f,  200.0f, 0.05f);
        glVertex3f(-0.05f,  200.0f, 0.05f);
        glVertex3f(-0.05f, -200.0f, 0.05f);
        glVertex3f( 0.05f, -200.0f, 0.05f);

        // Back face (z = -0.05f)
        glColor3f(0.0f, 0.05f, 0.0f);     // Yellow
        glVertex3f( 0.05f, -200.0f, -0.05f);
        glVertex3f(-0.05f, -200.0f, -0.05f);
        glVertex3f(-0.05f,  200.0f, -0.05f);
        glVertex3f( 0.05f,  200.0f, -0.05f);

        // Left face (x = -0.05f)
        glColor3f(0.0f, 0.05f, 0.0f);     // Blue
        glVertex3f(-0.05f,  200.0f,  0.05f);
        glVertex3f(-0.05f,  200.0f, -0.05f);
        glVertex3f(-0.05f, -200.0f, -0.05f);
        glVertex3f(-0.05f, -200.0f,  0.05f);

        // Right face (x = 0.05f)
        glColor3f(0.05f, 0.05f, 0.0f);     // Magenta
        glVertex3f(0.05f,  200.0f, -0.05f);
        glVertex3f(0.05f,  200.0f,  0.05f);
        glVertex3f(0.05f, -200.0f,  0.05f);
        glVertex3f(0.05f, -200.0f, -0.05f);
        glEnd();  // End of drawing color-cube
    }
}
