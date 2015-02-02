package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author Michael
 */
public class PWMController {
    
    private Victor controller;
    private final int port;
    private final boolean virtualized;
    
    public PWMController(int port) {
        this.port = port;
        virtualized = VirtualizationController.getInstance().isVirtualizationEnabled();
        if (!virtualized) {
            controller = new Victor(port);
        }
        if (VirtualizationController.getInstance().isMonitoringEnabled()) {
            VirtualizationController.getInstance().addPWMController(this);
        }
    }
    
    public int getPort() {
        return port;
    }
    
    public void set(double speed) {
        if (!virtualized){
            controller.set(speed);
        }
        if (VirtualizationController.getInstance().isMonitoringEnabled()) {
            VirtualizationController.getInstance().setPWM(this, speed);
        }
    }
    
    public Victor getWIPController() {
        return controller;
    }
}
