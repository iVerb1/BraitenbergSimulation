import entity.Entity3D;
import entity.SimpleEntity3D;
import entity.predefined.BraitenBergVehicle;
import entity.predefined.LightSource;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import rendering.Camera;
import rendering.PickHandler;
import util.Time;

import java.util.HashSet;

public class BraitenBergSimulation {

    private DisplayMode displayMode;
    public boolean closeRequested;

    public boolean PAUSED = false;
    public double TIMESCALE = 1.0;

    public PickHandler pickHandler = new PickHandler();
    private Time time;
    public Camera cam;

    public BraitenBergSimulation() {
        closeRequested = false;
        time = new Time();
        initializeWindow();
        cam = new Camera(displayMode, time);
        initializeWorld();
    }

    public void reloadEntities (HashSet<Entity3D> entities) {
        Entity3D.clearIdMap();

        for (Entity3D e : entities) {
            e.reload();
        }
    }

    public void run() {
        time.initialize();

        while (!closeRequested) {
            time.tick();
            pollInput();

            update();

            cam.update(pickHandler);
            cam.render();

            Display.update();
            Display.sync(60);
        }
    }

    private void update() {
        if (!PAUSED) {
            for (Entity3D e : Entity3D.renderSet) {
                e.update(time.getDelta(), (float) TIMESCALE);
            }
        }
    }

    private void initializeWorld() {
        //SimpleEntity3D bunny = new SimpleEntity3D("bunny", new Vector3f(10f, 0.7f, -20f), true);
        //bunny.scalingFactor = 0.4f;
        //bunny.material.color = new Vector4f(0.6f, 0.35f, 0.15f, 1f);

        SimpleEntity3D plane = new SimpleEntity3D("plane", new Vector3f(0f, 0f, 0f), true);
        plane.scalingFactor = 10f;
        plane.material.color = new Vector4f(1f, 1f, 1f, 1f);
        plane.material.specularColor = new Vector4f(0f, 0f, 0f, 1f);
        plane.material.shininess = 0f;
        plane.pickable = false;

        BraitenBergVehicle vehicle = new BraitenBergVehicle(new Vector3f(0f, 0.8f, -10f));
        vehicle.heading = new Vector3f(0f, 0f, -1f);

        LightSource light1 = new LightSource(new Vector3f(5f, 5f, -40f));
        LightSource light2 = new LightSource(new Vector3f(-5f, 5f, -40f));

        Entity3D.renderSet.add(plane);
        //Entity3D.renderSet.add(bunny);
        Entity3D.renderSet.add(vehicle);
        Entity3D.renderSet.add(light1);
        Entity3D.renderSet.add(light2);
    }

    private void initializeWindow() {
        displayMode = new DisplayMode(UI.VIEWPORT_WIDTH, UI.VIEWPORT_HEIGHT);

        try {
            Display.setDisplayMode(displayMode);
            Display.create();
        }
        catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void pollInput() {
        if (Display.isCloseRequested())
            closeRequested = true;
    }

    public static void main(String[] args) {
        BraitenBergSimulation sim = new BraitenBergSimulation();
        sim.run();
    }
}
