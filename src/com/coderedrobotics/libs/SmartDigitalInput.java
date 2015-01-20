package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 * @author Michael
 */
public class SmartDigitalInput extends DigitalInput {

    DriverStation ds;

    private boolean virtualMode;

    public SmartDigitalInput(int channel, boolean virtualMode) {
        super(channel);
        ds = DriverStation.getInstance();
        this.virtualMode = virtualMode;
    }

    public SmartDigitalInput(int channel) {
        super(channel);
        ds = DriverStation.getInstance();
        this.virtualMode = false;
    }

    public boolean get() {
        if (!virtualMode) {
            return super.get();
        } else {
            return ds.getDigitalIn(super.getChannel());
        }
    }

    public boolean isVirtualMode() {
        return virtualMode;
    }

    public void setVirtualMode(boolean virtualMode) {
        this.virtualMode = virtualMode;
    }
}
