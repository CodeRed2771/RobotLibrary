package com.coderedrobotics.libs;

/**
 *
 * @author austin
 */
public class TankDrive extends Drive {

    private final PWMController lVictor, rVictor;

    public TankDrive(PWMController lVictor, PWMController rVictor) {
        this.lVictor = lVictor;
        this.rVictor = rVictor;
    }

    @Override
    protected void recalulate(double x, double y, double rot) {
        lVictor.set(-y - rot);
        rVictor.set(y - rot);
    }
}
