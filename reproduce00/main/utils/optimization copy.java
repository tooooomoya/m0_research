package main.utils;

import com.gurobi.gurobi.*;
import main.utils.matrix_util;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.*;

public class optimization {

    // minZ : the STEP where users change their opinion according to the FJ model
    public static double[] minZ(double[][] W, double[] s) {
        double[][] L = matrix_util.createL(W, W.length);
        int n = W.length;

        // Create identity matrix (I)
        double[][] identityMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            identityMatrix[i][i] = 1.0;
        }

        double[][] LPlusI = matrix_util.add(identityMatrix, L);

        // z = (LPlusI)^-1 * s : calculateLE solves this linear equation

        // calculate (LPlusI)'s inverse matrix
        RealMatrix matrixLPlusI = MatrixUtils.createRealMatrix(LPlusI);
        // 微小な値を加えて正則化
        /*double regularizationValue = 0.00000001;
        for (int i = 0; i < matrixLPlusI.getRowDimension(); i++) {
            matrixLPlusI.setEntry(i, i, matrixLPlusI.getEntry(i, i) + regularizationValue);
        }*/

        LUDecomposition luDecomposition = new LUDecomposition(matrixLPlusI);
        RealMatrix inverseMatrix = luDecomposition.getSolver().getInverse();

        // z = (LPlusI)^-1 * s
        RealVector vectorS = new ArrayRealVector(s);
        RealVector vectorZ = inverseMatrix.operate(vectorS);

        return vectorZ.toArray();
    }

    // minW : the STEP where Admin changes(minimizes) W matrix under some
    // constraints
    // find weight matrix W that minimizes z^T L z
    public static double[][] minWGurobi(double[] z, double lam, double[][] W0, boolean reducePls, double gam,
            boolean existing) throws GRBException {
        int n = z.length;
        System.out.println("the number of n: "+ n);

        System.out.println("---------- guroubi information ----------");
        GRBEnv env = new GRBEnv("minW.log");
        env.set(GRB.IntParam.OutputFlag, 0); // Disable output
        GRBModel model = new GRBModel(env);
        System.out.println("---------- end information ----------");

        // Create variables x for the edges in the graph
        GRBVar[][] x = new GRBVar[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(i > j){
                x[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x_" + i + "_" + j);
                //x[j][i] = x[i][j]; // 対称行列を作る →いるか？
                }
            }
        }
        // x_1_2みたいなguroubi用の変数ができる。

        // check
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i > j && x[i][j] != null) {
                    count++;
                }
            }
        }
        
        // Print the count of non-null variables
        System.out.println("Number of non-null x variables: " + count);


        // Objective: minimize ∑_ij w_ij (zi - zj)^2
        GRBQuadExpr objExp = new GRBQuadExpr();
        //objExpは最小化する目的関数となる。
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
                        //wij(coefficient)*x[i][j]という項(Term)をAddする、という意味
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
                d[i] += W0[i][j]; // W0の行ごとの総和を計算
            }
        }

        // Add constraint
        for (int i = 0; i < n; i++) {// 各ノードiに対して
            GRBLinExpr expr = new GRBLinExpr();

            if (existing) {
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
                if (i > j) { // Only consider pairs where i > j
                    
                    // Add the terms to the quadratic expression
                    if (existing && W0[i][j] > 0) {
                        expr1.addTerm(1.0, x[i][j], x[i][j]); // x[i,j]^2
                        expr1.addTerm(-2.0 * W0[i][j], x[i][j]); // -2 * W0[i,j] * x[i,j]
                        expr1.addConstant(W0[i][j] * W0[i][j]); // W0[i,j]^2 as a constant
                    } else if (!existing) {
                        expr1.addTerm(1.0, x[i][j], x[i][j]); // x[i,j]^2
                        expr1.addTerm(-2.0 * W0[i][j], x[i][j]); // -2 * W0[i,j] * x[i,j]
                        expr1.addConstant(W0[i][j] * W0[i][j]); // W0[i,j]^2 as a constant
                    }
                }
            }
        }
        // Add the quadratic constraint: expr <= rhs
        model.addQConstr(expr1, GRB.LESS_EQUAL, rhs, "q_constraint");

        // Optimize the model
        model.optimize();
        // System.out.println("optimization finished!");

        if (model.get(GRB.IntAttr.Status) != GRB.Status.OPTIMAL) {
            throw new GRBException("Optimization was not successful. Status: " + model.get(GRB.IntAttr.Status));
        }

        // Retrieve the updated weight matrix W
        double[][] W = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i > j) {
                    W[i][j] = x[i][j].get(GRB.DoubleAttr.X); // Get the optimized value of x[i][j]
                    W[j][i] = W[i][j]; // Symmetric matrix
                }
            }
        }

        model.dispose();
        env.dispose();
        return W;
    }

    public static double computePls(double[] z) {
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
    }
}
