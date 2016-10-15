package rendering;

import entity.Entity3D;
import entity.predefined.BraitenBergVehicle;
import entity.predefined.LightSource;
import entity.predefined.LightSourceManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import util.Time;
import util.Util;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static rendering.ShaderUtils.loadShader;
import static rendering.ShaderUtils.loadShaderProgram;
import static util.Util.asFloatBuffer;


public class Camera {

    // [0.05, 0.4]
    private float MOUSE_SENSITIVITY = 0.2f;
    private float PITCH_MAX = 90;
    private float PITCH_MIN = -90;

    // units per second
    private float MOVEMENT_SPEED = 10f;
    private float MOVEMENT_SPEED_MULTIPLIER_FAST = 3f;
    private float MOVEMENT_SPEED_MULTIPLIER_SLOW = 0.3f;

    private int ACTION_KEY_MOVE_FORWARDS = Keyboard.KEY_W;
    private int ACTION_KEY_MOVE_BACKWARDS = Keyboard.KEY_S;
    private int ACTION_KEY_STRAFE_LEFT = Keyboard.KEY_D;
    private int ACTION_KEY_STRAFE_RIGHT = Keyboard.KEY_A;
    private int ACTION_KEY_ASCEND = Keyboard.KEY_LSHIFT;
    private int ACTION_KEY_DESCEND = Keyboard.KEY_SPACE;
    private int ACTION_KEY_MOVE_FAST = Keyboard.KEY_E;
    private int ACTION_KEY_MOVE_SLOW = Keyboard.KEY_Q;

    private boolean mouseDown = false;

    private float FOV_Y = 45.0f;
    private float Z_NEAR = 0.1f;
    private float Z_FAR = 300.0f;

    private String VERTEX_SHADER_LOCATION = "resources/shaders/phongShader.vert";
    private String FRAGMENT_SHADER_LOCATION = "resources/shaders/phongShader.frag";
    private int shaderProgram = 0;

    private float[] CLEAR_COLOR = new float[] {0.05f, 0.05f, 0.05f, 1f};
    private float[] AMBIENT_LIGHT_RGBA = new float[] {0.25f, 0.25f, 0.25f, 1f};

    public Vector3f position = new Vector3f(0f, 3f, 5f);
    public Vector3f rotation = new Vector3f();

    private Time time;
    private DisplayMode displayMode;

    public boolean swapping = false;

    private float[] rayStart = new float[3];
    private float[] rayEnd = new float[3];

    public Camera(DisplayMode displayMode, Time time) {
        this.displayMode = displayMode;
        this.time = time;
        initialize();
    }

    private void setPerspective() {
        GLU.gluPerspective(FOV_Y, ((float) displayMode.getWidth()) / (float) displayMode.getHeight(), Z_NEAR, Z_FAR);
    }

    public void update(PickHandler pickHandler) {
        if (swapping)
            return;

        float movementSpeed = MOVEMENT_SPEED;

        this.ray();
        float[] origin = {rayStart[0], rayStart[1], rayStart[2]};
        float[] direction = {rayEnd[0] - rayStart[0], rayEnd[1] - rayStart[1], rayEnd[2] - rayStart[2]};

        mouseDown = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);

        if (!mouseDown)
            pickHandler.clear();

        if (Keyboard.isKeyDown(ACTION_KEY_MOVE_FAST)) {
            movementSpeed *= MOVEMENT_SPEED_MULTIPLIER_FAST;
        }

        if (Keyboard.isKeyDown(ACTION_KEY_MOVE_SLOW)) {
            movementSpeed *= MOVEMENT_SPEED_MULTIPLIER_SLOW;
        }

        if (Mouse.isGrabbed()) {
            this.rotation.x = Math.max(Math.min(this.rotation.x - Mouse.getDY() * this.MOUSE_SENSITIVITY, this.PITCH_MAX), this.PITCH_MIN);
            this.rotation.y += Mouse.getDX() * this.MOUSE_SENSITIVITY;
        }

        while(Mouse.next()) {
            if (Mouse.getEventButtonState()) {

                if (Mouse.getEventButton() == 0) { // left mouse button
                    if (!Mouse.isGrabbed()) {

                        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {

                            Vector3f position = Util.intersectHorizontalPlane(origin, direction, 0);
                            position.y += 5;

                            try {
                                LightSource ls = new LightSource(position);
                                Entity3D.renderSet.add(ls);
                            }
                            catch (Exception e) {

                            }

                        } else if (Keyboard.isKeyDown(Keyboard.KEY_V)) {

                            Vector3f position = Util.intersectHorizontalPlane(origin, direction, 0);
                            position.y += 0.8f;

                            BraitenBergVehicle vehicle = new BraitenBergVehicle(position);
                            vehicle.body.material.color = new Vector4f((float)Math.random(), (float)Math.random(), (float)Math.random(), 1f);
                            Entity3D.renderSet.add(vehicle);
                        }
                        else {
                            pickHandler.click(this.pick());
                        }
                    }
                }

                if (Mouse.getEventButton() == 1) { // right mouse button
                    if (!Mouse.isGrabbed()) {
                        pickHandler.click(this.pick());
                    }
                }
            }
        }

