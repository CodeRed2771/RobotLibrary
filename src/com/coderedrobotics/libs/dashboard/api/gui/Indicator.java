package com.coderedrobotics.libs.dashboard.api.gui;

import com.coderedrobotics.libs.dashboard.api.resources.RemoteBoolean;
import com.coderedrobotics.libs.dashboard.api.resources.RemoteString;
import com.coderedrobotics.libs.dashboard.communications.Connection;
import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.NotMultiplexedException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class Indicator {

    RemoteBoolean state;
    RemoteString text;

    public Indicator(String subsocketPath) {
        try {
            Connection.getInstance().getRootSubsocket().enableMultiplexing().createNewRoute(subsocketPath).enableMultiplexing();
            state = new RemoteBoolean(subsocketPath + ".boolean", RemoteBoolean.MODE.REMOTE);
            text = new RemoteString(subsocketPath + ".string", RemoteString.MODE.REMOTE);
        } catch (InvalidRouteException | NotMultiplexedException ex) {
            Logger.getLogger(Indicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setState(boolean state) {
        try {
            this.state.setValue(state);
        } catch (RemoteBoolean.InvalidModeException ex) {
            Logger.getLogger(Indicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setText(String text) {
        try {
            this.text.setValue(text);
        } catch (RemoteString.InvalidModeException ex) {
            Logger.getLogger(Indicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void toggleValue() {
        try {
            state.toggleValue();
        } catch (RemoteBoolean.InvalidModeException ex) {
            Logger.getLogger(Indicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean getState() {
        return state.getValue();
    }

    public String getText() {
        return text.getValue();
    }
}
