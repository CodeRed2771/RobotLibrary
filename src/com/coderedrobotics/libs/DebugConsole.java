package com.coderedrobotics.libs;

import com.coderedrobotics.libs.dashboard.communications.Connection;
import com.coderedrobotics.libs.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.libs.dashboard.communications.Subsocket;
import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.libs.dashboard.communications.listeners.ConnectionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class DebugConsole implements ConnectionListener {

    private ArrayList<String> streamNames = new ArrayList<>();
    private ArrayList<Subsocket> streams = new ArrayList<>();

    private ArrayList<Action> toSend = new ArrayList<>();
    private static DebugConsole instance = null;
    private final Subsocket root;
    private Subsocket debugConsole;
    private boolean connected = false;

    private DebugConsole() {
        root = Connection.getInstance().getRootSubsocket();
        try {
            Connection.addConnectionListener(this);
            debugConsole = Connection.getInstance().getRootSubsocket().enableMultiplexing()
                    .createNewRoute("root.debugconsole").enableMultiplexing();
        } catch (NotMultiplexedException | InvalidRouteException ex) {
            Logger.getLogger(DebugConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DebugConsole getInstance() {
        if (instance == null) {
            instance = new DebugConsole();
        }
        return instance;
    }

    public synchronized void println(String output, String stream) {
        if (!streamNames.contains(stream)) {
            setupNewStream(stream);
        }
        streams.get(streamNames.indexOf(stream)).sendData(PrimitiveSerializer.toByteArray(output));
    }
    
    public synchronized void println(double output, String stream) {
        println(String.valueOf(output), stream);
    }
    
    public synchronized void println(int output, String stream) {
        println(String.valueOf(output), stream);
    }

    private synchronized void setupNewStream(String stream) {
        streamNames.add(stream);
        try {
            streams.add(root.createNewRoute("root.debugconsole." + stream));
        } catch (NotMultiplexedException | InvalidRouteException ex) {
            Logger.getLogger(DebugConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] data = PrimitiveSerializer.toByteArray("root.debugconsole." + stream);
        if (!connected) {
            toSend.add(new Action(data));
        } else {
            debugConsole.sendData(data);
        }
    }

    @Override
    public void connected() {
        connected = true;
        for (Action a : toSend) {
            debugConsole.sendData(a.data);
        }
        toSend.clear();
    }

    @Override
    public void disconnected() {
        connected = false;
    }

    private class Action {
        byte[] data;
        
        Action(byte[] data) {
            this.data = data;
        }
    }
    
}
