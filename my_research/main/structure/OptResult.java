package main.structure;

public class OptResult {
    private boolean Opt;
    private double[][] W;

    public OptResult(boolean Opt, double[][] W) {
        this.Opt = Opt;
        this.W = W;
    }

    // getter and setter
    public boolean getOpt() {
        return Opt;
    }

    public void setOpt(boolean Opt) {
        this.Opt = Opt;
    }


    public double[][] getW() {
        return W;
    }

    public void setW(double[][] W) {
        this.W = W;
    }
}
