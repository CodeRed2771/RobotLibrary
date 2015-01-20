package com.coderedrobotics.libs.dashboard.api.gui;

import com.coderedrobotics.libs.dashboard.api.resources.RemoteDouble;
import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class Bar {

    RemoteDouble value;

    public Bar(String subsocketPath) {
        try {
            value = new RemoteDouble(subsocketPath, RemoteDouble.MODE.REMOTE);
        } catch (InvalidRouteException ex) {
            Logger.getLogger(Bar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPercentage(double percent) {
        try {
            value.setValue(percent);
        } catch (RemoteDouble.InvalidModeException ex) {
            Logger.getLogger(Bar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getPercentage() {
        return value.getValue();
    }
}
