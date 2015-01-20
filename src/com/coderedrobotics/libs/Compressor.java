package com.coderedrobotics.libs;

/**
 *
 * @author Michael
 */
public class Compressor {
    
    private edu.wpi.first.wpilibj.Compressor compressor;
    private final boolean virtualized;
    
    public Compressor() {
        virtualized = VirtualizationController.getInstance().isVirtualizationEnabled();
        if (virtualized) {
            VirtualizationController.getInstance().addCompressor(this);
        } else {
            compressor = new edu.wpi.first.wpilibj.Compressor();
        }
    }
    
    public void start() {
        if (virtualized) {
            VirtualizationController.getInstance().setCompressor(true);
        } else {
            compressor.start();
        }
    }
    
    public void stop() {
        
        if (virtualized) {
            VirtualizationController.getInstance().setCompressor(false);
        } else {
            compressor.stop();
        }
    }
    
    public boolean getClosedLoopControl() {
        if (!virtualized) {
            return compressor.getClosedLoopControl();
        } else {
            return true;
        }
    }
    
    public boolean getPressureSwitchValve() {
        if (!virtualized) {
            return compressor.getPressureSwitchValue();
        } else {
            return false;
        }
    }
    
    public edu.wpi.first.wpilibj.Compressor getWPICompressor() {
        return compressor;
    }
}
