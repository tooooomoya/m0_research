package main.utils;

import com.gurobi.gurobi.*;
import main.utils.matrix_util;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.*;
import java.util.stream.IntStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import main.structure.OptResult;
import java.util.Random;

public class optimization {

    // minZ : the STEP where users change their opinion according to the FJ model
    public static double[] minZ(double[][] W, double[] s, double[] z) {
        double[][] temp_W = matrix_util.copyMatrix(W);
        int n = z.length;

        //Wを行ごとに正規化
        for (int i = 0; i < n; i++) {
            double rowSum = 0.0;

            // 行の総和を計算
            for (int j = 0; j < n; j++) {
                rowSum += temp_W[i][j];
            }

            // 総和が0の場合は正規化をスキップ（ゼロ除算防止）
            if (rowSum == 0.0) {
                continue;
            }

            // 行の各要素を正規化
            for (int j = 0; j < n; j++) {
                temp_W[i][j] /= rowSum;
            }
        }

        // d の初期化
        double[] d = new double[n];
        for (int i = 0; i < n; i++) {
            d[i] = 0.0;
            for (int j = 0; j < n; j++) {
                d[i] += temp_W[i][j];
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

        double[] tol = new double[n];
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.directory + "/tolerance_distribution.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                int u = Integer.parseInt(parts[0]) - 1; // draw node index
                double w = Double.parseDouble((parts[1]));// draw the node's tolerance
                tol[u] = w;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double coeff = 1.0;
        // new_z の初期化
        double[] new_z = new double[n];
        for (int i = 0; i < n; i++) {
            if (d[i] != 0) {
                double temp = 0.0;
                for (int j = 0; j < n; j++) {
                    temp += temp_W[i][j] * z1[j];
                }
                //new_z[i] = coeff * (s1[i] + temp) / d[i];
                new_z[i] = tol[i] * s1[i] + (1 - tol[i]) * temp;
            }
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

    public static OptResult minWGurobi(double[] z, double lam, double[][] W, boolean reducePls, double gam, boolean existing) throws GRBException{
        int n = z.length;
        boolean Opt = false;
        Random random = new Random();
        int isolate = 0;

        for(int i = 0; i < n ; i++){
            double my_w_sum = 0.0;
            int to_user = 0;
            int follow_num = 0;
            for(int j = 0; j < n; j++){
                if(W[i][j] > Constants.W_THRES){
                    my_w_sum += W[i][j];
                    follow_num++;
                }
            }
            if(follow_num <= 1){
                //System.out.println("user "+i+" doesn't have more than 1 follow.");
                isolate++;
                continue;
            }
            double rand = random.nextDouble();
            double prob = 0.0;
            for(int j = 0; j < n; j++){
                boolean found = false;
                if(W[i][j] > Constants.W_THRES){
                    prob += W[i][j] / my_w_sum;
                    if(prob > rand){
                        to_user = j;
                        break;
                    }
                }
            }
            double diff = Math.abs(z[i] - z[to_user]);
            double rate = (- 2 * Math.log(diff)) / 100;
            //double rate = (-10 * diff + 5)/100;
            double widen = rate * W[i][to_user];
            //System.out.println("rate "+rate);
            double overflow = 0.0;

            for(int j = 0; j < n; j++){
                if(W[i][j] > 0 && j != to_user){
                    W[i][j] -= widen / (follow_num - 1);
                    if(W[i][j] < 0){
                        overflow += -W[i][j];
                        W[i][j] = 0;
                    }
                    }
            }
            W[i][to_user] += widen - overflow;
            /*if(W[i][to_user] > 10.0){
                W[i][to_user] = 10.0;
                System.out.println("Overflow in minW!!!!!!");
            }*/
        }
        System.out.println("isolate num "+isolate);

        return new OptResult(Opt, W);
    }

    // minW : the STEP where Admin changes(minimizes) W matrix under some
    // constraints
    // find weight matrix W that minimizes z^T L z
    /*public static OptResult minWGurobi(double[] z, double lam, double[][] W0, boolean reducePls, double gam,
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
                if (i != j) {
                    x[i][j] = model.addVar(0.0, 10.0, 0.0, GRB.CONTINUOUS, "x_" + i + "_" + j);
                }
            }
        }
        // x_1_2みたいなguroubi用の変数ができる。

        // check
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (x[i][j] != null) {
                    count++;
                }
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
                        //double diff = 10 - 25 * Math.abs(z[i] - z[j]);
                        double diff = Math.abs(z[i] - z[j]);
                        objExp.addTerm(diff, x[i][j]);
                    }
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        //double diff = 10 - 25 * Math.abs(z[i] - z[j]);
                        double diff = (z[i] - z[j]) * (z[i] - z[j]);
                        objExp.addTerm(diff, x[i][j]);
                        // diff*x[i][j]という項(Term)をAddする、という意味
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

        model.setObjective(objExp, GRB.MINIMIZE);
        // System.out.println("Set the objective!");

        // Add constraints sum_j x[i,j] = di : the degree of each vertex should not
        // change
        // calculate d[i] d[i]にはノードiのinitialの度数が入る
        double[] d = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    d[i] += W0[i][j]; // W0の行ごとの総和を計算
                }
            }
        }

        // Add constraint
        for (int i = 0; i < n; i++) {// 各ノードiに対して
            GRBLinExpr expr = new GRBLinExpr();

            for (int j = 0; j < n; j++) {
                if (i != j) {
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
                if (i != j) {
                    normW0Squared += W0[i][j] * W0[i][j];
                }
            }
        }
        double rhs = lam * lam * normW0Squared;

        GRBQuadExpr expr1 = new GRBQuadExpr();
        // Create the expression for the constraint: ∑(wij - w0ij)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //if (i > j) { // Only consider pairs where i > j

                // Add the terms to the quadratic expression
                if (i != j) {
                    if (existing && W0[i][j] > 0) {
                        expr1.addTerm(1.0, x[i][j], x[i][j]); // x[i,j]^2
                        expr1.addTerm(-2.0 * W0[i][j], x[i][j]); // -2 * W0[i,j] * x[i,j]
                        if (W0[i][j] > 0) {
                            expr1.addConstant(W0[i][j] * W0[i][j]); // W0[i,j]^2 as a constant
                        }
                    } else if (!existing) {
                        expr1.addTerm(1.0, x[i][j], x[i][j]); // x[i,j]^2
                        if (W0[i][j] > 0) {
                            expr1.addTerm(-2.0 * W0[i][j], x[i][j]); // -2 * W0[i,j] * x[i,j]
                            expr1.addConstant(W0[i][j] * W0[i][j]); // W0[i,j]^2 as a constant
                        }
                    }
                }
                //}
            }
        }
        // Add the quadratic constraint: expr <= rhs
        model.addQConstr(expr1, GRB.LESS_EQUAL, rhs, "q_constraint");

        // Optimize the model
        model.optimize();
        //System.out.println("optimization finished!");

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
                if (i != j) {
                    W[i][j] = x[i][j].get(GRB.DoubleAttr.X); // Get the optimized value of x[i][j]
                }
            }
        }

        model.dispose();
        env.dispose();
        return new OptResult(Opt, W);
    }*/

    public static double computePls(double[] z, double[][] W, boolean[] isDiversityUser) {

        double[] z_diversity = new double[z.length];

        for (int i = 0; i < z.length; i++) {
            if(isDiversityUser[i]){
                continue;
            }
            double my_opinion = z[i];
            double adjacency_opinion_sum = 0; // calculate the number of agents having opposite opinion
            double adjacency_sum = 0;

            if (my_opinion > 0.5) {
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > Constants.W_THRES) {
                        adjacency_sum += W[i][j];
                        if (z[j] < 0.5) {
                            adjacency_opinion_sum += W[i][j];
                        }
                    }
                }
            } else if (my_opinion < 0.5) {
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > Constants.W_THRES) {
                        adjacency_sum += W[i][j];
                        if (z[j] > 0.5) {
                            adjacency_opinion_sum += W[i][j];
                        }
                    }
                }
            } else if (my_opinion == 0.5) {
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > 0) {
                        adjacency_sum += W[i][j];
                        adjacency_opinion_sum += W[i][j];
                    }
                }
            }

            if (adjacency_sum > 0) {
                z_diversity[i] = adjacency_opinion_sum / adjacency_sum;
            } else {
                z_diversity[i] = 0.0;
            }
        }

        double temp = 0;
        for (int i = 0; i < z.length; i++) {
            temp += z_diversity[i];
        }
        double diversity = temp / z_diversity.length;

        return diversity;

        /*int n = z.length;
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
