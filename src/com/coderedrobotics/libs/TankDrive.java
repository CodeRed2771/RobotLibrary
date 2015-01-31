/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author austin
 */
public class TankDrive extends Drive {

    private Victor lVictor, rVictor;

    public TankDrive(int lVictor, int rVictor) {
        this.lVictor = new Victor(lVictor);
        this.rVictor = new Victor(rVictor);
    }

    @Override
    protected void recalulate() {
        lVictor.set(-y - rot);
        rVictor.set(y - rot);
    }
}
