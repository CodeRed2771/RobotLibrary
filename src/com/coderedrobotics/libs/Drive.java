package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Victor;

public abstract class Drive {

    double x, y, rot;

    public Drive() {
    }

    public void setX(double x) {
        this.x = x;
        recalulate();
    }

    public void setY(double y) {
        this.y = y;
        recalulate();
    }

    public void setRot(double rot) {
        this.rot = rot;
        recalulate();
    }

    public void setXY(double x, double y) {
        setX(x);
        setY(y);
    }

    public void setXRot(double x, double rot) {
        setX(x);
        setRot(rot);
    }

    public void setYRot(double y, double rot) {
        setY(y);
        setRot(rot);
    }

    public void setXYRot(double x, double y, double rot) {
        setX(x);
        setY(y);
        setRot(rot);
    }

    protected abstract void recalulate();

    public PIDOutput getRotPIDOutput() {
        return new PIDOutput() {

            Drive drive;

            @Override
            public void pidWrite(double rot) {
                drive.setRot(rot);
            }

            public PIDOutput init(Drive drive) {
                this.drive = drive;
                return this;
            }
        }.init(this);
    }

    public PIDOutput getXPIDOutput() {
        return new PIDOutput() {

            Drive drive;

            @Override
            public void pidWrite(double x) {
                drive.setX(x);
            }

            public PIDOutput init(Drive drive) {
                this.drive = drive;
                return this;
            }
        }.init(this);
    }

    public PIDOutput getYPIDOutput() {
        return new PIDOutput() {

            Drive drive;

            @Override
            public void pidWrite(double y) {
                drive.setY(y);
            }

            public PIDOutput init(Drive drive) {
                this.drive = drive;
                return this;
            }
        }.init(this);
    }
}
