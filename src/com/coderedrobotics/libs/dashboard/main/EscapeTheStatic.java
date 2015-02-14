package com.coderedrobotics.libs.dashboard.main;

import com.coderedrobotics.libs.dashboard.Debug;
import com.coderedrobotics.libs.dashboard.api.gui.Bar;
import com.coderedrobotics.libs.dashboard.api.gui.Compass;
import com.coderedrobotics.libs.dashboard.api.gui.Graph;
import com.coderedrobotics.libs.dashboard.api.gui.Indicator;
import com.coderedrobotics.libs.dashboard.api.gui.OverrideValue;
import com.coderedrobotics.libs.dashboard.api.gui.StatusLabel;
import com.coderedrobotics.libs.dashboard.communications.Connection;
import com.coderedrobotics.libs.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.libs.dashboard.communications.Subsocket;
import com.coderedrobotics.libs.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.libs.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.libs.dashboard.communications.listeners.ConnectionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class EscapeTheStatic implements ConnectionListener, Runnable {

    Indicator indicator;
    StatusLabel statusLabel;
    StatusLabel pwmBarStatus;
    Bar bar1;
    Bar bar2;
    Bar bar3;
    Compass compass;
    OverrideValue ov;
    Subsocket s;
    Graph g;

    Thread t = null;

    public EscapeTheStatic() {
        Connection.addConnectionListener(this);
        indicator = new Indicator("root.testnewindicator");
        statusLabel = new StatusLabel("root.testnewstatuslabel");
        compass = new Compass("root.testnewcompass");
        bar1 = new Bar("root.testnewbar");
        bar2 = new Bar("root.testnewbar2");
        bar3 = new Bar("root.testnewbar3");
        pwmBarStatus = new StatusLabel("root.pwmbarstatus");
        ov = new OverrideValue("root.overridevalue", 10.23);
    }

    @Override
    public void connected() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void disconnected() {

    }

    @Override
    public void run() {
        Debug.println("ROBOT SIDE", Debug.EVERYTHING);
        try {
            s = Connection.getInstance().getRootSubsocket().enableMultiplexing().createNewRoute("root.graph");
        } catch (NotMultiplexedException ex) {
            Logger.getLogger(EscapeTheStatic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidRouteException ex) {
            Logger.getLogger(EscapeTheStatic.class.getName()).log(Level.SEVERE, null, ex);
        }
        int count = 0;
        double pwm = -1;
        while (true) {
            if (count == 0) {
                indicator.toggleValue();
                indicator.setText(String.valueOf(indicator.getState()));
                statusLabel.setText(indicator.getText());
            }
            compass.setAngle(compass.getAngle() + 1);
            bar1.setPercentage(compass.getAngle());
            bar2.setPercentage(bar1.getPercentage());
            bar3.setPercentage(pwm);
            pwmBarStatus.setText(String.valueOf(pwm));
            count++;
            s.sendData(PrimitiveSerializer.toByteArray((double) count / 100));
            if (count == 50) {
                count = 0;
                System.out.println(ov.getValue());
            }
            pwm += .01;
            if (pwm > 1) {
                pwm = -1;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(EscapeTheStatic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
