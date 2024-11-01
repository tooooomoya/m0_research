package main.structure;

import java.util.ArrayList;

public class Result {
    private ArrayList<Double> pls;
    private ArrayList<Double> disaggs;
    private double[] z;
    private double[][] W;

    public Result(ArrayList<Double> pls, ArrayList<Double> disaggs, double[] z, double[][] W) {
        this.pls = pls;
        this.disaggs = disaggs;
        this.z = z;
        this.W = W;
    }

    // getter and setter
    public ArrayList<Double> getPls() {
        return pls;
    }

    public void setPls(ArrayList<Double> pls) {
        this.pls = pls;
    }

    public ArrayList<Double> getDisaggs() {
        return disaggs;
    }

    public void setDisaggs(ArrayList<Double> disaggs) {
        this.disaggs = disaggs;
    }

    public double[] getZ() {
        return z;
    }

    public void setZ(double[] z) {
        this.z = z;
    }

    public double[][] getW() {
        return W;
    }

    public void setW(double[][] W) {
        this.W = W;
    }
}
