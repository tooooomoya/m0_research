import java.util.ArrayList;
import java.util.List;

import com.gurobi.gurobi.GRBException;

import main.utils.calculater;
import main.utils.calculater;
import main.utils.calculater;
import main.utils.calculater;
import main.utils.calculater;
import main.utils.*;
import main.structure.*;

public class AdminGame {

    public static Result am(double[][] A, double[] s, double lam, boolean reducePls, double gam, int maxIter,
            boolean existing, boolean random) {
        double[][] W = matrix_util.copyMatrix(A);
        // System.out.println("the first W matrix");
        // matrix_util.printMatrix(W);

        // First, each user change its opinion according to the FJ model
        // System.out.println("\nthe first z: ");
        // matrix_util.printVector(s);
        double[] z = optimization.minZ(W, s);
        // System.out.println("\nz after the FJ effect: ");
        // matrix_util.printVector(z);

        ///// Set pls
        ArrayList<Double> pls = new ArrayList<>();
        pls.add(optimization.computePls(z));
        // System.out.println("\npls before iteration: "+ optimization.computePls(z));


        ///// Set disaggs
        double[][] L = matrix_util.createL(W, W.length);
        ArrayList<Double> disaggs = new ArrayList<>();
        disaggs.add(computeDisagreement(z, L));
        // System.out.println("\ndisagg before iteration: "+computeDisagreement(z, L));

        ///// Set gppls
        ArrayList<Double> gppls = new ArrayList<>();
        gppls.add(calculater.computeGpPls(z));

        ///// Set stfs
        ArrayList<Double> stfs = new ArrayList<>();
        stfs.add(calculater.computeStf(z, W));

        ///// Set dvs
        ArrayList<Double> dvs = new ArrayList<>();
        dvs.add(calculater.computeDvs(z, W));
        
        int i = 0;
        boolean flag = true;
        double[][] Wnew = null;
        boolean finderror = false;

        while (flag) {
            System.out.println("--------------------------");
            System.out.println("Iteration:" + i);

            try {
                // Admin changes weight matrix
                OptResult optResult = optimization.minWGurobi(z, lam, W, reducePls, gam, existing);
                finderror = optResult.getOpt();
                Wnew = optResult.getW();

                //System.out.println("\nnew W matrix");
                //matrix_util.printMatrix(Wnew);
                // ここのWがAだと最初の重み状態からの変化で、あんま意味ない気がする。
            } catch (GRBException e) {
                System.out.println("Gurobi optimization error: " + e.getMessage());
                e.printStackTrace();
            }

            if(random){
            /// My Method : randomly add weight
            List<int[]> selectedPairs = new ArrayList<>();
            selectedPairs = calculater.selectPairs_v0(Wnew,z);
            for (int[] pair : selectedPairs){
                Wnew[pair[0]][pair[1]] += 1;
            }
        }

            /// confirm the maximum weight
            double max_w = 0.0;
            for(int ii=0; ii<z.length;ii++){
                for(int j=0; j<z.length; j++){
                    if(Wnew[i][j] > max_w){
                        max_w = Wnew[ii][j];
                    }
                }
            }
            System.out.println("\nMaximum Weight of W matrix : " + max_w);

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
            

            //double PLS = calculateDiversity(znew, Wnew);

            double PLS = optimization.computePls(z);
            pls.add(PLS);
            System.out.println("\npls: " + PLS);

            L = matrix_util.createL(W, W.length);
            double disagg = computeDisagreement(z, L);
            disaggs.add(disagg);
            System.out.println("\ndisagg: " + disagg);

            double GPPLS = calculater.computeGpPls(z);
            gppls.add(GPPLS);
            System.out.println("\ngppls: " + GPPLS);

            double stf = calculater.computeStf(z, W);
            stfs.add(stf);
            System.out.println("\nstf: " + stf);

            double DVS = calculater.computeDvs(z, W);
            dvs.add(DVS);
            System.out.println("\ndvs: "+DVS);

            int a = 0, b = 0, c = 0, d = 0;
            for (int t = 0; t < z.length; t++) {
                if (z[t] < 0.25) {
                    a++;
                } else if (z[t] < 0.5) {
                    b++;
                } else if (z[t] < 0.75) {
                    c++;
                } else {
                    d++;
                }
            }

            System.out.println("Confirm the distribution of intinsic opinions ↓↓↓");
            System.out.printf("0 ~ 0.25: %d\n", a);
            System.out.printf("0.25 ~ 0.5: %d\n", b);
            System.out.printf("0.5 ~ 0.75: %d\n", c);
            System.out.printf("0.75 ~ 1.0: %d\n", d);

        }
        return new Result(pls, disaggs, gppls, stfs, dvs, z, W, finderror);
    }



    /// Helper Functions

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
}


        