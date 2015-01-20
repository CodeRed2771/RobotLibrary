package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Michael
 */
public class PWMController {
    
    private Talon controller;
    private final int port;
    private final boolean virtualized;
    
    public PWMController(int port) {
        this.port = port;
        virtualized = VirtualizationController.getInstance().isVirtualizationEnabled();
        if (virtualized) {
            VirtualizationController.getInstance().addPWMController(this);
        } else {
            controller = new Talon(port);
        }
    }
    
    public int getPort() {
        return port;
    }
    
    public void set(double speed) {
        if (virtualized){
            VirtualizationController.getInstance().setPWM(this, speed);
        } else {
            controller.set(speed);
        }
    }
}
