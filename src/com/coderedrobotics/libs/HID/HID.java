package com.coderedrobotics.libs.HID;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Austin
 */
public class HID {

    Joystick joystick;

    /**
     *
     * @param port
     */
    public HID(int port) {
        joystick = new Joystick(port);
    }

    public boolean button(Button button) {
        if (button instanceof AxisButton) {
            AxisButton axis = (AxisButton) button;
            return axis(axis.axis) * (axis.direction ? 1 : -1) > 0.2;
        }
        return joystick.getRawButton(button.button);
    }

    public double axis(Axis axis) {
        double result = joystick.getRawAxis(axis.axis);
        boolean sign = result > 0d;
        result = Math.abs(result);
        result -= axis.deadZone;
        if (result < 0d) {
            return 0d;
        }
        result = result * (1d / (1d - axis.deadZone));
        result *= sign ? 1d : -1d;
        return result;
    }

    public boolean buttonReleased(Button button, ButtonState state) {
        boolean b = button(button);
        boolean result = !b && state.state;
        state.state = b;
        return result;
    }

    public boolean buttonPressed(Button button, ButtonState state) {
        boolean b = button(button);
        boolean result = b && !state.state;
        state.state = b;
        return result;
    }

    public boolean buttonToggled(
            Button button,
            ButtonState pressState,
            ButtonState toggleState) {
        if (buttonPressed(button, pressState)) {
            toggleState.state = !toggleState.state;
        }
        return toggleState.state;
    }

    public static ButtonState newButtonState() {
        return new ButtonState();
    }

    public static class Button {

        private int button;

        protected Button(Axis axis, boolean direction) {
        }//do not use this constructor

        Button(int button) {
            this.button = button;
        }
    }

    public static class Axis {

        private int axis;
        private double deadZone = 0;

        Axis(int axis) {
            this.axis = axis;
        }

        Axis(int axis, double deadZone) {
            this.axis = axis;
            this.deadZone = deadZone;
        }
    }

    public static class AxisButton extends Button {

        private Axis axis;
        private boolean direction;

        AxisButton(Axis axis, boolean direction) {
            super(axis, direction);
            this.axis = axis;
            this.direction = direction;
        }
    }

    public static class ButtonState {

        private boolean state = false;

        private ButtonState() {
        }

    }
}
