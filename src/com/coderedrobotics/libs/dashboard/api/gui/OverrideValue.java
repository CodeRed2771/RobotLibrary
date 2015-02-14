package com.coderedrobotics.libs.dashboard.api.gui;

import com.coderedrobotics.libs.dashboard.api.resources.RemoteBoolean;
import com.coderedrobotics.libs.dashboard.api.resources.RemoteDouble;
import com.coderedrobotics.libs.dashboard.api.resources.listeners.RemoteBooleanListener;
import com.coderedrobotics.libs.dashboard.communications.Connection;
import com.coderedrobotics.libs.dashboard.communications.Subsocket;
import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.NotMultiplexedException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class OverrideValue implements RemoteBooleanListener {

    RemoteBoolean useOverride;
    RemoteDouble overrideValue;
    RemoteDouble originalValue;

    private double value;
    
    public OverrideValue(String subsocketPath, double originalValue) {
        try {
            Subsocket root = Connection.getInstance().getRootSubsocket().enableMultiplexing();
            root.createNewRoute(subsocketPath).enableMultiplexing();
            useOverride = new RemoteBoolean(subsocketPath + ".use",  RemoteBoolean.MODE.REMOTE);
            useOverride.addListener(this);
            overrideValue = new RemoteDouble(subsocketPath + ".override", RemoteDouble.MODE.REMOTE);
            this.originalValue = new RemoteDouble(subsocketPath + ".original", originalValue);
            value = originalValue;
        } catch (NotMultiplexedException | InvalidRouteException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(boolean value, RemoteBoolean sender) {
        if (value) {
            this.value = overrideValue.getValue();
        } else {
            this.value = originalValue.getValue();
        }
    }
    
    public void setOriginalValue(double value) {
        try {
            originalValue.setValue(value);
        } catch (RemoteDouble.InvalidModeException ex) {
            Logger.getLogger(OverrideValue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getValue() {
        return value;
    }
    
    public boolean isOverriden() {
        return useOverride.getValue();
    }
}
