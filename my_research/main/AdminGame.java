
import com.gurobi.gurobi.GRBException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import main.utils.*;
import main.structure.*;

public class AdminGame {

    public static Result am(double[][] A, double[] s, double lam, boolean reducePls, double gam, int maxIter,
            boolean existing, boolean random, Map<Integer, List<Integer>> communities, NetworkSimulation simulation) {
        double[][] W = matrix_util.copyMatrix(A);
        // System.out.println("the first W matrix");
        // matrix_util.printMatrix(W);

        // First, each user change its opinion according to the FJ model
        // System.out.println("\nthe first z: ");
        // matrix_util.printVector(s);
        //double[] z = optimization.minZ(W, s);
        double[] z = s;
        //System.out.println("\nthe fisrst z after the FJ effect (basis): ");
        //matrix_util.printVector(z);

        ///// Set pls
        ArrayList<Double> pls = new ArrayList<>();
        pls.add(optimization.computePls(z));
        //System.out.println("\npls before iteration: "+ optimization.computePls(z));

        ///// Set disaggs
        double[][] L = matrix_util.createL(W, W.length);
        ArrayList<Double> disaggs = new ArrayList<>();
        disaggs.add(computeDisagreement(z, L));
        //System.out.println("\ndisagg before iteration: "+computeDisagreement(z, L));

        ///// Set gppls
        ArrayList<Double> gppls = new ArrayList<>();
        gppls.add(calculater.computeGpPls(z));

        ///// Set stfs
        ArrayList<Double> stfs = new ArrayList<>();
        stfs.add(calculater.computeStf(z, W, communities));
        System.out.println("\nstfs before iteration: " + calculater.computeStf(z, W, communities));

        ///// Set udv
        ArrayList<Double> udv = new ArrayList<>();
        udv.add(calculater.computeUdv(z, W));

        ///// Set cdv
        ArrayList<Double> cdv = new ArrayList<>();
        cdv.add(calculater.computeCdv(z, W, communities));

        GIFMaker.recordHistogram(0.0, z);

        int i = 0;
        boolean flag = true;
        double[][] Wnew = null;
        boolean finderror = false;
        double weight_added = 0;

        simulation.updateGraph(z, W);
        simulation.exportGraph(i);
        int conv_speed = -1;
        boolean first_conv = true;

        double[] deg = new double[z.length];
        int isolate = 0;
        for (int k = 0; k < z.length; k++) {
            for (int l = 0; l < z.length; l++) {
                if (k != l) {
                    deg[k] += W[k][l];
                }
            }
            if (deg[k] == 0) {
                isolate++;
            }
        }
        System.out.println("The num of Isolated Node : " + isolate);

        while (flag) {
            System.out.println("--------------------------");
            System.out.println("Iteration:" + i);

            if (lam != 0.0) {
                try {
                    // Admin changes weight matrix
                    OptResult optResult = optimization.minWGurobi(z, lam, W, reducePls, gam, existing);
                    Wnew = optResult.getW();

                    //System.out.println("\nnew W matrix");
                    //matrix_util.printMatrix(Wnew);
                    // ここのWがAだと最初の重み状態からの変化で、あんま意味ない気がする。
                } catch (GRBException e) {
                    System.out.println("Gurobi optimization error: " + e.getMessage());
                    finderror = true;
                    e.printStackTrace();
                }
            }

            if (Wnew == null) {
                Wnew = W;
                System.out.println("Wnew Error : Wnew and W will be Completely same");
            }

            double[] deg_aft = new double[z.length];
            for (int k = 0; k < z.length; k++) {
                for (int l = 0; l < z.length; l++) {
                    if (k != l) {
                        deg_aft[k] += W[k][l];
                    }
                }
                if ((deg_aft[k] - deg[k]) > 0.1 && !random) {
                    System.out.println("Huge difference in degree -> " + (deg_aft[k] - deg[k]));
                }

            }

            Wnew = calculater.friendRecommend(Wnew, z);

            double w_num = 0.0;
            int added_num = 0;
            int zero_num = 0;
            if (random) {
                /// My Method : randomly add weight
            List<int[]> selectedPairs = new ArrayList<>();
                selectedPairs = calculater.selectPairs_v1(Wnew, z);
                for (int[] pair : selectedPairs) {
                    if ((10 - Wnew[pair[0]][pair[1]]) > Constants.ADD_WEIGHT) {
                        if (Wnew[pair[0]][pair[1]] == 0) {
                            zero_num++;
                        }
                        Wnew[pair[0]][pair[1]] += Constants.ADD_WEIGHT;
                        w_num += Constants.ADD_WEIGHT;
                        added_num++;
                    }
                }
                System.out.println("The sum of w added by my method: " + w_num);
                System.out.println("Added edges num :" + added_num + ", To Zero edges num : " + zero_num);
            }
            weight_added += w_num;

            /// confirm the maximum weight
            double max_w = 0.0;
            double min_w = 1.0;
            for (int ii = 0; ii < z.length; ii++) {
                for (int j = 0; j < z.length; j++) {
                    if (Wnew[ii][j] > max_w) {
                        max_w = Wnew[ii][j];
                    } else if (Wnew[ii][j] > 0 && Wnew[ii][j] < min_w) {
                        min_w = Wnew[ii][j];
                    } else if (ii == j) {
                        if (Wnew[ii][j] != 0) {
                            System.out.println("Matrix error Occured!!!!!!!!!!!!");
                        }
                    }
                }
            }
            System.out.println("\nMaximum Weight of W matrix : " + max_w);
            System.out.println("Minimun Weight of W matrix : " + min_w);

            //重みが小すぎるリンクは削除する。
            for (int k = 0; k < z.length; k++) {
                for (int l = 0; l < z.length; l++) {
                    if (k != l && Wnew[k][l] < Constants.W_THRES) {
                        Wnew[k][l] = 0.0;
                    }
                }
            }

            // After Admin action, each user change its opinion according to the FJ model
            // System.out.println("\nz before this time Admin effect: ");
            // matrix_util.printVector(z);
            double[] znew = optimization.minZ(Wnew, s, z);

            // System.out.println("\nNew z after Admin effect: ");
            // matrix_util.printVector(znew);
            // Terminal Criterion(both z and W can be considered to be converged, or maxIter
            // criterion)
            System.out.println("\nz-znew:\n" + norm(z, znew));
            System.out.println("W-Wnew:\n" + matrixNorm(W, Wnew));
            if (Math.max(norm(z, znew), matrixNorm(W, Wnew) / 100) < 0.01 || i > maxIter - 1) {
                System.out.println("\nTerminal Criterion!!!!!!!");
                flag = false;
            }

            z = znew;
            W = Wnew;

            //double PLS = calculateDiversity(znew, Wnew);
            double PLS = optimization.computePls(z);
            pls.add(PLS);
            System.out.println("\npls: " + PLS);
            if (PLS > 0.60 && first_conv) {
                conv_speed = i;
                first_conv = false;
            }

            L = matrix_util.createL(W, W.length);
            double disagg = computeDisagreement(z, L);
            disaggs.add(disagg);
            //System.out.println("\ndisagg: " + disagg);

            double GPPLS = calculater.computeGpPls(z);
            gppls.add(GPPLS);
            //System.out.println("\ngppls: " + GPPLS);

            double stf = calculater.computeStf(z, W, communities);
            stfs.add(stf);
            System.out.println("\nstf: " + stf);

            double UDV = calculater.computeUdv(z, W);
            udv.add(UDV);
            //System.out.println("\nudv: " + UDV);

            double CDV = calculater.computeCdv(z, W, communities);
            cdv.add(CDV);

            int a = 0, b = 0, c = 0, d = 0, e = 0;
            for (int t = 0; t < z.length; t++) {
                if (0 <= z[t] && z[t] < 0.2) {
                    a++;
                } else if (z[t] < 0.4) {
                    b++;
                } else if (z[t] < 0.6) {
                    c++;
                } else if (z[t] < 0.8) {
                    d++;
                } else if (z[t] <= 1.0) {
                    e++;
                }
            }

            System.out.println("Confirm the distribution of z (opinions) ↓↓↓");
            System.out.printf("0 ~ 0.2: %d\n", a);
            System.out.printf("0.2 ~ 0.4: %d\n", b);
            System.out.printf("0.4 ~ 0.6: %d\n", c);
            System.out.printf("0.6 ~ 0.8: %d\n", d);
            System.out.printf("0.8 ~ 1.0: %d\n", e);

            i++;

            GIFMaker.recordHistogram(lam, z);
            simulation.updateGraph(z, W);
            simulation.exportGraph(i);

        }
        double[] deg_aft = new double[z.length];
        for (int k = 0; k < z.length; k++) {
            for (int l = 0; l < z.length; l++) {
                if (k != l) {
                    deg_aft[k] += W[k][l];
                }
            }
        }
        System.out.println("Final distance in D(0) - D(-1) :" + norm(deg, deg_aft));

        System.out.println("The final sum of w added by my method: " + weight_added);
        return new Result(pls, disaggs, gppls, stfs, udv, cdv, z, W, finderror, weight_added, conv_speed);
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
