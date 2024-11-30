package main.structure;

import java.util.ArrayList;

public class Result {
    private ArrayList<Double> pls;
    private ArrayList<Double> disaggs;
    private ArrayList<Double> gppls;
    private ArrayList<Double> stfs;
    private ArrayList<Double> udv;
    private ArrayList<Double> cdv;
    private double[] z;
    private double[][] W;
    private boolean finderror;
    private double weight_added;

    public Result(ArrayList<Double> pls, ArrayList<Double> disaggs, ArrayList<Double> gppls, ArrayList<Double> stfs, ArrayList<Double> udv, ArrayList<Double> cdv, double[] z, double[][] W,
            boolean finderror, double weight_added) {
        this.pls = pls;
        this.disaggs = disaggs;
        this.gppls = gppls;
        this.stfs = stfs;
        this.udv = udv;
        this.cdv = cdv;
        this.z = z;
        this.W = W;
        this.finderror = finderror;
        this.weight_added = weight_added;
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

    public ArrayList<Double> getUdv() {
        return udv;
    }

    public void setUdv(ArrayList<Double> udv) {
        this.udv = udv;
    }

    public ArrayList<Double> getCdv() {
        return cdv;
    }

    public void setCdv(ArrayList<Double> cdv) {
        this.cdv = cdv;
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

    public double getWeightadded(){
        return weight_added;
    }

    public void setWeightadded(double weight_added){
        this.weight_added = weight_added;
    }
}
