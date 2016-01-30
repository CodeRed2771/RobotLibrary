package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.Encoder;

/**
 *
 * @author michael
 */
public class PWMSplitter2X implements SettableController {
    
    private PWMController controllerA;
    private PWMController controllerB;
    
    public PWMSplitter2X(int portA, int portB, boolean backwards) {
        this(portA, portB, backwards, null);
    }
    
    public PWMSplitter2X(int portA, int portB, boolean backwards, Encoder encoder) {
        controllerA = new PWMController(portA, backwards, encoder);
        controllerB = new PWMController(portB, backwards, encoder);
    }
    
    @Override
    public void set(double speed) {
        controllerA.set(speed);
        controllerB.set(speed);
    }
    
    public PWMController getPWMControllerA() {
        return controllerA;
    }
    
    public PWMController getPWMControllerB() {
        return controllerB;
    }
    
}
