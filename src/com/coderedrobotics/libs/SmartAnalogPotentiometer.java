package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 * @author Michael
 */
public class SmartAnalogPotentiometer extends AnalogPotentiometer {

    DriverStation ds;
    
    private boolean virtualMode;
    private final int channel;
    
    public SmartAnalogPotentiometer(int channel) {
        super(channel);
        ds = DriverStation.getInstance();
        this.virtualMode = false;
        this.channel = channel;
    }
    
    public SmartAnalogPotentiometer(int channel, boolean virtualMode) {
        super(channel);
        ds = DriverStation.getInstance();
        this.virtualMode = virtualMode;
        this.channel = channel;
    }
    
    public double get() {
        if (!virtualMode){
            return super.get();
        } else {
            return ds.getAnalogIn(channel);
        }
    }

    public boolean isVirtualMode() {
        return virtualMode;
    }

    public void setVirtualMode(boolean virtualMode) {
        this.virtualMode = virtualMode;
    }
}
