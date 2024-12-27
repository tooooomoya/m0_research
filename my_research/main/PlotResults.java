
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import main.structure.*;

public class PlotResults {

    public void exportPls(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double[] lamvals = {0.6, 0.7, 0.8, 0.9, 1.0};
        //plsAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] plsRatioList = new double[lamvals.length];
        //plsRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);

        for (int i = 0; i < lamvals.length; i++) {
            ArrayList<Double> pls = rdFix.get(lamvals[i]).getPls();
            double initialPls = pls.get(0);
            //System.out.println("initialPls: "+ initialPls);
            double finalPls = pls.get(pls.size() - 1);
            //System.out.println("finalPls: "+ finalPls);
            if(i == 0){
            System.err.println("The initial Pls: "+initialPls);
            }
            plsRatioList[i] = finalPls ;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/pls" + filename + ".csv")))) {
            writer.write("Lambda, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                writer.write(lamvals[i] + "," + plsRatioList[i]);
                writer.newLine();
            }
            //System.out.println("data has been written to the csv file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportDisagg(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double[] lamvals = {0.6, 0.7, 0.8, 0.9, 1.0};
        //disaggAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] disaggRatioList = new double[lamvals.length];
        //disaggRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);

        for (int i = 0; i < lamvals.length; i++) {
            ArrayList<Double> disaggs = rdFix.get(lamvals[i]).getDisaggs();
            double initialDisagg = disaggs.get(0);
            //System.out.println("initialDisagg: "+ initialDisagg);
            double finalDisagg = disaggs.get(disaggs.size() - 1);
            //System.out.println("finalDisagg: "+ finalDisagg);

            disaggRatioList[i] = finalDisagg / initialDisagg;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/disagg" + filename + ".csv")))) {
            writer.write("Lambda, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                writer.write(lamvals[i] + "," + disaggRatioList[i]);
                writer.newLine();
            }
            //System.out.println("data has been written to the csv file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportGppls(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double[] lamvals = {0.6, 0.7, 0.8, 0.9, 1.0};
        //disaggAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] gpplsRatioList = new double[lamvals.length];
        //disaggRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);

        for (int i = 0; i < lamvals.length; i++) {
            ArrayList<Double> gppls = rdFix.get(lamvals[i]).getGppls();
            double initialGppls = 0.0;
            int a = 0;
            while (true) {
                initialGppls = gppls.get(a);
                a++;
                if (initialGppls != 0.0 || a >= gppls.size()) {
                    break;
                }
            }
            if(initialGppls==0){
                initialGppls = 1;
            }
            //System.out.println("initialGppls: "+ initialGppls);
            double finalGppls = gppls.get(gppls.size() - 1);
            //System.out.println("finalGppls: "+ finalGppls);

            gpplsRatioList[i] = finalGppls;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/gppls" + filename + ".csv")))) {
            writer.write("Lambda, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                writer.write(lamvals[i] + "," + gpplsRatioList[i]);
                writer.newLine();
            }
            //System.out.println("data has been written to the csv file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportStfs(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double[] lamvals = {0.6, 0.7, 0.8, 0.9, 1.0};
        //disaggAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] stfsRatioList = new double[lamvals.length];
        //disaggRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);

        for (int i = 0; i < lamvals.length; i++) {
            ArrayList<Double> stfs = rdFix.get(lamvals[i]).getStfs();
            double initialStfs = stfs.get(0);
            //System.out.println("initialStfs: "+ initialStfs);
            double finalStfs = stfs.get(stfs.size() - 1);
            //System.out.println("finalStfs: "+ finalStfs);

            stfsRatioList[i] = finalStfs / initialStfs;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/stfs" + filename + ".csv")))) {
            writer.write("Lambda, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                writer.write(lamvals[i] + "," + stfsRatioList[i]);
                writer.newLine();
            }
            //System.out.println("data has been written to the csv file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportUdv(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double[] lamvals = {0.6, 0.7, 0.8, 0.9, 1.0};
        //disaggAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] udvRatioList = new double[lamvals.length];
        //disaggRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);

        for (int i = 0; i < lamvals.length; i++) {
            ArrayList<Double> udv = rdFix.get(lamvals[i]).getUdv();
            int a = 0;
            double initialUdv = 0.0;
            while (true) {
                initialUdv = udv.get(a);
                a++;
                if (initialUdv != 0.0 || a >= udv.size()) {
                    break;
                }
            }
            if(initialUdv==0){
                initialUdv=1;
            }
            //System.out.println("initialDvs: "+ initialDvs);
            double finalUdv = udv.get(udv.size() - 1);
            //System.out.println("finalDvs: "+ finalDvs);

            udvRatioList[i] = finalUdv / initialUdv;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/udv" + filename + ".csv")))) {
            writer.write("Lambda, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                writer.write(lamvals[i] + "," + udvRatioList[i]);
                writer.newLine();
            }
            //System.out.println("data has been written to the csv file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportCdv(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5};
        //double[] lamvals = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double[] lamvals = {0.6, 0.7, 0.8, 0.9, 1.0};
        //disaggAfterList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);
        double[] cdvRatioList = new double[lamvals.length];
        //disaggRatioList = matrix_util.createZeroMatrix(lamvals.length, lamvals.length);

        for (int i = 0; i < lamvals.length; i++) {
            ArrayList<Double> cdv = rdFix.get(lamvals[i]).getCdv();
            int a = 0;
            double initialCdv = 0.0;
            while (true) {
                initialCdv = cdv.get(a);
                a++;
                if (initialCdv != 0.0  || a >= cdv.size()) {
                    break;
                }
            }
            if(initialCdv==0){
                initialCdv=1;
            }

            //System.out.println("initialDvs: "+ initialDvs);
            double finalCdv = cdv.get(cdv.size() - 1);
            //System.out.println("finalDvs: "+ finalDvs);

            cdvRatioList[i] = finalCdv / initialCdv;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/cdv" + filename + ".csv")))) {
            writer.write("Lambda, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                writer.write(lamvals[i] + "," + cdvRatioList[i]);
                writer.newLine();
            }
            //System.out.println("data has been written to the csv file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
