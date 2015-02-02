package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author Michael
 */
public class VirtualizableAnalogInput implements PIDSource {
    
    private AnalogInput analogInput;
    private final boolean virtualized;
    private final int port;
    private double state = 0;

    @SuppressWarnings("LeakingThisInConstructor")
    public VirtualizableAnalogInput(int port) {
        this.port = port;
        virtualized = VirtualizationController.getInstance().isVirtualizationEnabled();
        if (virtualized) {
            VirtualizationController.getInstance().addAnalogInput(this);
        } else {
            analogInput = new AnalogInput(port);
        }
    }

    public int getPort() {
        return port;
    }

    public double get() {
        if (virtualized) {
            return state;
        } else {
            return analogInput.getVoltage();
        }
    }

    void set(double state) {
        this.state = state;
    }

    @Override
    public double pidGet() {
        return get();
    }
}
