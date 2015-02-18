/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.libs;

/**
 *
 * @author austin
 */
public class PIDDrive extends Drive {

    private Drive outDrive;
    private boolean enablePID = true;
    private PIDControllerAIAO xPID, yPID, rotPID;

    public PIDDrive(Drive outDrive,
            double xp, double xi, double xd,
            double yp, double yi, double yd,
            double rotp, double roti, double rotd) {

        this.outDrive = outDrive;
        xPID = new PIDControllerAIAO(xp, xi, xd, null, outDrive.getXPIDOutput(), "X drive");
        yPID = new PIDControllerAIAO(yp, yi, yd, null, outDrive.getYPIDOutput(), "Y drive");
        rotPID = new PIDControllerAIAO(rotp, roti, rotd, null, outDrive.getRotPIDOutput(), "rot drive");

        xPID.enable();
        yPID.enable();
        rotPID.enable();

        xPID.setInputRange(-1, 1);
        yPID.setInputRange(-1, 1);
        rotPID.setInputRange(-1, 1);
    }

    public void enablePID() {
        enablePID = true;
        xPID.enable();
        yPID.enable();
        rotPID.enable();
    }

    public void disablePID() {
        enablePID = false;
        xPID.disable();
        yPID.disable();
        rotPID.disable();
    }

    private static double clip(double x) {
        return Math.min(1, Math.max(-1, x));
    }

    @Override
    protected void recalulate(double x, double y, double rot) {
        xPID.setSetpoint(clip(x));
        yPID.setSetpoint(clip(y));
        rotPID.setSetpoint(clip(rot));
    }
}
