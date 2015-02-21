/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.libs;

import com.coderedrobotics.libs.dash.DashBoard;
import com.coderedrobotics.libs.dash.PIDControllerAIAO;
import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author austin
 */
public class PIDDrive extends Drive {

    private Drive outDrive;
    private boolean enablePID = true;
    public PIDControllerAIAO xPID, yPID, rotPID;

    public PIDDrive(Drive outDrive,
            PIDSource xSource, PIDSource ySource, PIDSource rotSource,
            double xp, double xi, double xd,
            double yp, double yi, double yd,
            double rotp, double roti, double rotd,
            DashBoard dash) {

        this.outDrive = outDrive;
        xPID = new PIDControllerAIAO(xp, xi, xd, xSource, outDrive.getXPIDOutput(), dash, "X drive");
        yPID = new PIDControllerAIAO(yp, yi, yd, ySource, outDrive.getYPIDOutput(), dash, "Y drive");
        rotPID = new PIDControllerAIAO(rotp, roti, rotd, rotSource, outDrive.getRotPIDOutput(), dash, "rot drive");

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
