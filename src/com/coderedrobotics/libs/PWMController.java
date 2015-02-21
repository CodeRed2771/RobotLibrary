package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.Encoder;
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
    private Encoder encoder;
    private long time = -1;
    private int lastEncoderReading = 0;

    public PWMController(int port, boolean backwards) {
        this(port, backwards, null);
    }

    public PWMController(int port, boolean backwards, Encoder encoder) {
        this.encoder = encoder;
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
        if (!virtualized) {
            controller.set(speed * (backwards ? -1 : 1));
        }
        if (VirtualizationController.getInstance().isMonitoringEnabled()) {
            VirtualizationController.getInstance().setPWM(this, speed * (backwards ? -1 : 1));
        }

        if (encoder != null) {
            int encoderReading = encoder.getRaw();
            
            if (Math.abs(encoderReading - lastEncoderReading) < 4) {
                if (Math.abs(speed) > 0.3) {
                    if (time == -1) {
                        time = System.currentTimeMillis();
                    } else if (System.currentTimeMillis() - time > 250) {
                        System.out.println("Motor port " + port + " encoder error");
                    }
                }
            } else {
                lastEncoderReading = encoderReading;
            }
        }
    }

    public Victor getWIPController() {
        return controller;
    }
}
