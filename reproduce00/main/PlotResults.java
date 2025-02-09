import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import main.utils.matrix_util;

public class PlotResults {
    public void exportPls(ResultPair resultPair, String filename, double[] lamvals ){
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double[] lamvals = {0.6, 0.7, 0.8, 0.9, 1.0};
        //plsAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] plsRatioList = new double[lamvals.length];
        //plsRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        
        for(int i = 0; i < lamvals.length; i++){
            ArrayList<Double> pls = rd.get(lamvals[i]).getPls();
            double initialPls = pls.get(0);
            System.out.println("initialPls: "+ initialPls);
            double finalPls = pls.get(pls.size() - 1);
            System.out.println("finalPls: "+ finalPls);
            plsRatioList[i] = (finalPls / initialPls) - 1;
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(("results/pls"+filename+".csv")))){
            writer.write("Lambda, val");
            writer.newLine();

            for(int i = 0; i < lamvals.length; i++){
                writer.write(lamvals[i] + "," + plsRatioList[i]);
                writer.newLine();
            }
            System.out.println("data has been written to the csv file");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void exportDisagg(ResultPair resultPair, String filename, double[] lamvals){
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double[] lamvals = {0.6, 0.7, 0.8, 0.9, 1.0};
        //disaggAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] disaggRatioList = new double[lamvals.length];
        //disaggRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        
        for(int i = 0; i < lamvals.length; i++){
            ArrayList<Double> disaggs = rd.get(lamvals[i]).getDisaggs();
            double initialDisagg = disaggs.get(0);
            System.out.println("initialDisagg: "+ initialDisagg);
            double finalDisagg = disaggs.get(disaggs.size() - 1);
            System.out.println("finalDisagg: "+ finalDisagg);

            disaggRatioList[i] = 100 * ((finalDisagg / initialDisagg) - 1);
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(("results/disagg"+filename+".csv")))){
            writer.write("Lambda, val");
            writer.newLine();

            for(int i = 0; i < lamvals.length; i++){
                writer.write(lamvals[i] + "," + disaggRatioList[i]);
                writer.newLine();
            }
            System.out.println("data has been written to the csv file");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}