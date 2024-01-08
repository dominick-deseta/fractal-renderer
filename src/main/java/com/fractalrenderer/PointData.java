package com.fractalrenderer;

public class PointData {
    private double z_real;
    private double z_imag;
    private double c_real;
    private double c_imag;
    private boolean breached;
    private int iterations;

    public PointData(double c_real, double c_imag) {
        resetTo(c_real, c_imag);
    }

    public void resetTo(double c_real, double c_imag) {
        this.c_real = c_real;
        this.c_imag = c_imag;
        this.z_real = c_real;
        this.z_imag = c_imag;
        this.breached = false;
        this.iterations = 0;
    }

    public void iterate() {
        if (breached) {
            return;
        } else {
            iterations++;
        }
        double z_real0 = z_real;
        double z_imag0 = z_imag;
        z_real = z_real0*z_real0- z_imag0*z_imag0 + c_real;
        z_imag = z_real0*z_imag0*2 + c_imag;
        if (z_real*z_real + z_imag*z_imag > 4) {
            breached = true;
        }
    }

    public int getIterations() {
        return iterations;
    }

    public boolean getBreached() { return breached; }

    public double getC_Real() { return c_real; }

    public double getC_Imag() {
        return c_imag;
    }

    public double getZ_Real() { return z_real; }

    public double getZ_Imag() {
        return z_imag;
    }
}
