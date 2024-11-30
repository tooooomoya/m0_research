package main.utils;

import com.gurobi.gurobi.*;
import main.utils.matrix_util;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.*;
import java.util.stream.IntStream;

import main.structure.OptResult;

public class optimization {

    // minZ : the STEP where users change their opinion according to the FJ model
    public static double[] minZ(double[][] W, double[] s, double[] z) {
        int n = z.length;

        // d の初期化
        double[] d = new double[n];
        for (int i = 0; i < n; i++) {
            d[i] = 0.0;
            for (int j = 0; j < n; j++) {
                d[i] += W[i][j];
            }
        }
        double[] z1 = new double[n];
        double[] s1 = new double[n];
        for (int i = 0; i < n; i++) {
            z1[i] = 2 * z[i] - 1;
            s1[i] = 2 * s[i] - 1;
        }
        //matrix_util.printDist(z1);
        //matrix_util.printDist(s1);

        double lambda = 0.5;
        double coeff = 1.4;
        // new_z の初期化
        double[] new_z = new double[n];
        for (int i = 0; i < n; i++) {
            double temp = 0.0;
            for (int j = 0; j < n; j++) {
                temp += W[i][j] * z1[j];
            }
            new_z[i] = coeff * (s1[i] + temp) / (d[i] + 1);
            //new_z[i] = temp * lambda + (1 - lambda) * s[i]; 
        }
        //matrix_util.printDist(new_z);

        double z_min = 0.0;
        double z_max = 0.0;
        for (int i = 0; i < z.length; i++) {
            if (new_z[i] > z_max) {
                z_max = new_z[i];
            } else if (new_z[i] < z_min) {
                z_min = new_z[i];
            }
        }
        System.out.println("\nz_max: " + z_max);
        System.out.println("z_min: " + z_min);

        for (int i = 0; i < z.length; i++) {
            if (new_z[i] < -1) {
                new_z[i] = -1;
            } else if (new_z[i] > 1) {
                new_z[i] = 1;
            }
            new_z[i] = (new_z[i] + 1) / 2;
            //new_z[i] = (new_z[i] - z_min)/(z_max - z_min);
        }

        return new_z;
    }

    // minW : the STEP where Admin changes(minimizes) W matrix under some
    // constraints
    // find weight matrix W that minimizes z^T L z
    public static OptResult minWGurobi(double[] z, double lam, double[][] W0, boolean reducePls, double gam,
            boolean existing) throws GRBException {
        int n = z.length;
        System.out.println("the number of n: " + n);

        System.out.println("---------- guroubi information ----------");
        GRBEnv env = new GRBEnv("minW.log");
        env.set(GRB.IntParam.OutputFlag, 0); // Disable output for 0
        GRBModel model = new GRBModel(env);
        //model.set("BarHomogeneous", "0");
        //model.set(GRB.IntParam.Method, 2); // Use Barrier method for QCP
        //model.set(GRB.DoubleParam.BarConvTol, 1e-4); // Set tighter convergence tolerance
        //model.set(GRB.DoubleParam.FeasibilityTol, 1e-5); // Adjust feasibility tolerance for numerical stability
        //model.set(GRB.IntParam.BarIterLimit, 200); // Increase iteration limit to allow more iterations if needed
        //model.set(GRB.DoubleParam.MIPGap, 0.01); // 例: 0.01
        //model.set(GRB.IntParam.BarIterLimit, 50);

        System.out.println("---------- end information ----------");

        // Create variables x for the edges in the graph
        GRBVar[][] x = new GRBVar[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //if (i > j) {
                x[i][j] = model.addVar(0.0, 10.0, 0.0, GRB.CONTINUOUS, "x_" + i + "_" + j);
                // x[j][i] = x[i][j]; // 対称行列を作る →いるか？
                //}
            }
        }
        // x_1_2みたいなguroubi用の変数ができる。

