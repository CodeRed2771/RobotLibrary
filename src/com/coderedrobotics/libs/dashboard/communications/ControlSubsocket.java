package com.coderedrobotics.libs.dashboard.communications;

import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.RootRouteException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.RouteException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class ControlSubsocket extends Subsocket {

    Connection connection;

    ControlSubsocket(String nodeName, Connection connection) {
        super(nodeName, null);
        this.connection = connection;
    }

    @Override
    public Subsocket getParent() {
        return null;
    }

    @Override
    void sendData(byte b) {
        connection.writeByte(b);
    }

    @Override
    public void sendData(byte[] bytes) {
        switch (BindingManager.getBytesRequiredToTransmit()) {
            case 1:
                sendData((byte) getID());
                break;
            case 2:
                for (byte b : PrimitiveSerializer.toByteArray((short) getID())) {
                    sendData(b);
                }
                break;
            case 4:
                for (byte b : PrimitiveSerializer.toByteArray(getID())) {
                    sendData(b);
                }
                break;
        }
        for (byte b : PrimitiveSerializer.toByteArray((char) bytes.length)) {
            sendData(b);
        }
        for (byte b : bytes) {
            sendData(b);
        }
    }

    void sendMultiplexAction(MPLXAction action) {
        byte code = (byte) action.typeToInt();
        byte[] id = new byte[1];
        try{
        switch (BindingManager.getBytesRequiredToTransmit()) {
            case 1:
                id[0] = (byte) BindingManager.checkID(action.getRoute());
                break;
            case 2:
                id = PrimitiveSerializer.toByteArray((short) BindingManager.checkID(action.getRoute()));
                break;
            case 4:
                id = PrimitiveSerializer.toByteArray(BindingManager.checkID(action.getRoute()));
                break;
        }
        } catch (RouteException ex) {
            Logger.getLogger(ControlSubsocket.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        byte[] route = PrimitiveSerializer.toByteArray(action.getRoute());
        byte[] data = new byte[1 + id.length + route.length];
        data[0] = code;
        System.arraycopy(id, 0, data, 1, id.length);
        System.arraycopy(route, 0, data, BindingManager.getBytesRequiredToTransmit() + 1, route.length);
        sendData(data);
    }
    
    MPLXAction decodeByteArray(byte[] data) {
        int code = data[0];
        int id = 0;
        int brtt = BindingManager.getBytesRequiredToTransmit();
        switch(brtt) {
            case 1:
                id = (int) data[1];
                break;
            case 2:
                byte[] ashort = {data[1], data[2]};
                id = (int) PrimitiveSerializer.bytesToShort(ashort);
                break;
            case 4:
                byte[] anint = {data[1], data[2], data[3], data[4]};
                id = PrimitiveSerializer.bytesToInt(anint);
                break;
        }
        byte[] routedata = new byte[data.length - 1 - brtt];
        System.arraycopy(data, 1 + brtt, routedata, 0, routedata.length);
        String route = PrimitiveSerializer.bytesToString(routedata);
        MPLXAction action = new MPLXAction(MPLXAction.intToType(code), route, id);
        return action;
    }
    
    void sendConfirmPacket() {
        byte[] bytes = {-127, 1, 0, 0};
        for (byte b : bytes) {
            connection.writeByte(b);
        }
    }

    /**
     * The ControlSubsocket is not multiplexable, so this method does nothing.
     *
     * @return the Control Subsocket.
     */
    @Override
    public Subsocket enableMultiplexing() {
        // Do nothing;
        return this;
    }

    /**
     * The ControlSubsocket is not multiplexable, so this method does nothing.
     *
     * @return the Control Subsocket.
     */
    @Override
    public synchronized Subsocket disableMultiplexing() {
        // Do nothing
        return this;
    }
 
    /**
     * Will always throw a NotMultiplexedException for the ControlSubsocket,
     * because it is not multiplexable.
     *
     * @param route doesn't matter
     * @return won't ever return
     * @throws NotMultiplexedException always
     * @throws InvalidRouteException never
     */
    @Override
    public synchronized Subsocket createNewRoute(String route) throws NotMultiplexedException, InvalidRouteException {
        throw new NotMultiplexedException();
    }

    /**
     * Will always throw a NotMultiplexedException for the ControlSubsocket,
     * because it is not multiplexable.
     *
     * @param route doesn't matter
     * @throws NotMultiplexedException always
     * @throws InvalidRouteException never
     * @throws RootRouteException never
     */
    @Override
    public synchronized void destroyRoute(String route) throws InvalidRouteException, NotMultiplexedException, RootRouteException {
        throw new NotMultiplexedException();
    }
}
