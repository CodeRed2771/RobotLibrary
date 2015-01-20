package com.coderedrobotics.libs;

/**
 *
 * @author Michael
 */
public class Timer {

    private int stage;
    private long startTime;

    public Timer() {

    }

    public void resetTimer(long time) {
        startTime = System.currentTimeMillis() + time;
    }

    public void advanceWhenReady() {
        if (startTime < System.currentTimeMillis()) {
            stage++;
        }
    }
    
    public boolean ready() {
        return startTime < System.currentTimeMillis();
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
    
    public void nextStage() {
        this.stage++;
    }
}
