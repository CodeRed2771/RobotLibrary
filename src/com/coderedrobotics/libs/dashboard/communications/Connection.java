package com.coderedrobotics.libs.dashboard.communications;

import com.coderedrobotics.libs.dashboard.Debug;
import com.coderedrobotics.libs.dashboard.communications.exceptions.ConnectionResetException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.RouteException;
import com.coderedrobotics.libs.dashboard.communications.listeners.ConnectionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main Connection Thread that manages the TCP connection, sends and listens
 * for data, as well as many other things.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class Connection {

    private ServerSocket serverSocket;
    private Socket tcpConnection;
    private BufferedInputStream input;
    private NonblockingOutputBuffer output;

    private final ControlSubsocket controlSocket;
    private final RootSubsocket rootSocket;

    private static Connection connection = null;
    private final ConnectionThread privateStuff;
    private Thread thread;
    private final MultiplexingManager multiplexingManager;

    private static final CopyOnWriteArrayList<ConnectionListener> connectionListeners = new CopyOnWriteArrayList<>();

    private static boolean connected;

    private Connection() {
        try {
            serverSocket = new ServerSocket(1180);
        } catch (IOException ex) {
            System.exit(1180);
        }
        rootSocket = new RootSubsocket("root", this);
        controlSocket = new ControlSubsocket("control", this);
        privateStuff = new ConnectionThread();
        multiplexingManager = new MultiplexingManager(controlSocket, rootSocket);
        multiplexingManager.mode = MultiplexingManager.Mode.RUN;
        controlSocket.addListener(multiplexingManager);
        start();
    }

    private void start() {
        thread = new Thread(privateStuff);
        thread.start();
    }

    /**
     * Returns the instance of the Connection object. If there is not currently
     * running Connection instance, one is created.
     *
     * @return a Connection object instance.
     */
    public static Connection getInstance() {
        if (connection == null) {
            connection = new Connection();
        }
        return connection;
    }

    /**
     * Receive alerts for when the network becomes connected or disconnected.
     *
     * @param listener a ConnectionListener
     */
    public synchronized static void addConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
        connectionListeners.add(listener);
        if (connected) {
            listener.connected();
        } else {
            listener.disconnected();
        }
    }

    /**
     * Stop receiving alerts for when the network becomes connected or
     * disconnected.
     *
     * @param listener a ConnectionListener
     */
    public static void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    private synchronized static void alertConnected() {
        connected = true;
        Debug.println("[NETWORK] Connected", Debug.EXTENDED);
        for (ConnectionListener listener : connectionListeners) {
            listener.connected();
        }
    }

    private synchronized static void alertDisconnected() {
        connected = false;
        for (ConnectionListener listener : connectionListeners) {
            listener.disconnected();
        }
        Debug.println("[NETWORK] Disconnected", Debug.EXTENDED);
    }

    /**
     * Get the root Subsocket.
     *
     * @return the root Subsocket.
     * @see RootSubsocket
     */
    public RootSubsocket getRootSubsocket() {
        return rootSocket;
    }

    @SuppressWarnings("SleepWhileInLoop")
    private void reconnect() {
        alertDisconnected();

        multiplexingManager.mode = MultiplexingManager.Mode.SYNC;
        
        boolean retry = true;
        while (retry) {
            retry = false;
            try {
                if (input != null) {//close if open
                    input.close();
                }
                if (output != null) {//close if open
                    output.close();
                }
                if (tcpConnection != null) {//close if open
                    tcpConnection.close();
                }
            } catch (IOException ex) {
            }
            tcpConnection = null;
            while (tcpConnection == null) {//keeps trying to connect
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Connection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                try {
                    Debug.println("[NETWORK] Waiting for connection", Debug.EXTENDED);
                    tcpConnection = serverSocket.accept();
                } catch (IOException ex) {
                    tcpConnection = null;
                    Debug.println("[NETWORK] Error connecting.", Debug.EXTENDED);
                }
            }
            //setup the reader and writer objects
            try {
                output = new NonblockingOutputBuffer(tcpConnection.getOutputStream());
                input = new BufferedInputStream(tcpConnection.getInputStream());
            } catch (IOException ex) {
                retry = true;
            }
        }

        controlSocket.allowWriting();
        multiplexingManager.sendMultiplexingActions();
//        controlSocket.sendConfirmPacket();
        
        // DON'T ALLOW ANY OTHER TRAFFIC UNTIL WE KNOW ABOUT THE CURRENT TREE STRUCTURE:
//        boolean run = true;
//        boolean serverDone = false;
//        while (run) {
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException ex) {
//            }
//
//            flush();
//
//            try {
//                while (input.available() > 0 || tcpConnection.getInputStream().available() > 0) {
//                    int id = readSubsocketID();
////                    System.out.println(id);
//                    byte[] length = {readByte(), readByte()};
//                    int l = (int) (PrimitiveSerializer.bytesToChar(length));
//                    byte[] data = new byte[(int) (PrimitiveSerializer.bytesToChar(length))];
////                    System.out.println(Arrays.toString(length));
////                    System.out.println(data.length);
//                    for (int j = 0; j < data.length; j++) {
//                        data[j] = readByte();
////                        System.out.println(data[j]);
//                    }
//
//                    try {
//                        String route = BindingManager.getRoute(id);
//                        if ("control".equals(route)) {
//                            if (data[0] == 0) {
//                                controlSocket.sendConfirmPacket();
//                                run = false;
//                                break;
//                            } else {
//                                controlSocket.pushData(data);
//                            }
//                        } else {
//                            Debug.println("WE GOT SOMETHING ELSE: " + route, Debug.CRITICAL);
//                        }
//                    } catch (RouteException ex) {
//                        Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            } catch (ConnectionResetException ex) {
//            } catch (IOException ex) {
//                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
//                reconnect();
//            }
//        }
//
//        multiplexingManager.mode = MultiplexingManager.Mode.RUN;
        Debug.println("BEGINNING ALERTS", Debug.STANDARD);
//        BindingManager.printBindings();
        
        alertConnected();
    }

    public void disconnect() {
        alertDisconnected();
        byte[] data = {5}; // 5 = bye
        controlSocket.sendData(data);
    }

    synchronized byte readByte() throws ConnectionResetException {
        if (tcpConnection == null) {
            reconnect();
        }
        try {
            return (byte) (input.read());
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    void writeByte(byte b) {
        try {
            output.writeByte(b);
            //System.out.println("WRITE: " + b);
        } catch (NullPointerException ex) {

        }
    }

    void writeBytes(byte[] toByteArray) {
        for (byte b : toByteArray) {
            writeByte(b);
        }
    }

    void flush() {
        if (tcpConnection == null) {
            reconnect();
        }
        try {
            output.flush();
        } catch (IOException ex) {
            reconnect();
        }
    }

//    private int readSubsocketID() {
//        switch (BindingManager.getBytesRequiredToTransmit()) {
//            case 1:
//                return (int) readByte();
//            case 2:
//                byte[] data = {readByte(), readByte()};
//                return (int) (PrimitiveSerializer.bytesToShort(data));
//            case 4:
//                byte[] bytes = {readByte(), readByte(), readByte(), readByte()};
//                return PrimitiveSerializer.bytesToInt(bytes);
//        }
//        return 0;
//    }

    /**
     * This stuff should <b>never</b> be called outside of the Connection object
     * or the communications package.
     */
    private class ConnectionThread implements Runnable {

        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }

                flush();

                try {
                    while (input.available() > 0 || tcpConnection.getInputStream().available() > 0) {
//                        int id = readSubsocketID();
                        byte[] routelength = {readByte(), readByte()};
                        byte[] routedata = new byte[PrimitiveSerializer.bytesToShort(routelength)];
                        for (int k = 0; k < routedata.length; k++) {
                            routedata[k] = readByte();
                        }
                        byte[] length = {readByte(), readByte()};
                        int l = (int) (PrimitiveSerializer.bytesToChar(length));
                        byte[] data = new byte[(int) (PrimitiveSerializer.bytesToChar(length))];
                        for (int j = 0; j < data.length; j++) {
                            data[j] = readByte();
                        }
                        try {
//                            String route = BindingManager.getRoute(id);
                            String route = PrimitiveSerializer.bytesToString(routedata);
                            if ("control".equals(route)) {
                                controlSocket.pushData(data);
                            } else {
                                rootSocket.getSubsocket(route).pushData(data);
                            }
                        } catch (RouteException ex) {
                            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (ConnectionResetException ex) {
                } catch (IOException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                    reconnect();
                }
            }
        }
    }
}

/**
 * Differences between this and the client side:
 *
 * CONSTRUCTOR:
 *
 * private Connection() { idFactory = new IDFactory(this); rootSocket = new
 * RootSubsocket("root", this); controlSocket = new ControlSubsocket("control",
 * this); setupListeners(); start(); }
 *
 *
 *
 * RECONNECT():
 *
 * @SuppressWarnings("SleepWhileInLoop") private void reconnect() {
 * alertDisconnected();
 *
 * boolean retry = true; while (retry) { retry = false; try { if (input != null)
 * {//close if open input.close(); } if (output != null) {//close if open
 * output.close(); } if (tcpConnection != null) {//close if open
 * tcpConnection.close(); } } catch (IOException ex) { } tcpConnection = null;
 * while (tcpConnection == null) {//keeps trying to connect try {
 * Thread.sleep(200); } catch (InterruptedException ex) {
 * Logger.getLogger(Connection.class.getName()).log(java.util.logging.Level.SEVERE,
 * null, ex); } try { tcpConnection = new Socket("localhost", 1180); } catch
 * (IOException ex) { System.out.println("couldn't connect"); } } //setup the
 * reader and writer objects try { output = new
 * BufferedOutputStream(tcpConnection.getOutputStream()); input = new
 * BufferedInputStream(tcpConnection.getInputStream()); } catch (IOException ex)
 * { retry = true; } }
 *
 * alertConnected(); } *
 */
