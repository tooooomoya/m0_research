
import com.gurobi.gurobi.GRBException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.*;
import java.util.Collections;

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
        double[] z = matrix_util.copyVector(s);
        double[] initital_s = matrix_util.copyVector(s);
        //System.out.println("\nthe fisrst z after the FJ effect (basis): ");
        //matrix_util.printVector(z);

        int div_user_num = (int) (Constants.DIV_RATE * z.length);
        System.out.println("The num of user with diversity plan : " + div_user_num);
        List<Integer> userIndices = IntStream.range(0, z.length).boxed().collect(Collectors.toList());
        Collections.shuffle(userIndices, new Random(56));
        int[] diversityUserList = userIndices.subList(0, div_user_num).stream().mapToInt(Integer::intValue).toArray();
        boolean[] isDiversityUser = new boolean[z.length];
        for (int m : diversityUserList) {
            isDiversityUser[m] = true; // diversityUserList に含まれるインデックスを true に設定
        }

        ///// Set pls
        ArrayList<Double> pls = new ArrayList<>();
        pls.add(optimization.computePls(z, W, isDiversityUser));
        //System.out.println("\npls before iteration: "+ optimization.computePls(z));

        ///// Set disaggs
        ArrayList<Double> disaggs = new ArrayList<>();
        disaggs.add(calculater.computeDisagreement(z, W, isDiversityUser));
        System.out.println("\ndisagg before iteration: " + calculater.computeDisagreement(z, W, isDiversityUser));

        ///// Set gppls
        ArrayList<Double> gppls = new ArrayList<>();
        gppls.add(calculater.computeGpPls(z, s));
        System.out.println("gppls before iteration: " + calculater.computeGpPls(z, s));

        ///// Set stfs
        ArrayList<Double> stfs = new ArrayList<>();
        stfs.add(calculater.computeStf(z, W, communities, isDiversityUser));
        System.out.println("\nstfs before iteration: " + calculater.computeStf(z, W, communities, isDiversityUser));

        ///// Set udv
        ArrayList<Double> udv = new ArrayList<>();
        udv.add(calculater.computeUdv(z, W, isDiversityUser));

        ///// Set cdv
        ArrayList<Double> cdv = new ArrayList<>();
        cdv.add(calculater.computeCdv(z, W, communities));

        double initil_total_weight = matrix_util.calculateSumWeight(W);

        GIFMaker.recordHistogram(0.0, z);

        int i = 0;
        boolean flag = true;
        double[][] Wnew = null;
        boolean finderror = false;
        double weight_added = 0;

        simulation.updateGraph(z, W);
        simulation.assignCommunities(communities);
        simulation.exportGraph(i);

        int conv_speed = -1;
        boolean first_conv = true;


        Random random_state0 = new Random(80);
        for (int k = 0; k < z.length; k++) {
            double total_w = 0.0;
            for (int j = 0; j < z.length; j++) {
                if (k != j && W[k][j] > 0.0) {
                    total_w += W[k][j];
                }
            }
            if (total_w == 0.0) {
                System.out.println("Find Isolated Agent !!!!!!!!");
                for (int l = 0; l < Constants.NEW_USER_NUM; l++) {
                    int newFriend = random_state0.nextInt(z.length);
                    if (newFriend != k) {
                        W[k][newFriend] += (double) (Constants.NEW_WEIGHT_FOR_ISOLATED / Constants.NEW_USER_NUM);
                    }
                }
            }
        }

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
            if (i != 0 && i % 10 == 0) {
                communities = Louvain.louvainCommunityDetection(W);
            }

            Wnew = null;
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
                Wnew = matrix_util.copyMatrix(W);
                System.out.println("Wnew Error : Wnew and W will be Completely same");
            }

            double[] deg_aft = new double[z.length];
            for (int k = 0; k < z.length; k++) {
                for (int l = 0; l < z.length; l++) {
                    if (k != l) {
                        deg_aft[k] += Wnew[k][l];
                    }
                }
                if ((deg_aft[k] - deg[k]) > 1.0 && !random) {
                    System.out.println("Huge difference in degree -> " + (deg_aft[k] - deg[k] + " in " + k));
                }
            }
            double w_num = 0.0;
            int added_num = 0;
            int zero_num = 0;
            double avg_rec_weight = 0.0;
            double total_sub_weight = 0.0;
            List<int[]> newFollowPairs = new ArrayList<>();
            if (random) {
                /// My Method : randomly add weight
            List<int[]> selectedPairs = new ArrayList<>();
                selectedPairs = calculater.selectPairs_v1(Wnew);
                for (int[] pair : selectedPairs) {
                    int follow_num = 0;
                    double total_add = 0.0;
                    if ((10 - Wnew[pair[0]][pair[1]]) > Constants.ADD_WEIGHT) {
                        newFollowPairs.add(pair);
                        avg_rec_weight += Wnew[pair[0]][pair[1]];
                        if (Wnew[pair[0]][pair[1]] < Constants.W_THRES) {
                            zero_num++;
                        }

                        //あるユーザのSNS利用時間は変化しないとして、増えた人の分、他の人との関係性を目減りさせる.
                        for (int k = 0; k < W.length; k++) {
                            if (Wnew[pair[0]][k] > Constants.W_THRES) {
                                follow_num++;
                            }
                        }
                        //System.out.println("Follow num" + follow_num);
                        if (follow_num > 1) {
                            double sub_wieght = (double) Constants.ADD_WEIGHT / follow_num;
                            for (int k = 0; k < W.length; k++) {
                                if (Wnew[pair[0]][k] > Constants.W_THRES) {
                                    Wnew[pair[0]][k] -= sub_wieght;
                                    total_sub_weight += sub_wieght;
                                    total_add -= sub_wieght;
                                }
                            }
                        }

                        Wnew[pair[0]][pair[1]] += Constants.ADD_WEIGHT;
                        total_add += Constants.ADD_WEIGHT;
                        w_num += Constants.ADD_WEIGHT;
                        added_num++;

                    }
                    //System.out.println("total add:"+total_add);
                }
                System.out.println("The sum of w added by my method: " + w_num);
                System.out.println("The sum of w subbed by my method: " + total_sub_weight);
                System.out.println("Added edges num :" + added_num + ", To Zero edges num : " + zero_num);
                System.out.println("Avg weight to which new weight added: " + avg_rec_weight / added_num);
            }
            weight_added += (w_num - total_sub_weight);

            double total_vanish_weight = 0.0;
            for (int k = 0; k < z.length; k++) {
                double user_balance_weight = 0.0;
                int user_potential_num = 0;
                for (int l = 0; l < z.length; l++) {
                    //あまりにリンクの重みが小さいとそのユーザとの関係性は断たれる
                    if (Wnew[k][l] > 0 && Wnew[k][l] < Constants.W_THRES) {
                        total_vanish_weight += Wnew[k][l];
                        user_balance_weight += Wnew[k][l];
                        Wnew[k][l] = 0;
                    } else if (Wnew[k][l] > Constants.W_THRES) {
                        user_potential_num++;
                    }
                }
                for (int l = 0; l < z.length; l++) {
                    if (Wnew[k][l] > 0) {
                        Wnew[k][l] += user_balance_weight / user_potential_num;
                    }
                }
            }
            System.out.println("Total subbed weight because they were too small :" + total_vanish_weight);

            Random random_state = new Random(43);
            for (int k = 0; k < z.length; k++) {
                double total_w = 0.0;
                for (int j = 0; j < z.length; j++) {
                    if (k != j && Wnew[k][j] > 0.0) {
                        total_w += Wnew[k][j];
                    }
                }
                if (total_w == 0.0) {
                    System.out.println("Find Isolated Agent !!!!!!!!");
                    for (int l = 0; l < Constants.NEW_USER_NUM; l++) {
                        int newFriend = random_state.nextInt(z.length);
                        if (newFriend != k) {
                            Wnew[k][newFriend] += (double) (Constants.NEW_WEIGHT_FOR_ISOLATED / Constants.NEW_USER_NUM);
                        }
                    }
                }
            }
            for (int k = 0; k < z.length; k++) {
                double temp = 0.0;
                for (int l = 0; l < z.length; l++) {
                    temp += Wnew[k][l];
                }
                if (temp == 0) {
                    System.out.println("oakdfasdjfajdfjas!!!!!!!!!!!!!!!!!!!!!!!!");
                }
            }
            double total_weight2 = matrix_util.calculateSumWeight(Wnew);
            System.out.println("Total Weight : Initial->" + initil_total_weight + ", This step->" + total_weight2);

            Wnew = calculater.friendRecommend(Wnew, z, isDiversityUser);
            double total_weight1 = matrix_util.calculateSumWeight(Wnew);
            System.out.println("Total Weight : Initial->" + initil_total_weight + ", This step->" + total_weight1);

            if (random) {
                int unfollowed = 0;
                for (int[] pair : newFollowPairs) {
                    if (Wnew[pair[0]][pair[1]] == 0.0) {
                        unfollowed++;
                    }
                }
                System.out.println("unfollowed random recommend links " + unfollowed);
            }

            /// confirm the maximum weight
            double max_w = 0.0;
            double min_w = 1.0;
            for (int ii = 0; ii < z.length; ii++) {
                for (int j = 0; j < z.length; j++) {
                    if (ii == j) {
                        if (Wnew[ii][j] != 0) {
                            System.out.println("Matrix error Occured!!!!!!!!!!!!");
                            Wnew[ii][j] = 0;
                        }
                    } else if (Wnew[ii][j] > max_w) {
                        max_w = Wnew[ii][j];
                    } else if (Wnew[ii][j] > 0 && Wnew[ii][j] < min_w) {
                        min_w = Wnew[ii][j];
                    }

                    /*if (Wnew[ii][j] > 10.0) {
                        Wnew[ii][j] = 10.0;
                    } else if (Wnew[ii][j] < 0) {
                        Wnew[ii][j] = 0.0;
                    }*/
                }
            }
            System.out.println("\nMaximum Weight of W matrix : " + max_w);
            System.out.println("Minimun Weight of W matrix : " + min_w);

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

            z = matrix_util.copyVector(znew);
            W = matrix_util.copyMatrix(Wnew);

            double total_weight = matrix_util.calculateSumWeight(W);
            System.out.println("Total Weight : Initial->" + initil_total_weight + ", This step->" + total_weight);

            for (int k = 0; k < z.length; k++) {
                if (initital_s[k] != s[k]) {
                    System.out.println("initial opinion error !!!!!!!");
                }
            }

            //double PLS = calculateDiversity(znew, Wnew);
            double PLS = optimization.computePls(z, W, isDiversityUser);
            pls.add(PLS);
            System.out.println("\npls: " + PLS);
            if (PLS > 0.56 && first_conv) {
                conv_speed = i;
                first_conv = false;
            }

            double disagg = calculater.computeDisagreement(z, W, isDiversityUser);
            disaggs.add(disagg);
            //System.out.println("\ndisagg: " + disagg);

            double GPPLS = calculater.computeGpPls(z, s);
            gppls.add(GPPLS);
            System.out.println("\ngppls: " + GPPLS);

            double stf = calculater.computeStf(z, W, communities, isDiversityUser);
            stfs.add(stf);
            System.out.println("\nstf: " + stf);

            double UDV = calculater.computeUdv(z, W, isDiversityUser);
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
            simulation.assignCommunities(communities);
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
            for (int j = 0; j < matrix1.length; j++) {
                double difference = matrix1[i][j] - matrix2[i][j];
                sumSquaredDifferences += difference * difference;
            }
        }

        // Return the square root of the sum of squared differences
        return Math.sqrt(sumSquaredDifferences);
    }
}
