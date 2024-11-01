import java.util.ArrayList;

import com.gurobi.gurobi.GRBException;

import main.utils.calculater;
import main.utils.calculater;
import main.utils.calculater;
import main.utils.calculater;
import main.utils.calculater;
import main.utils.*;
import main.structure.*;

public class AdminGame {

    public static Result am(double[][] A, double[] s, double lam, boolean reducePls, double gam, int maxIter,
            boolean existing) {
        double[][] W = matrix_util.copyMatrix(A);
        // System.out.println("the first W matrix");
        // matrix_util.printMatrix(W);

        // First, each user change its opinion according to the FJ model
        // System.out.println("\nthe first z: ");
        // matrix_util.printVector(s);
        double[] z = optimization.minZ(W, s);
        // System.out.println("\nz after the FJ effect: ");
        // matrix_util.printVector(z);

        ///// Set pls
        ArrayList<Double> pls = new ArrayList<>();
        pls.add(optimization.computePls(z));
        // System.out.println("\npls before iteration: "+ optimization.computePls(z));


        ///// Set disaggs
        double[][] L = matrix_util.createL(W, W.length);
        ArrayList<Double> disaggs = new ArrayList<>();
        disaggs.add(computeDisagreement(z, L));
        // System.out.println("\ndisagg before iteration: "+computeDisagreement(z, L));

        ///// Set gppls
        ArrayList<Double> gppls = new ArrayList<>();
        gppls.add(calculater.computeGpPls(z));

        ///// Set stfs
        ArrayList<Double> stfs = new ArrayList<>();
        stfs.add(calculater.computeStf(z, W));

        ///// Set dvs
        ArrayList<Double> dvs = new ArrayList<>();
        dvs.add(calculater.computeDvs(z, W));
        
        int i = 0;
        boolean flag = true;
        double[][] Wnew = null;
        boolean finderror = false;

        while (flag) {
            System.out.println("--------------------------");
            System.out.println("Iteration:" + i);

            try {
                // Admin changes weight matrix
                OptResult optResult = optimization.minWGurobi(z, lam, W, reducePls, gam, existing);
                finderror = optResult.getOpt();
                Wnew = optResult.getW();

                // System.out.println("\nnew W matrix");
                // matrix_util.printMatrix(Wnew);
                // ここのWがAだと最初の重み状態からの変化で、あんま意味ない気がする。
            } catch (GRBException e) {
                System.out.println("Gurobi optimization error: " + e.getMessage());
                e.printStackTrace();
            }  

            /// confirm the maximum weight
            double max_w = 0.0;
            for(int i=0; i<z.length;i++){
                for(int j=0; j<z.length; j++){
                    if(W[i][j] > max_w){
                        max_w = W[i][j];
                    }
                }
            }
            System.out.println("Maximum Weight of W matrix : " + max_w);

            // Wnew = W;

            // After Admin action, each user change its opinion according to the FJ model
            // System.out.println("\nz before this time Admin effect: ");
            // matrix_util.printVector(z);
            double[] znew = optimization.minZ(Wnew, s);
            // System.out.println("\nNew z after Admin effect: ");
            // matrix_util.printVector(znew);

            // Terminal Criterion(both z and W can be considered to be converged, or maxIter
            // criterion)
            System.out.println("z-znew:\n" + norm(z, znew));
            System.out.println("W-Wnew:\n" + matrixNorm(W, Wnew));
            if (Math.max(norm(z, znew), matrixNorm(W, Wnew)) < 5e-1 || i > maxIter - 1) {
                System.out.println("\nTerminal Criterion!!!!!!!");
                flag = false;
            }

            z = znew;
            W = Wnew;
            i++;
            

            //double PLS = calculateDiversity(znew, Wnew);

            double PLS = optimization.computePls(z);
            pls.add(PLS);
            System.out.println("\npls: " + PLS);

            L = matrix_util.createL(W, W.length);
            double disagg = computeDisagreement(z, L);
            disaggs.add(disagg);
            System.out.println("\ndisagg: " + disagg);

            double GPPLS = calculater.computeGpPls(z);
            gppls.add(GPPLS);
            System.out.println("\ngppls: " + GPPLS);

            double stf = calculater.computeStf(z, W);
            stfs.add(stf);
            System.out.println("\nstf: " + stf);

            double DVS = calculater.computeDvs(z, W);
            dvs.add(DVS);
            System.out.println("\ndvs: "+DVS);
        }
        return new Result(pls, disaggs, gppls, stfs, dvs, z, W, finderror);
    }



    /// Helper Functions

    // caluculate norm (ノルム：距離)
    private static double norm(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] -  b[i], 2);
        } 
        return Math.sqrt(sum);
    }

                      

                        
                            legalArgumentExc
                            
                         
                            u
                    
        // Calculate the sum of squared differences of corresponding elements
        for (int i = 0; i < matrix1.length; i++) {

                      

                        
                            
                                    
                         
                            a
                    

    // calculate z^T * L * z: Global disagreement is 
 
                    p  = new double[ z

                             
                               L * z 
                                i < z.length; i++) {
                           
         

        // then calculate z^T * temp
        for (int i = 0; i < z.length; i++) {
            disagreement += z[i] * temp[i];
        }     
  
        return d is agreement;
    }


        