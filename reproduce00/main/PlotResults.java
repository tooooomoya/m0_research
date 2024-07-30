import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import utils.matrix_util;

public class PlotResults {
    public void exportPls(HashMap<Double, List<Double>> rd){
        int[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //int[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //plsAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] plsRatioList = new double[lamvals.length];
        //plsRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        
        for(int i = 0; i < lamvals.length; i++){
            List<Double> pls = rd.get(lamvals[i]);
            double initialPls = pls.get(0);
            double finalPls = pls.get(pls.size() - 1);
            plsRatioList[i] = finalPls * 1.0 / initialPls;
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(("pls.csv")))){
            writer.write("Lambda, Percent change in pls");
            writer.newLine();

            for(int i = 0; i < lamvals.length; i++){
                writer.write(lamvals[i] + "," + plsRatioList[i]);
                writer.newLine();
            }
            System.out.println("data has been written to the csc file");
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void exportDisagg(HashMap<Double, List<Double>> rd){
        int[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //int[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //plsAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] plsRatioList = new double[lamvals.length];
        //plsRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        
        for(int i = 0; i < lamvals.length; i++){
            List<Double> pls = rd.get(lamvals[i]);
            double initialPls = pls.get(0);
            double finalPls = pls.get(pls.size() - 1);
            plsRatioList[i] = finalPls * 1.0 / initialPls;
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(("pls.csv")))){
            writer.write("Lambda, Percent change in pls");
            writer.newLine();

            for(int i = 0; i < lamvals.length; i++){
                writer.write(lamvals[i] + "," + plsRatioList[i]);
                writer.newLine();
            }
            System.out.println("data has been written to the csc file");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}