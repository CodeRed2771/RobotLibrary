/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author laptop
 */
public abstract class PlaceTracker {

    private double x, y, rot;
    private double totalLateral, totalLinear;
    private double destinationX, destinationY;

    public PlaceTracker() {
    }

    protected abstract double[] updatePosition();

    public void step() {
        double[] update = updatePosition();

        totalLateral += update[0];
        totalLinear += update[1];

        rot += update[2] / 2;

        x += Math.cos(Math.toRadians(rot)) * update[1];
        y += Math.sin(Math.toRadians(rot)) * update[1];

        x += Math.sin(Math.toRadians(rot)) * update[0];
        y += Math.cos(Math.toRadians(rot)) * update[0];

        rot += update[2] / 2;
    }

    public void goTo(double x, double y) {
        destinationX = x;
        destinationY = y;
    }

    public PIDSource getLateralPIDSource() {
        return new PIDSource() {

            PlaceTracker pt;

            @Override
            public double pidGet() {
                return pt.totalLateral;
            }

            public PIDSource init(PlaceTracker pt) {
                this.pt = pt;
                return this;
            }
        }.init(this);
    }

    public PIDSource getLinearPIDSource() {
        return new PIDSource() {

            PlaceTracker pt;

            @Override
            public double pidGet() {
                return pt.totalLinear;
            }

            public PIDSource init(PlaceTracker pt) {
                this.pt = pt;
                return this;
            }
        }.init(this);
    }

    public PIDSource getRotPIDSource() {
        return new PIDSource() {

            PlaceTracker pt;

            @Override
            public double pidGet() {
                return pt.rot;
            }

            public PIDSource init(PlaceTracker pt) {
                this.pt = pt;
                return this;
            }
        }.init(this);
    }

    public PIDSource getRotToDestinationPIDSource() {
        return new PIDSource() {

            PlaceTracker pt;

            @Override
            public double pidGet() {
                double xError = destinationX - x, yError = destinationY - y;

                return (pt.rot - Math.toDegrees(Math.atan2(yError, xError))) % 360;
            }

            public PIDSource init(PlaceTracker pt) {
                this.pt = pt;
                return this;
            }
        }.init(this);
    }

    public PIDSource getDistanceToDestinationPIDSource() {
        return new PIDSource() {

            PlaceTracker pt;

            @Override
            public double pidGet() {
                double xError = pt.destinationX - pt.x, yError = pt.destinationY - pt.y;

                double rotError = (pt.rot - Math.toDegrees(Math.atan2(yError, xError))) % 360;

                if (rotError > 180) {
                    rotError -= 360;
                }

                double displacement = Math.sqrt(Math.pow(xError, 2) + Math.pow(yError, 2));
                double speed = (90 - Math.abs(rotError)) / 90;

                return speed * displacement;
            }

            public PIDSource init(PlaceTracker pt) {
                this.pt = pt;
                return this;
            }
        }.init(this);
    }
}
