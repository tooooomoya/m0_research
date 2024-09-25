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
        List<GRBVar> xList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i > j) {
                    GRBVar x = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x_" + i + "_" + j);
                    xList.add(x);
                }
            }
        }

        // Objective: minimize ∑_ij w_ij (zi - zj)^2
        GRBQuadExpr objExp = new GRBQuadExpr();
        for (GRBVar x : xList) {
            String[] indices = x.get(GRB.StringAttr.VarName).split("_");
            int i = Integer.parseInt(indices[1]);
            int j = Integer.parseInt(indices[2]);
            double w = (z[i] - z[j]) * (z[i] - z[j]);
            objExp.addTerm(w, x);
        }
        if (reducePls) {
            for (GRBVar x : xList) {
                objExp.addTerm(gam, x);
            }
        }
        model.setObjective(objExp, GRB.MINIMIZE);

        // Add constraints sum_j x[i,j] = di
        double[] d = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i] += W0[i][j];
            }
        }
        for (int i = 0; i < n; i++) {
            GRBLinExpr constr = new GRBLinExpr();
            for (GRBVar x : xList) {
                String[] indices = x.get(GRB.StringAttr.VarName).split("_");
                int ii = Integer.parseInt(indices[1]);
                int jj = Integer.parseInt(indices[2]);
                if (ii == i || jj == i) {
                    constr.addTerm(1.0, x);
                }
            }
            model.addConstr(constr, GRB.EQUAL, d[i], "c0_" + i);
        }

        // Add the constraint ∑(wij - w0ij) < lam * ||w0||^2
        // This part would need adjustment based on the actual implementation context
        // The right-hand side would be defined according to your original logic

        // Optimize the model
        model.optimize();

        // Retrieve the updated weight matrix W
        double[][] W = new double[n][n];
        for (GRBVar x : xList) {
            String[] indices = x.get(GRB.StringAttr.VarName).split("_");
            int i = Integer.parseInt(indices[1]);
            int j = Integer.parseInt(indices[2]);
            W[i][j] = x.get(GRB.DoubleAttr.X);
            W[j][i] = W[i][j]; // Since the weight matrix is symmetric
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
