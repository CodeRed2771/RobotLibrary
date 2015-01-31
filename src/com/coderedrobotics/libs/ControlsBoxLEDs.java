package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 * @author Michael
 */
public class ControlsBoxLEDs {

    private final Relay greenandred;
    private final Relay blue;

    public static enum Color {

        WHITE, RED, GREEN, BLUE, YELLOW, MAGENTA, CYAN, BLACK
    }

    private Color color = Color.GREEN;
    private static final Color AUTONOMOUS_COLOR = Color.WHITE;
    private static final Color TELEOP_COLOR = Color.RED;
    private static final Color TEST_COLOR = Color.CYAN;
    private static final int SLOW_BLINK_HZ = 1;
    private static final int FAST_BLINK_HZ = 5;

    private double hz = 0; // don't blink
    private int partyTick = 0;

    public ControlsBoxLEDs(int relay1, int relay2) {
        greenandred = new Relay(relay1);
        blue = new Relay(relay2);
    }

    public void activateDisabled() {
        color = Color.GREEN;
        update();
    }

    public void activateTeleop() {
        DriverStation ds = DriverStation.getInstance();
        color = DriverStation.Alliance.Blue == ds.getAlliance() ? Color.BLUE : 
                DriverStation.Alliance.Red == ds.getAlliance() ? Color.RED : Color.MAGENTA;
        update();
    }

    public void activateAutonomous() {
        color = AUTONOMOUS_COLOR;
        update();
    }

    public void activateTest() {
        color = TEST_COLOR;
        update();
    }

    public void turnOff() {
        color = Color.BLACK;
        update();
    }

    public void setHz(double hz) {
        this.hz = hz;
    }

    public void blinkSlow() {
        hz = SLOW_BLINK_HZ;
        update();
    }

    public void blinkFast() {
        hz = FAST_BLINK_HZ;
        update();
    }

    public void blinkTick() {
        update();
    }

    public void party() {
        hz = 15;
        if (partyTick < 10) {
            color = Color.WHITE;
        } else if (partyTick < 20) {
            color = Color.RED;
        } else if (partyTick < 30) {
            color = Color.YELLOW;
        } else if (partyTick < 40) {
            color = Color.GREEN;
        } else if (partyTick < 50) {
            color = Color.CYAN;
        } else if (partyTick < 60) {
            color = Color.BLUE;
        } else if (partyTick < 70) {
            color = Color.MAGENTA;
        }
        if (partyTick == 70) {
            partyTick = 0;
        }
        partyTick++;
    }

    public void setColor(Color color) {
        this.color = color;
        hz = 0;
        update();
    }

    private void update() {
        boolean on = hz == 0 ? true : (((System.currentTimeMillis()/1000d) * hz) % 1) < 0.5;
        
        boolean r = (color == Color.WHITE || color == Color.RED || color == Color.MAGENTA
                || color == Color.YELLOW) && on;
        boolean g = (color == Color.WHITE || color == Color.GREEN || color == Color.YELLOW
                || color == Color.CYAN) && on;
        boolean b = (color == Color.WHITE || color == Color.BLUE || color == Color.MAGENTA
                || color == Color.CYAN) && on;
        greenandred.setForward(!g);
        greenandred.setReverse(r);
        blue.setReverse(b);
        
//        greenandred.setDirection(r && g ? Relay.Direction.kBoth
//                : r ? Relay.Direction.kForward : Relay.Direction.kReverse);
//        greenandred.set(r || g ? Relay.Value.kOn : Relay.Value.kOff);
//        blue.set(b ? Relay.Value.kOn : Relay.Value.kOff);
    }
}
