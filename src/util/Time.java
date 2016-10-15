package util;

import org.lwjgl.Sys;

/**
 * Created by s113958 on 7-3-2015.
 */
public class Time {

    private long lastTime;
    private float delta;

    public Time() {   }

    public void initialize() {
        this.lastTime = 1000 * Sys.getTime() / Sys.getTimerResolution();
    }

    public float getDelta() {
        return delta;
    }

    public void tick() {
        long time = 1000 * Sys.getTime() / Sys.getTimerResolution();
        delta = ((float)(time - lastTime)) / 1000;
        lastTime = time;
    }

}
