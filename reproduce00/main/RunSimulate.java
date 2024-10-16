import java.util.HashMap;
import java.util.Map;

public class RunSimulate{
    private double[][] A;
    private double[] s;

    public RunSimulate(double[][] A, double[] s){
        this.A = A;
        this.s = s;
    }

    public ResultPair runDynamics(){
        // because of time issues, we run the NA dynamics separately 
        double[] lamList = {0.1, 0.2, 0.3, 0.4, 0.5};
        // double lamList = [0.6, 0.7, 0.8, 0.9, 1.0];
        HashMap<Double, Result> rd = new HashMap<>();       // results of NA dynamics
        HashMap<Double, Result> rdFix = new HashMap<>();    // results of regularized NA dynamics

        int maxIter = 7;
        double gam = 0.2; // L2 regularization coefficient 

        for(int i = 0; i<lamList.length; i++){
            System.out.println("---------------Start the Experiment with lambda:" + lamList[i]);

            System.out.println(("no fix"));
            System.out.println("lam:" + lamList[i]);
            Result resultNoFix = AdminGame.am(A, s, lamList[i], false, 0, maxIter, false);
            // no fix -> gamma = 0
            
            System.out.println("with fix");
            System.out.println("lam:" + lamList[i]);
            Result resultFix = AdminGame.am(A, s, lamList[i], true, gam, maxIter, false);

            rd.put(lamList[i], resultNoFix);
            rdFix.put(lamList[i], resultFix);
        }

        ResultPair resultPair = new ResultPair(rd, rdFix);

        return resultPair;
    }
}