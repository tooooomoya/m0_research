package main.machines;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.optimization.direct.BaseAbstractRealOptimizer;
import org.apache.commons.math3.optimization.direct.SimplexOptimizer;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.OptimizationException;
import org.apache.commons.math3.optimization.RealObjectiveFunction;
import org.apache.commons.math3.optimization.RealPointValuePair;
import org.apache.commons.math3.optimization.direct.CMAESOptimizer;

import main.utils.matrix_util;

public class optimization {
    public static double[] minZ(double[][] W, double[] s){
        double[][] L = matrix_util.createL(W, W.length);
        int n = W.length;
        double[][] LPlusI = matrix_util.add(W, L);

        // z = (LPlusI)^-1 * s : calculateLE solves this linear equation
        // ここを実装
        z = calculateLE();
        return z;
    }

    public static double[][] minW(double[] z, double lam, double[][] W0, boolean reducePls, double gam, boolean existing) {
        int n = z.length;
        double[] d = new double[n];

        // Compute degrees
        for (int i = 0; i < n; i++) {
            d[i] = 0;
            for (int j = 0; j < n; j++) {
                d[i] += W0[i][j];
            }
        }

        // Define the objective function
        RealObjectiveFunction objectiveFunction = new RealObjectiveFunction(x -> {
            double[][] W = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i > j) {
                        W[i][j] = x[i * n + j];
                        W[j][i] = W[i][j];
                    }
                }
            }

            double objective = 0;
            double[][] L = createL(W, n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i > j) {
                        objective += (z[i] - z[j]) * (z[i] - z[j]) * W[i][j];
                    }
                }
            }

            if (reducePls) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (i > j) {
                            objective += gam * W[i][j] * W[i][j];
                        }
                    }
                }
            }

            return objective;
        });

        // Optimization variables and constraints
        double[] initialGuess = new double[n * n / 2];
        // Set initial guess here

        try {
            // Optimization algorithm
            CMAESOptimizer optimizer = new CMAESOptimizer();
            PointValuePair result = optimizer.optimize(objectiveFunction, initialGuess);

            double[][] W = new double[n][n];
            double[] optimizedValues = result.getPoint();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i > j) {
                        W[i][j] = optimizedValues[i * n + j];
                        W[j][i] = W[i][j];
                    }
                }
            }

            return W;
        } catch (OptimizationException e) {
            e.printStackTrace();
            return new double[n][n];
        }
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
