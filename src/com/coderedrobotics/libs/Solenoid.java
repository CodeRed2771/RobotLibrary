package com.coderedrobotics.libs;

/**
 *
 * @author Michael
 */
public class Solenoid {

    private edu.wpi.first.wpilibj.Solenoid solenoid;
    private final int port;
    private final boolean virtualized;
    private boolean state;

    public Solenoid(int port) {
        this.port = port;
        virtualized = VirtualizationController.getInstance().isVirtualizationEnabled();
        if (virtualized) {
            VirtualizationController.getInstance().addSolenoid(this);
        } else {
            solenoid = new edu.wpi.first.wpilibj.Solenoid(port);
        }
    }

    public Solenoid(int pcm, int port) {
        this.port = port;
        virtualized = VirtualizationController.getInstance().isVirtualizationEnabled();
        if (virtualized) {
            if (pcm > 0) {
                return;
            }
            VirtualizationController.getInstance().addSolenoid(this);
        } else {
            solenoid = new edu.wpi.first.wpilibj.Solenoid(pcm, port);
        }
    }

    public int getPort() {
        return port;
    }

    public void set(boolean state) {
        if (virtualized) {
            VirtualizationController.getInstance().setSolenoid(this, state);
            this.state = state;
        } else {
            solenoid.set(state);
        }
    }

    public boolean get() {
        return virtualized ? state : solenoid.get();
    }

    public boolean isBlackListed() {
        return virtualized ? solenoid.isBlackListed() : false;
    }
}
