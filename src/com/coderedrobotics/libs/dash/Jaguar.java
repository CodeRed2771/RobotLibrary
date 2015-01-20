/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.libs.dash;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 *
 * @author laptop
 */
public class Jaguar {

    private int cANid;
    private int pWMChannel;
    private boolean isCAN;
    private SpeedController jaguar;

    public Jaguar(int cANid) {
        this(cANid, 0);
    }

    public Jaguar(int cANid, int pWMChannel) {
        this.cANid = cANid;
        this.pWMChannel = pWMChannel;
        if (cANid != 0) {
            isCAN = true;
            try {
                jaguar = new CANJaguar(cANid);
            } catch (CANTimeoutException ex) {
                cANTimeout();
            }
        } else {
            isCAN = false;
            jaguar = new edu.wpi.first.wpilibj.Jaguar(pWMChannel);
        }
    }

    private void cANTimeout() {
        isCAN = false;
        if (pWMChannel != 0) {
            jaguar = new edu.wpi.first.wpilibj.Jaguar(pWMChannel);
        } else {
            jaguar = null;
        }
    }

    public synchronized double getBusVoltage() {
        if (isCAN) {
            try {
                return ((CANJaguar) jaguar).getBusVoltage();
            } catch (CANTimeoutException ex) {
                cANTimeout();
            }
        }
        return 0;
    }

    public synchronized double getOutputVoltage() {
        if (isCAN) {
            try {
                return ((CANJaguar) jaguar).getOutputVoltage();
            } catch (CANTimeoutException ex) {
                cANTimeout();
            }
        }
        return 0;
    }

    public synchronized double getOutputCurrent() {
        if (isCAN) {
            try {
                return ((CANJaguar) jaguar).getOutputCurrent();
            } catch (CANTimeoutException ex) {
                cANTimeout();
            }
        }
        return 0;
    }

    public synchronized double getTemperature() {
        if (isCAN) {
            try {
                return ((CANJaguar) jaguar).getTemperature();
            } catch (CANTimeoutException ex) {
                cANTimeout();
            }
        }
        return 0;
    }

    public int getID() {
        if (isCAN) {
            return cANid;
        }
        return pWMChannel;
    }

    public double getX() {
        if (jaguar != null) {
            return jaguar.get();
        }
        return 0;
    }

    public boolean isCAN() {
        return isCAN;
    }

    public boolean isPWM() {
        if (!isCAN && jaguar!=null) {
            return true;
        }
        return false;
    }
}
