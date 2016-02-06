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

    public CurrentBreaker(SettableController sc, int portnum, double currentThreshold, int timeOut) {
        pdp = new PowerDistributionPanel();
        this.sc = sc == null ? new NullController() : sc;
        this.portnum = portnum;
        this.currentThreshold = currentThreshold;
        this.timeOut = timeOut;
    }

    public boolean step() {
        if (!tripped) {
            tripped = (pdp.getCurrent(portnum) > currentThreshold);

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
    
    private class NullController implements SettableController {

        @Override
        public void set(double value) {

        }
        
    }
}
