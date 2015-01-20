/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.libs.dash;

import edu.wpi.first.wpilibj.DigitalModule;

/**
 *
 * @author laptop
 */
class SidecarMonitor implements Runnable {

    DataStreamingModule dataStreamingModule;
    int hz;

    public SidecarMonitor(DataStreamingModule dataStreamingModule, int hz) {
        this.dataStreamingModule = dataStreamingModule;
        this.hz = hz;
        new Thread(this).start();
    }

    public void run() {
        while (true) {
            DigitalModule dm = DigitalModule.getInstance(
                    DigitalModule.getDefaultDigitalModule());
            for (int i = 0; i < 10; i++) {
                dataStreamingModule.sendPacket(
                        dm.getPWM(i+1), "PWM" + i);
            }
            for (int i = 0; i < 10; i++) {
                dataStreamingModule.sendPacket(
                        dm.getDIODirection(i) ? 1 : 0, "DIOD" + i);
                dataStreamingModule.sendPacket(
                        dm.getDIO(i) ? 1 : 0, "DIOS" + i);
            }
            for (int i = 0; i < 10; i++) {
                dataStreamingModule.sendPacket(
                        dm.getRelayForward(i) ? 1 : 0, "RelayF" + i);
                dataStreamingModule.sendPacket(
                        dm.getRelayReverse(i) ? 1 : 0, "RelayR" + i);
            }
            try {
                Thread.sleep((long) (1000d / hz));
            } catch (InterruptedException ex) {
            }
        }
    }
}