        // check
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //if (i > j && x[i][j] != null) {
                count++;
                //}
            }
        }

        // Print the count of non-null variables
        System.out.println("Number of non-null x variables: " + count);

        // Objective: minimize ∑_ij w_ij (zi - zj)^2
        GRBQuadExpr objExp = new GRBQuadExpr();

        // objExpは最小化する目的関数となる。
        if (existing) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i > j && W0[i][j] > 0) {
                        double diff = 10 - 25 * Math.abs(z[i] - z[j]);
                        objExp.addTerm(diff, x[i][j]);
                    }
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    //if (i > j) {
                    double diff = 10 - 25 * Math.abs(z[i] - z[j]);
                    objExp.addTerm(diff, x[i][j]);
                    // diff*x[i][j]という項(Term)をAddする、という意味
                    //}
                }
            }
        }

        // If reducePls is true, add the regularization term γ * ∑(x_ij^2)
        if (reducePls) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i > j) {
                        objExp.addTerm(gam, x[i][j], x[i][j]); // γ * x_ij^2 term
                    }
                }
            }
        }
        /*
        // objExpは最小化する目的関数となる。
        if (existing) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i > j && W0[i][j] > 0) {
                        double wij = (z[i] - z[j]) * (z[i] - z[j]);
                        objExp.addTerm(wij, x[i][j]);
                    }
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i > j) {
                        double wij = (z[i] - z[j]) * (z[i] - z[j]);
                        objExp.addTerm(wij, x[i][j]);
                        // wij(coefficient)*x[i][j]という項(Term)をAddする、という意味
                    }
                }
            }
        }

        // If reducePls is true, add the regularization term γ * ∑(x_ij^2)
        if (reducePls) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i > j) {
                        objExp.addTerm(gam, x[i][j], x[i][j]); // γ * x_ij^2 term
                    }
                }
            }
        }
         */

        model.setObjective(objExp, GRB.MAXIMIZE);
        // System.out.println("Set the objective!");

        // Add constraints sum_j x[i,j] = di : the degree of each vertex should not
        // change
        // calculate d[i] d[i]にはノードiのinitialの度数が入る
        double[] d = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i] += W0[i][j]; // W0の行ごとの総和を計算
            }
        }

        // Add constraint
        for (int i = 0; i < n; i++) {// 各ノードiに対して
            GRBLinExpr expr = new GRBLinExpr();

            /*if (existing) {
                // W0[i,j] > 0 の場合に制約を追加
                for (int j = i + 1; j < n; j++) {
                    if (W0[i][j] > 0 & x[i][j] != null) {
                        expr.addTerm(1.0, x[i][j]);
                    }
                }
                for (int j = 0; j < i; j++) {
                    if (W0[i][j] > 0 & x[i][j] != null) {
                        expr.addTerm(1.0, x[j][i]);
                    }
                }
            } else {
                // 制約をすべてのエッジに対して追加
                for (int j = i + 1; j < n; j++) {
                    if (x[j][i] != null) {
                        expr.addTerm(1.0, x[j][i]);
                    }
                }
                for (int j = 0; j < i; j++) {
                    if (x[i][j] != null) {
                        expr.addTerm(1.0, x[i][j]);
                    }
                }
            }*/
            for (int j = 0; j < n; j++) {
                if (x[i][j] != null) {
                    expr.addTerm(1.0, x[i][j]);
                }
            }

            // 制約: sum_j x[i,j] = d[i]
            model.addConstr(expr, GRB.EQUAL, d[i], "c_" + i);
        }

        // System.out.println("added first constraint");
        // Add the constraint ∑(wij - w0ij) < lam * ||w0||^2
        // This part would need adjustment based on the actual implementation context
        // The right-hand side would be defined according to your original logic
        // Calculate Frobenius norm of W0 (||W0||^2 : 二乗和)
        double normW0Squared = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                normW0Squared += W0[i][j] * W0[i][j];
            }
        }
        double rhs = lam * lam * normW0Squared;

        GRBQuadExpr expr1 = new GRBQuadExpr();
        // Create the expression for the constraint: ∑(wij - w0ij)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //if (i > j) { // Only consider pairs where i > j

                    // Add the terms to the quadratic expression
                    if (existing && W0[i][j] > 0) {
                        expr1.addTerm(1.0, x[i][j], x[i][j]); // x[i,j]^2
                        expr1.addTerm(-2.0 * W0[i][j], x[i][j]); // -2 * W0[i,j] * x[i,j]
                        if (W0[i][j] > 0) {
                            expr1.addConstant(W0[i][j] * W0[i][j]); // W0[i,j]^2 as a constant
                        }
                    } else if (!existing) {
                        expr1.addTerm(1.0, x[i][j], x[i][j]); // x[i,j]^2
                        expr1.addTerm(-2.0 * W0[i][j], x[i][j]); // -2 * W0[i,j] * x[i,j]
                        if (W0[i][j] > 0) {
                            expr1.addConstant(W0[i][j] * W0[i][j]); // W0[i,j]^2 as a constant
                        }
                    }
                //}
            }
        }
        // Add the quadratic constraint: expr <= rhs
        model.addQConstr(expr1, GRB.LESS_EQUAL, rhs, "q_constraint");

        // Optimize the model
        model.optimize();
        // System.out.println("optimization finished!");

        boolean Opt = false;

        if (model.get(GRB.IntAttr.Status) != GRB.Status.OPTIMAL) {
            //Opt = true;
            //下の分でthrowしてるから、return文まで実行されない。だからOptは意味ない
            throw new GRBException("-------------- Optimization was not successful. Status: " + model.get(GRB.IntAttr.Status));
        }

        // Retrieve the updated weight matrix W
        double[][] W = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i > j) {
                    W[i][j] = x[i][j].get(GRB.DoubleAttr.X); // Get the optimized value of x[i][j]
                    if (W[i][j] < 0.01) {
                        W[i][j] = 0;
                    }
                    W[j][i] = W[i][j]; // Symmetric matrix
                }
            }
        }

        model.dispose();
        env.dispose();
        return new OptResult(Opt, W);
    }

    public static double computePls(double[] z) {
        int n = z.length;
        // 平均を計算
        double mean = IntStream.range(0, n).mapToDouble(i -> z[i]).average().orElse(0.0);

        // 分散を計算
        double variance = IntStream.range(0, n)
                .mapToDouble(i -> Math.pow(z[i] - mean, 2))
                .sum() / n;

        // 歪度 (m3) を計算
        double m3 = IntStream.range(0, n)
                .mapToDouble(i -> Math.pow(z[i] - mean, 3))
                .sum() / (n * Math.pow(variance, 1.5));

        // 尖度 (m4) を計算
        double m4 = IntStream.range(0, n)
                .mapToDouble(i -> Math.pow(z[i] - mean, 4))
                .sum() / (n * Math.pow(variance, 2)) - 3.0; // Excess Kurtosis

        // Bimodality Coefficient (BC) を計算
        double numerator = m3 * m3 + 1;
        double denominator = m4 + (3.0 * Math.pow(n - 1, 2)) / ((n - 2) * (n - 3));
        double BC = numerator / denominator;

        return BC;

        /*
        double sum = 0.0;
        for (double value : z) {
            sum += value;
        }
        double ZMean = sum / z.length;

        double sumSquareDifferences = 0.0;
        for (double value : z) {
            double difference = value - ZMean;
            sumSquareDifferences += difference * difference;
        }
        return sumSquareDifferences;
         */
    }
}
