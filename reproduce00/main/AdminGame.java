import java.util.ArrayList;

import com.gurobi.gurobi.GRBException;

import main.utils.matrix_util;
import main.utils.optimization;

public class AdminGame {

    public static Result am(double[][] A, double[] s, double lam, boolean reducePls, double gam, int maxIter, boolean existing) {
        double[][] W = matrix_util.copyMatrix(A);
        //System.out.println("the first W matrix");
        //matrix_util.printMatrix(W);

        // First, each user change its opinion according to the FJ model
        System.out.println("the first z: ");
        matrix_util.printVector(s);
        double[] z = optimization.minZ(W, s);
        System.out.println("the first z: ");
        matrix_util.printVector(z);

        ArrayList<Double> pls = new ArrayList<>();
        pls.add(optimization.computePls(z));
        System.out.println("pls before iteration: "+ optimization.computePls(z));

        double[][] L = matrix_util.createL(W, W.length);

        ArrayList<Double> disaggs = new ArrayList<>();
        disaggs.add(computeDisagreement(z, L));
        System.out.println("disagg before iteration: "+computeDisagreement(z, L));

        int i = 0;
        boolean flag = true;
        double[][] Wnew = null;

        while (flag) {
            System.out.println("--------------------------");
            System.out.println("Iteration:" + i);

            try {
                // Admin changes weight matrix
                Wnew = optimization.minWGurobi(z, lam, A, reducePls, gam, existing);
                System.out.println("new W matrix");
                matrix_util.printMatrix(Wnew);
                // ここのWがAだと最初の重み状態からの変化で、あんま意味ない気がする。
            } catch (GRBException e) {
                System.out.println("Gurobi optimization error: " + e.getMessage());
                e.printStackTrace();
            }
            // After Admin action, each user change its opinion according to the FJ model
            double[] znew = optimization.minZ(Wnew, s);
            System.out.println("New Z: ");
            matrix_util.printVector(znew);

            // Terminal Criterion(both z and W can be considered to be converged, or maxIter
            // criterion)
            if (Math.max(norm(z, znew), matrixNorm(W, Wnew)) < 5e-1 || i > maxIter - 1) {
                System.out.println("Terminal Criterion!!!!!!!");
                flag = false;
            }

            z = znew;
            W = Wnew;
            i++;
            double PLS = optimization.computePls(z); 
            System.out.println("pls: "+ PLS);
            pls.add(PLS);
            L = matrix_util.createL(W, W.length);
            double disagg = computeDisagreement(z, L);
            System.out.println("disagg: "+disagg);
            disaggs.add(disagg);
        }
        return new Result(pls, disaggs, z, W);
    }

    // caluculate norm (ノルム：距離)
    private static double norm(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    // Calculate the Frobenius norm of a matrix
    private static double matrixNorm(double[][] matrix1, double[][] matrix2) {
        if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length) {
            throw new IllegalArgumentException("Matrices must be of the same dimensions.");
        }

        double sumSquaredDifferences = 0.0;

        // Calculate the sum of squared differences of corresponding elements
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[i].length; j++) {
                double difference = matrix1[i][j] - matrix2[i][j];
                sumSquaredDifferences = difference * difference;
            }
        }

        // Return the square root of the sum of squared differences
        return Math.sqrt(sumSquaredDifferences);
    }

    // calculate z^T * L * z: Global disagreement is scalar
    private static double computeDisagreement(double[] z, double[][] L) {
        double[] temp = new double[z.length];
        double disagreement = 0.0;

        // calculate L * z
        for (int i = 0; i < z.length; i++) {
            for (int j = 0; j < z.length; j++) {
                temp[i] += L[i][j] * z[j];
            }
        }

        // then calculate z^T * temp
        for (int i = 0; i < z.length; i++) {
            disagreement += z[i] * temp[i];
        }

        return disagreement;
    }
}
