package com.coderedrobotics.libs;

/**
 *
 * @author Austin
 */
public class Relay {

    private edu.wpi.first.wpilibj.Relay relay;
    private boolean forward, reverse;
    private final int port;
    private boolean virtualized = false;

    @SuppressWarnings("LeakingThisInConstructor")
    public Relay(int port) {
        this.port = port;
        virtualized = VirtualizationController.getInstance().isVirtualizationEnabled();
        if (virtualized) {
            VirtualizationController.getInstance().addRelay(this);
        } else {
            relay = new edu.wpi.first.wpilibj.Relay(port);
            relay.set(edu.wpi.first.wpilibj.Relay.Value.kOff);
        }
    }

    public int getPort() {
        return port;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
        refresh();
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
        refresh();
    }

    public boolean getForward() {
        return forward;
    }

    public boolean getReverse() {
        return reverse;
    }

    private void refresh() {
        if (virtualized) {
            VirtualizationController.getInstance().setRelay(this, forward, reverse);
        } else {
            if (forward || reverse) {
                if (forward && reverse) {
                    relay.setDirection(edu.wpi.first.wpilibj.Relay.Direction.kBoth);
                } else if (forward && !reverse) {
                    relay.setDirection(edu.wpi.first.wpilibj.Relay.Direction.kForward);
                } else if (!forward && reverse) {
                    relay.setDirection(edu.wpi.first.wpilibj.Relay.Direction.kReverse);
                }
                relay.set(edu.wpi.first.wpilibj.Relay.Value.kOn);
            } else {
                relay.set(edu.wpi.first.wpilibj.Relay.Value.kOff);
            }
        }
    }
}
