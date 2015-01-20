package com.coderedrobotics.libs.dashboard.api.gui;

import com.coderedrobotics.libs.dashboard.api.resources.RemoteDouble;
import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class Compass {

    RemoteDouble value;

    public Compass(String subsocketPath) {
        try {
            value = new RemoteDouble(subsocketPath, RemoteDouble.MODE.REMOTE);
        } catch (InvalidRouteException ex) {
            Logger.getLogger(Compass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAngle(double angle) {
        try {
            value.setValue(angle);
        } catch (RemoteDouble.InvalidModeException ex) {
            Logger.getLogger(Compass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getAngle() {
        return value.getValue();
    }
}
