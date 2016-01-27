/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 *
 * @author austin
 */
public class PIDDerivativeCalculator implements PIDSource{
    PIDSource pIDSource;
    DerivativeCalculator derivativeCalculator;

    public PIDDerivativeCalculator(PIDSource pIDSource) {
        if (pIDSource == null) throw new NullPointerException("pIDSource");
        this.pIDSource = pIDSource;
        derivativeCalculator = new DerivativeCalculator(10);
    }

    @Override
    public double pidGet() {
        return derivativeCalculator.calculate(pIDSource.pidGet());
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
