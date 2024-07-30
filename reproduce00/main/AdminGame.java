import java.util.ArrayList;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;
import utils.matrix_util;
import machines.*;

public class AdminGame {

    public static Result am(double[][] A, double[] s, double lam, boolean reducePls, double gam, int maxIter, boolean existing){
        double[][] W = matrix_util.copyMatrix(A);
        double[] z = optimization.minZ(W, s);


        List<Double> pls = new ArrayList<>();
        pls.add(optimization.computePls(z));

        double[][] L = matrix_util.createL(W, W.length);

        double[] disaggs = new double[W.length];
        disaggs=computeDisagreement(z, L);

        int i = 0;
        boolean flag = true;
        while(flag){
            System.out.println("Iteration:" + i);

            double[][] Wnew = optimization.minW(z, lam, A, reducePls, gam, existing);
            double[] znew = optimization.minZ(Wnew, s);

            if(Math.max(norm(z, znew), norm(W, Wnew)) < 0.5 || i > maxIter - 1){
                flag = false;
            }

            z = znew;
            W = Wnew;
            i++;
            pls.add(optimization.computePls(z));
            L = matrix_util.createL(W, W.length);
            disaggs = computeDisagreement(z, L);
        }
    return new Result(pls, disaggs, z, W);
    }


    private static double norm(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    private static double[] computeDisagreement(double[] z, double[][] L){
        double[] disaggs = new double[z.length];
        double[] temp = new double[z.length];
        // calculate L * z
        for(int i = 0 ; i < z.length; i++){
            for(int j = 0; j < z.length; j++){
                temp[i] += L[i][j] * z[j];
            }
        }
        // then calculate z^T * temp
        for(int i = 0; i < n; i++){
            disaggs[i] = z[i] * temp[i];
        }
        return disaggs;
    }
}
