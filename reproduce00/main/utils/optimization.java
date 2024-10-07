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
        double[][] LPlusI = matrix_util.add(W, L);

        // z = (LPlusI)^-1 * s : calculateLE solves this linear equation

        // calculate (LPlusI)'s inverse matrix
        RealMatrix matrixLPlusI = MatrixUtils.createRealMatrix(LPlusI);
        // 微小な値を加えて正則化
        double regularizationValue = 0.0001;
        for (int i = 0; i < matrixLPlusI.getRowDimension(); i++) {
            matrixLPlusI.setEntry(i, i, matrixLPlusI.getEntry(i, i) + regularizationValue);
        }

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
        GRBEnv env = new GRBEnv("minW.log");
        GRBModel model = new GRBModel(env);

        // Create variables x for the edges in the graph
        GRBVar[][] x = new GRBVar[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i > j) {
                    x[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x_" + i + "_" + j);
                }
            }
        }
        // x_1_2みたいなguroubi用の変数ができる。

        // check
        System.out.println("Number of variables created: " + x.length);

        // Objective: minimize ∑_ij w_ij (zi - zj)^2
        GRBQuadExpr objExp = new GRBQuadExpr();
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

        // Add constraints sum_j x[i,j] = di
        double[] d = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i] += W0[i][j]; // W0の行ごとの総和を計算
            }
        }

        for (int i = 0; i < n; i++) {
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
                    expr.addTerm(1.0, x[i][j]);
                }
                for (int j = 0; j < i; j++) {
                    expr.addTerm(1.0, x[j][i]);
                }
            }

            // 制約: sum_j x[i,j] = d[i]
            model.addConstr(expr, GRB.EQUAL, d[i], "c_" + i);
        }

        System.out.println("added first constraint");

        // Add the constraint ∑(wij - w0ij) < lam * ||w0||^2
        // This part would need adjustment based on the actual implementation context
        // The right-hand side would be defined according to your original logic

        // Optimize the model
        model.optimize();

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
        return sumSquareDifferences / z.length;
    }
}
