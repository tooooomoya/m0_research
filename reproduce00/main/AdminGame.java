import java.util.ArrayList;
import java.util.List;

import com.gurobi.gurobi.GRBException;

import main.utils.matrix_util;
import main.utils.optimization;

public class AdminGame {

    public static Result am(double[][] A, double[] s, double lam, boolean reducePls, double gam, int maxIter,
            boolean existing) {
        double[][] W = matrix_util.copyMatrix(A);
        // System.out.println("the first W matrix");
        // matrix_util.printMatrix(W);

        // First, each user change its opinion according to the FJ model
        // System.out.println("\nthe first z: ");
        // matrix_util.printVector(s);
        double[] z = optimization.minZ(W, s);
        // System.out.println("\nz after the FJ effect: ");
        // matrix_util.printVector(z);

        ArrayList<Double> pls = new ArrayList<>();
        pls.add(optimization.computePls(z));
        // System.out.println("\npls before iteration: "+ optimization.computePls(z));

        double[][] L = matrix_util.createL(W, W.length);

        ArrayList<Double> disaggs = new ArrayList<>();
        disaggs.add(computeDisagreement(z, L));
        // System.out.println("\ndisagg before iteration: "+computeDisagreement(z, L));

        int i = 0;
        boolean flag = true;
        double[][] Wnew = null;

        while (flag) {
            System.out.println("--------------------------");
            System.out.println("Iteration:" + i);

            try {
                // Admin changes weight matrix
                Wnew = optimization.minWGurobi(z, lam, A, reducePls, gam, existing);
                // System.out.println("\nnew W matrix");
                // matrix_util.printMatrix(Wnew);
                // ここのWがAだと最初の重み状態からの変化で、あんま意味ない気がする。
            } catch (GRBException e) {
                System.out.println("Gurobi optimization error: " + e.getMessage());
                e.printStackTrace();
            }

            // Wnew = W;

            // After Admin action, each user change its opinion according to the FJ model
            // System.out.println("\nz before this time Admin effect: ");
            // matrix_util.printVector(z);
            double[] znew = optimization.minZ(Wnew, s);
            // System.out.println("\nNew z after Admin effect: ");
            // matrix_util.printVector(znew);

            // Terminal Criterion(both z and W can be considered to be converged, or maxIter
            // criterion)
            System.out.println("z-znew:\n" + norm(z, znew));
            System.out.println("W-Wnew:\n" + matrixNorm(W, Wnew));
            if (Math.max(norm(z, znew), matrixNorm(W, Wnew)) < 5e-1 || i > maxIter - 1) {
                System.out.println("\nTerminal Criterion!!!!!!!");
                flag = false;
            }

            z = znew;
            W = Wnew;
            i++;
            //double PLS = optimization.computePls(z);

            double PLS = calculateDiversity(znew, Wnew);

            System.out.println("\npls: " + PLS);
            pls.add(PLS);
            L = matrix_util.createL(W, W.length);
            double disagg = computeDisagreement(z, L);
            System.out.println("\ndisagg: " + disagg);
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
                sumSquaredDifferences += difference * difference;
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

    private static double calculateDiversity(double[] z, double[][] W) {
        double[] z_diversity = new double[z.length];

        for (int i = 0; i < z.length; i++) {
            double my_opinion = 0;
            my_opinion = z[i] - 0.5;
            double adjacency_opinion_sum = 0;
            double adjacency_sum =0;

            if (my_opinion > 0.5) {
                for (int j = 0; j < z.length; j++) {
                    
                    if(W[i][j] > 0){
                        
                        adjacency_sum += W[i][j];
                    if (W[i][j] < 0.5) {
                        adjacency_opinion_sum += 1;
                    }
                }
                }
            } else if (my_opinion < 0.5) {
                for (int j = 0; j < z.length; j++) {
        
                    if(W[i][j] > 0){
                        
                        adjacency_sum += W[i][j];
                    if (W[i][j] > 0.5) {
                        adjacency_opinion_sum += 1;
                    }
                }
                }
            }
            z_diversity[i] = adjacency_opinion_sum /adjacency_sum;
        }

        double diversity = 0;
        for(int i=0; i< z.length; i++){
            double temp=0;
            temp+=z_diversity[i];
            diversity = temp / z_diversity.length;
        }


        return diversity;
    }
}