        if (Mouse.isButtonDown(0) && !Mouse.isButtonDown(1) && !Keyboard.isKeyDown(Keyboard.KEY_R)) {
            pickHandler.moveHorizontal(origin, direction);
        }

        if (!Mouse.isButtonDown(0) && Mouse.isButtonDown(1)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y);

            float[] horizon = {(float)Math.cos(xzPlaneAngle), 0, (float)Math.sin(xzPlaneAngle)};

            pickHandler.moveVertical(origin, direction, horizon);

        }

        if (Mouse.isButtonDown(0) && !Mouse.isButtonDown(1) && Keyboard.isKeyDown(Keyboard.KEY_R)) {
            pickHandler.rotate(origin, direction);
        }

        if (Keyboard.isKeyDown(ACTION_KEY_MOVE_FORWARDS)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y);

            this.position.z -= (float) Math.cos(xzPlaneAngle) * movementSpeed * time.getDelta();
            this.position.x += (float) Math.sin(xzPlaneAngle) * movementSpeed * time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_MOVE_BACKWARDS)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y);

            this.position.z += (float) Math.cos(xzPlaneAngle) * movementSpeed * time.getDelta();
            this.position.x -= (float) Math.sin(xzPlaneAngle) * movementSpeed * time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_STRAFE_LEFT)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y + 90);

            this.position.z -= (float) Math.cos(xzPlaneAngle) * movementSpeed * time.getDelta();
            this.position.x += (float) Math.sin(xzPlaneAngle) * movementSpeed * time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_STRAFE_RIGHT)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y - 90);

            this.position.z -= (float) Math.cos(xzPlaneAngle) * movementSpeed * time.getDelta();
            this.position.x += (float) Math.sin(xzPlaneAngle) * movementSpeed * time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_ASCEND)) {
            this.position.y -= movementSpeed * time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_DESCEND)) {
            this.position.y += movementSpeed * time.getDelta();
        }

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                /**
                 * if (Keyboard.getEventKey() == Keyboard.KEY_*) {
                 *       key * is pressed
                 * }
                 * */
                if (Keyboard.getEventKey() == Keyboard.KEY_C) {
                    Mouse.setGrabbed(!Mouse.isGrabbed());
                    if (Mouse.isGrabbed()) {
                        pickHandler.clear();
                    }

                }
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
            if (pickHandler.picked != null) {
                Entity3D ent = pickHandler.picked;

                pickHandler.clear();

                ent.delete();
            }
        }
    }

    public void initialize() {
        Mouse.setGrabbed(true);

        initBasic();
        initShaders();
        initLighting();
    }

    private void initBasic() {
        glClearDepth(1.0f); // clear depth buffer
        glEnable(GL_DEPTH_TEST); // Enables depth testing
        glDepthFunc(GL_LEQUAL); // sets the type of test to use for depth
        glMatrixMode(GL_PROJECTION); // sets the matrix mode to project

        setPerspective();

        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        //clear color
        glClearColor(CLEAR_COLOR[0],CLEAR_COLOR[1], CLEAR_COLOR[2], CLEAR_COLOR[3]);

        //culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    private void initLighting() {
        glEnable(GL_LIGHTING);
        glEnable(GL_COLOR_MATERIAL);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, asFloatBuffer(AMBIENT_LIGHT_RGBA));
    }

    private void initShaders() {
        glShadeModel(GL_SMOOTH);

        shaderProgram = loadShaderProgram(
                loadShader(VERTEX_SHADER_LOCATION, GL_VERTEX_SHADER),
                loadShader(FRAGMENT_SHADER_LOCATION, GL_FRAGMENT_SHADER));
        glUseProgram(shaderProgram);
    }

    private int pick() {
        glUseProgram(0);

        int x = Mouse.getX();
        int y = Mouse.getY();

        // The selection buffer
        IntBuffer selBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder()).asIntBuffer();
        int buffer[] = new int[256];

        IntBuffer vpBuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
        // The size of the viewport. [0] Is <x>, [1] Is <y>, [2] Is <width>, [3] Is <height>
        int[] viewport = new int[4];

        // The number of "hits" (objects within the pick area).
        int hits;
        // Get the viewport info
        glGetInteger(GL_VIEWPORT, vpBuffer);
        vpBuffer.get(viewport);

        // Set the buffer that OpenGL uses for selection to our buffer
        glSelectBuffer(selBuffer);

        // Change to selection mode
        glRenderMode(GL_SELECT);

        // Initialize the name stack (used for identifying which object was selected)
        glInitNames();
        glPushName(0);

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();

        /*  create 5x5 pixel picking region near cursor location */
        GLU.gluPickMatrix((float) x, (float) y, 1.0f, 1.0f, IntBuffer.wrap(viewport));

        //GLU.gluPerspective(40f, 800/600f, 0.001f, 400f);
        this.setPerspective();
        this.renderPicking();

        glMatrixMode (GL_PROJECTION);
        glPopMatrix();
        glFlush();

        // Exit selection mode and return to render mode, returns number selected
        hits = glRenderMode(GL_RENDER);

        int picked = -1;

        selBuffer.get(buffer);
        // Objects Were Drawn Where The Mouse Was
        int depth = Integer.MAX_VALUE;
        if (hits > 0) {

            for (int i = 0; i < hits; i++) {
                int hitId = buffer[i * 4 + 3];
                int hitDepth = buffer[i * 4 + 1];

                // Loop Through All The Detected Hits
                // If This Object Is Closer To Us Than The One We Have Selected
                if (buffer[i * 4 + 1] <  depth && Entity3D.get(hitId).pickable) {
                    picked = hitId; // Select The Closer Object
                    depth = hitDepth; // Store How Far Away It Is
                }
            }
        }

        glUseProgram(shaderProgram);

        return picked;
    }

    private void ray() {
        FloatBuffer modelMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glGetFloat(GL_MODELVIEW_MATRIX, modelMatrix);

        FloatBuffer projectionMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glGetFloat(GL_PROJECTION_MATRIX, projectionMatrix);

        IntBuffer viewport = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
        glGetInteger(GL_VIEWPORT, viewport);

        FloatBuffer objectPosition = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
        float[] pos = new float[3];

        GLU.gluUnProject(Mouse.getX(), Mouse.getY(), 0, modelMatrix, projectionMatrix, viewport, objectPosition);

        objectPosition.get(pos);

        float[] pos2 = new float[3];

        GLU.gluUnProject(Mouse.getX(), Mouse.getY(), 1, modelMatrix, projectionMatrix, viewport, objectPosition);

        objectPosition.get(pos2);

        this.rayStart = pos;
        this.rayEnd = pos2;
    }

    public void render() {
        if (swapping)
            return;

        glMatrixMode(GL_MODELVIEW);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        glRotatef(this.rotation.x, 1, 0, 0);
        glRotatef(this.rotation.y, 0, 1, 0);
        glTranslatef(-this.position.x, -this.position.y, -this.position.z);

        LightSourceManager.positionLights();

        glEnableClientState(GL_VERTEX_ARRAY);
        if (glGetInteger(GL_RENDER_MODE) == GL_RENDER) {
            glEnableClientState(GL_NORMAL_ARRAY);
        }

        for (Entity3D e : Entity3D.renderSet) {
            glLoadName(e.id);
            e.render();
        }

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);

        if (mouseDown && PickHandler.ent != null) {

            glLineWidth(1);
            Vector3f pos = PickHandler.ent.position;

            glUseProgram(0);
            glColor3f(0f, 0f, 0f);
            glBegin(GL_LINES);
            glVertex3f(pos.x - 500, pos.y, pos.z);
            glVertex3f(pos.x + 500, pos.y, pos.z);
            glEnd();
            glBegin(GL_LINES);
            glVertex3f(pos.x, pos.y - 500, pos.z);
            glVertex3f(pos.x, pos.y + 500, pos.z);
            glEnd();
            glBegin(GL_LINES);
            glVertex3f(pos.x, pos.y, pos.z - 500);
            glVertex3f(pos.x, pos.y, pos.z + 500);
            glEnd();
            glBegin(GL_LINES);
            glVertex3f(pos.x - 500, 0.1f, pos.z);
            glVertex3f(pos.x + 500, 0.1f, pos.z);
            glEnd();
            glBegin(GL_LINES);
            glVertex3f(pos.x, 0.1f, pos.z - 500);
            glVertex3f(pos.x, 0.1f, pos.z + 500);
            glEnd();
            glUseProgram(shaderProgram);
        }
    }

    public void renderPicking() {
        if (swapping)
            return;

        glMatrixMode(GL_MODELVIEW);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        glRotatef(this.rotation.x, 1, 0, 0);
        glRotatef(this.rotation.y, 0, 1, 0);
        glTranslatef(-this.position.x, -this.position.y, -this.position.z);

        LightSourceManager.positionLights();

        glEnableClientState(GL_VERTEX_ARRAY);
        if (glGetInteger(GL_RENDER_MODE) == GL_RENDER) {
            glEnableClientState(GL_NORMAL_ARRAY);
        }

        for (Entity3D e : Entity3D.renderSet) {
            if (e.pickable) {
                glLoadName(e.id);
                e.render();
            }
        }

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
    }

}