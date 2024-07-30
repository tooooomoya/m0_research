package machines;

import utils.matrix_util;

public class optimization {
    public static double[] minZ(double[][] W, double[] s){
        double[][] L = matrix_util.createL(W, W.length);
        int n = W.length;
        double[][] LPlusI = matrix_util.add(W, L);

        // z = (LPlusI)^-1 * s : calculateLE solves this linear equation
        z = calculateLE();
        return z;
    }

    public static double[][] minW(double[] z, double lam, double[][] A, boolean reducePls, double gam, boolean existing){
        
        return new double[A.length][A[0].length];
    }

    public static double computePls(double[] z){
        double sum = 0.0;
        for (double value : z){
            sum += value;
        }
        double ZMean = sum / z.length;
        
        double sumSquareDifferences = 0.0;
        for(double value : z){
            double difference = value - ZMean;
            sumSquareDifferences += difference * difference;
        }
        return sumSquareDifferences / z.length;
    }
}
