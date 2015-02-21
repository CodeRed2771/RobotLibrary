/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.libs.dash;

/**
 *
 * @author laptop
 */
public class DashBoard {

    DataStreamingModule dataStreamingModule;
    Connection connection;
    CommunicationsThread communicationsThread;
    SynchronizedRegisterArray synchronizedRegisterArray;
    JaguarMonitor jaguarMonitor;
    DashBoard dashBoard;
    SidecarMonitor sidecarMonitor;
    
    private static String connectionAddress;

    public DashBoard() {
        dataStreamingModule = new DataStreamingModule();
        jaguarMonitor = new JaguarMonitor(dataStreamingModule);
        connection = new Connection(connectionAddress);
        synchronizedRegisterArray = new SynchronizedRegisterArray();
        communicationsThread = new CommunicationsThread(
                connection,
                synchronizedRegisterArray,
                dataStreamingModule);
        sidecarMonitor = new SidecarMonitor(dataStreamingModule, 12);
    }

    public static void setConnectionAddress(String address){
        connectionAddress = address;
    }
    
    public void streamPacket(double val, String name) {
        dataStreamingModule.sendPacket(val, name);
    }

    public double getRegister(String name) {
        return synchronizedRegisterArray.get(name);
    }

    public void setRegister(String name, double val) {
        synchronizedRegisterArray.setRegister(name, val);
    }

    public void monitorJaguar(Jaguar j) {
        jaguarMonitor.addJaguar(j);
    }

    public synchronized void prtln(String ln, int channel) {
        if (channel == synchronizedRegisterArray.get("DEBUGCHANNEL")) {
            for (int i = 0; i < ln.length(); i++) {
                streamPacket(ln.charAt(i), "DEBUG");
            }
            streamPacket('\n', "DEBUG");
        }
        if (0 == synchronizedRegisterArray.get("DEBUGCHANNEL")) {
            for (int i = 0; i < ln.length(); i++) {
                streamPacket(ln.charAt(i), "DRIVERCONSOLE");
            }
            streamPacket('\n', "DRIVERCONSOLE");
        }
    }
}