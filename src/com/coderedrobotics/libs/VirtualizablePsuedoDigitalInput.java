package com.coderedrobotics.libs;

import com.coderedrobotics.libs.dash.DashBoard;

/**
 *
 * @author michael
 */
public class VirtualizablePsuedoDigitalInput extends VirtualizableAnalogInput {

    private DashBoard board;
    
    public VirtualizablePsuedoDigitalInput(int port, DashBoard board) {
        super(port);
        this.board = board;
    }

    public boolean getState() {
        board.prtln(""+get(), 5);
        return super.get() > 2;
    }
}
