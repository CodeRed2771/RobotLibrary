/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author austin
 */
public class FieldOrientedDrive extends Drive {

    Drive drive;
    PIDSource pIDSource;

    public FieldOrientedDrive(Drive drive, PIDSource orientation) {
        this.drive = drive;
        this.pIDSource = orientation;
    }

    @Override
    protected void recalulate(double x, double y, double rot) {
        double mag = Math.sqrt(x * x + y * y);
        double dir = Math.atan2(x, y) - Math.toRadians(pIDSource.pidGet());

        drive.setXYRot(mag * Math.sin(dir), mag * Math.cos(dir), rot);
    }

}
