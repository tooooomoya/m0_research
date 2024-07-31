import java.util.HashMap;

public class ResultPair {
    private HashMap<Double, Result> rd;
    private HashMap<Double, Result> rdFix;

    public ResultPair(HashMap<Double, Result> rd, HashMap<Double, Result> rdFix) {
        this.rd=rd;
        this.rdFix=rdFix;
    }

    // getter and setter
    public HashMap<Double, Result> getRd() {
        return rd;
    }

    public void setRd(HashMap<Double, Result> rd) {
        this.rd = rd;
    }

    public HashMap<Double, Result> getRdFix() {
        return rdFix;
    }

    public void setRdFix(HashMap<Double, Result> rdFix) {
        this.rdFix = rdFix;
    }
}
