package com.coderedrobotics.libs.dashboard.api.resources;

import com.coderedrobotics.libs.dashboard.api.resources.listeners.SynchronizedStringListener;
import com.coderedrobotics.libs.dashboard.communications.Connection;
import com.coderedrobotics.libs.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.libs.dashboard.communications.Subsocket;
import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.libs.dashboard.communications.listeners.ConnectionListener;
import com.coderedrobotics.libs.dashboard.communications.listeners.SubsocketListener;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Michael
 */
public class SynchronizedString implements SubsocketListener, ConnectionListener {

    private String val;
    private boolean highPriority;
    private boolean setup = false;
    private boolean setupHP = false;
    private boolean HPwereSAME = false;
    private int thisSideInt;
    private boolean HPotherSIDEwins = false;
    private boolean setupIV = false;

    private int echosWaitingFor = 0;
    private String sentVariable;

    Subsocket subsocket;
    private ArrayList<SynchronizedStringListener> listeners = new ArrayList<>();

    @SuppressWarnings("LeakingThisInConstructor")
    public SynchronizedString(String subsocketPath, String initialValue, boolean highPriority) throws InvalidRouteException {
        val = initialValue;
        this.highPriority = highPriority;
        try {
            subsocket = Connection.getInstance().getRootSubsocket().enableMultiplexing().createNewRoute(subsocketPath);
        } catch (NotMultiplexedException ex) {
            // really not possible, but java wins again
        }
        subsocket.addListener(this);
        Connection.addConnectionListener(this);
    }

    public SynchronizedString(String subsocketPath, String initialValue) throws InvalidRouteException {
        this(subsocketPath, initialValue, false);
    }

    public SynchronizedString(String subsocketPath) throws InvalidRouteException {
        this(subsocketPath, "");
    }

    public synchronized void setValue(String value) {
        sentVariable = value;
        echosWaitingFor++;
        update();
    }

    public String getValue() {
        return val;
    }

    private void update() {
        subsocket.sendData(PrimitiveSerializer.toByteArray(sentVariable));
    }

    @Override
    public void incomingData(byte[] data, Subsocket subsocket) {
        if (subsocket == this.subsocket) {
            if (!setup) {
                if (!setupHP) {
                    if (!HPwereSAME) {
                        boolean otherSide = PrimitiveSerializer.bytesToBoolean(data);
                        if (otherSide == highPriority) {
                            HPwereSAME = true;
                            thisSideInt = new Random().nextInt();
                            subsocket.sendData(PrimitiveSerializer.toByteArray(thisSideInt));
                        } else {
                            setupHP = true;
                        }
                    } else {
                        if (!HPotherSIDEwins) {
                            int o = PrimitiveSerializer.bytesToInt(data);
                            if (thisSideInt > o) {
                                highPriority = new Random().nextBoolean();
                                subsocket.sendData(PrimitiveSerializer.toByteArray(!highPriority));
                                setupHP = true;
                            } else if (thisSideInt == o) { // IF THIS EVER HAPPENS....
                                thisSideInt = new Random().nextInt();
                                subsocket.sendData(PrimitiveSerializer.toByteArray(thisSideInt));
                            } else {
                                HPotherSIDEwins = true;
                            }
                        } else {
                            highPriority = PrimitiveSerializer.bytesToBoolean(data);
                            setupHP = true;
                        }
                    }
                    if (setupHP) {
                        subsocket.sendData(PrimitiveSerializer.toByteArray(val));
                    }
                    return;
                }
                if (!setupIV) {
                    String otherSideVal = PrimitiveSerializer.bytesToString(data);
                    if (!otherSideVal.equals(val)) {
                        if (!highPriority) {
                            updateValue(otherSideVal);
                        }
                    }
                    setupIV = true;
                    setup = true;
                }
            } else {
                if (echosWaitingFor == 0) {
                    updateValue(PrimitiveSerializer.bytesToString(data));
                    subsocket.sendData(data);
                } else {
                    String response = PrimitiveSerializer.bytesToString(data);
                    if (highPriority) {
                        if (sentVariable.equals(response)) {
                            updateValue(sentVariable);
                            echosWaitingFor--;
                        }
                    } else {
                        if (!response.equals(sentVariable)) {
                            subsocket.sendData(PrimitiveSerializer.toByteArray(response));
                        }
                        updateValue(response);
                        echosWaitingFor--;
                    }
                }
            }
        }
    }

    private void updateValue(String value) {
        val = value;
        for (SynchronizedStringListener ssl : listeners) { // not the buggy kind :)
            ssl.update(value, this);
        }
    }

    public void addListener(SynchronizedStringListener ssl) {
        listeners.remove(ssl);
        listeners.add(ssl);
    }

    public void removeListener(SynchronizedStringListener ssl) {
        listeners.remove(ssl);
    }

    public String getSubsocketPath() {
        return subsocket.mapCompleteRoute();
    }

    @Override
    public void connected() {
        subsocket.sendData(PrimitiveSerializer.toByteArray(highPriority));
    }

    @Override
    public void disconnected() {
        setup = false;
        setupHP = false;
        setupIV = false;
        HPotherSIDEwins = false;
        HPwereSAME = false;
        echosWaitingFor = 0;
    }
}
