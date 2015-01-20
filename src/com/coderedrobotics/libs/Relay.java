package com.coderedrobotics.libs;

/**
 *
 * @author Michael
 */
public class Relay {
    
    private edu.wpi.first.wpilibj.Relay relay;
    private final int port;
    private final boolean virtualized;
    private edu.wpi.first.wpilibj.Relay.Value state;
    private edu.wpi.first.wpilibj.Relay.Direction direction;

    public Relay(int port) {
        this.port = port;
        virtualized = VirtualizationController.getInstance().isVirtualizationEnabled();
        if (virtualized) {
            VirtualizationController.getInstance().addRelay(this);
        } else {
            relay = new edu.wpi.first.wpilibj.Relay(port);
        }
    }

    public int getPort() {
        return port;
    }

    public edu.wpi.first.wpilibj.Relay.Value get() {
        return virtualized ? state : relay.get();
    }
    
    public void set(edu.wpi.first.wpilibj.Relay.Value value) {
        if (virtualized) {
            VirtualizationController.getInstance().setRelay(this, value);
            this.state = value;
        } else {
            relay.set(value);
        }
    }
    
    public edu.wpi.first.wpilibj.Relay.Direction getDirection() {
        return direction;
    }
    
    public void setDirection(edu.wpi.first.wpilibj.Relay.Direction direction) {
        this.direction = direction;
        if (!virtualized) {
            relay.setDirection(direction);
        }
    }
}
