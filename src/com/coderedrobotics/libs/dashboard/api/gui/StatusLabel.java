package com.coderedrobotics.libs.dashboard.api.gui;

import com.coderedrobotics.libs.dashboard.api.resources.RemoteString;
import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class StatusLabel {

    RemoteString value;

    public StatusLabel(String subsocketPath) {
        try {
            value = new RemoteString(subsocketPath, RemoteString.MODE.REMOTE);
        } catch (InvalidRouteException ex) {
            Logger.getLogger(StatusLabel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setText(String text) {
        try {
            value.setValue(text);
        } catch (RemoteString.InvalidModeException ex) {
            Logger.getLogger(StatusLabel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getText() {
        return value.getValue();
    }
}
