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
    private final boolean backwards;
    
    public PWMController(int port, boolean backwards) {
        this.port = port;
        this.backwards = backwards;
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
            controller.set(speed * (backwards ? -1 : 1));
        }
        if (VirtualizationController.getInstance().isMonitoringEnabled()) {
            VirtualizationController.getInstance().setPWM(this, speed * (backwards ? -1 : 1));
        }
    }
    
    public Victor getWIPController() {
        return controller;
    }
}
