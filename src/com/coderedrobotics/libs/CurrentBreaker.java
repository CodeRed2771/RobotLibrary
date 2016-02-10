package com.coderedrobotics.libs;

import com.coderedrobotics.libs.SettableController;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;

public class CurrentBreaker {

    SettableController sc;
    PowerDistributionPanel pdp;
    int portnum;
    double currentThreshold;
    int timeOut;
    boolean motorOff = false;
    boolean tripped = false;
    long motorOffTime = -1;
    long ignoreTime = -1;
    long ignoreDuration = -1;
    
    public CurrentBreaker(SettableController sc, int portnum, double currentThreshold, int timeOut) {
        pdp = new PowerDistributionPanel();
        this.sc = sc == null ? new NullController() : sc;
        this.portnum = portnum;
        this.currentThreshold = currentThreshold;
        this.timeOut = timeOut;
    }

    /**
     * returns true if the breaker exceeds the allowed value.
     * @return breaker tripped
     */
    public boolean step() {
        if (ignoreDuration != -1) {
            ignoreTime = System.currentTimeMillis() + ignoreDuration;
            ignoreDuration = -1;
        }
        if (System.currentTimeMillis() <= ignoreTime) {
            return false;
        }
        
        if (!tripped) {
            tripped = (pdp.getCurrent(portnum) > currentThreshold);
            Logger.getInstance().log(Logger.Level.ERROR, 1, String.valueOf(pdp.getCurrent(portnum)));
            if (!tripped) {
                return false;
            }
        }
        if (motorOffTime == -1) {
            motorOffTime = System.currentTimeMillis() + timeOut;
        }
        if (motorOff || System.currentTimeMillis() >= motorOffTime) {
            motorOff = true;
            sc.set(0.0);
        }
        return motorOff;
    }

    public void set(double speed) {
        step();
        if (!motorOff) {
            sc.set(speed);
        }
    }

    public void reset() {
        tripped = false;
        motorOff = false;
        motorOffTime = -1;
    }
    
    public void ignoreFor(long time) {
        ignoreDuration = time;
    }
    
    private class NullController implements SettableController {

        @Override
        public void set(double value) {

        }
        
    }
}
