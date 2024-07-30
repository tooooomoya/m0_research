import java.util.ArrayList;
import javax.naming.spi.DirStateFactory.Result;
import utils.matrix_util;
import machines.*;

public class AdminGame {

    public static Result am(double[][] A, double[] s, double lam, boolean reducePls, double gam, int maxIter, boolean existing){
        double[][] W = matrix_util.copyMatrix(A);
        double[] z = optimization.minZ(W, s);


        List<Double> pls = new ArrayList<>();
        pls.add(optimization.computePls(z));

        double[][] L = matrix_util.createL(W, W.size);

        List<Double> disaggs = new ArrayList<>();
        // ここ実装

        int i = 0;
        boolean flag = true;
        while(flag){
            System.out.println("Iteration:" + i);

            double[][] Wnew = minW(z, lam, A, reducePls, gam, existing);
            double[] znew = minZ(Wnew, s);

            if(Math.max(norm(z, znew), norm(W, Wnew)) < 0.5 || i > maxIter - 1){
                flag = false;
            }

            z = znew;
            W = Wnew;
            i++;
            pls.add(computePls(z));
            L = matrix_util.createL(W, W.size);
            disaggs.add(computeDisagreement(z, L));
        }
    return new Result(pls, disaggs, z, W);
    }
}
