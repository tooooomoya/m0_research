package main.structure;

import java.util.ArrayList;

public class Result {
    private ArrayList<Double> pls;
    private ArrayList<Double> disaggs;
    private ArrayList<Double> gppls;
    private ArrayList<Double> stfs;
    private ArrayList<Double> dvs;
    private double[] z;
    private double[][] W;
    private boolean finderror;

    public Result(ArrayList<Double> pls, ArrayList<Double> disaggs, ArrayList<Double> gppls, ArrayList<Double> stfs, ArrayList<Double> dvs, double[] z, double[][] W,
            boolean finderror) {
        this.pls = pls;
        this.disaggs = disaggs;
        this.gppls = gppls;
        this.stfs = stfs;
        this.dvs = dvs;
        this.z = z;
        this.W = W;
        this.finderror = finderror;
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

    public ArrayList<Double> getGppls() {
        return gppls;
    }

    public void setGppls(ArrayList<Double> gppls) {
        this.gppls = gppls;
    }

    public ArrayList<Double> getStfs() {
        return stfs;
    }

    public void setStfs(ArrayList<Double> stfs) {
        this.stfs = stfs;
    }

    public ArrayList<Double> getDvs() {
        return dvs;
    }

    public void setDvs(ArrayList<Double> dvs) {
        this.dvs = dvs;
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

    public boolean getFindError() {
        return finderror;
    }

    public void setFindError(boolean finderror) {
        this.finderror = finderror;
    }
}
