package com.coderedrobotics.libs.dashboard.main;

import com.coderedrobotics.libs.dashboard.Debug;
import com.coderedrobotics.libs.dashboard.communications.Connection;

/**
 *
 * @author Michael
 */
public class Start {

    private static EscapeTheStatic ets;

    public static void main(String[] args) {
        Connection.getInstance(); // start connection thread
        ets = new EscapeTheStatic();
        Debug.setDebugLevel(Debug.CRITICAL);
    }
}
