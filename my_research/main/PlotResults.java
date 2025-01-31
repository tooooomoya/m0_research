
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.imageio.ImageIO;
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
            if (i == 0) {
                System.err.println("The initial Pls: " + initialPls);
            }
            plsRatioList[i] = finalPls;
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

    public void exportStepDisagg(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/disaggStep" + filename + ".csv")))) {
            writer.write("Lambda, step, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                ArrayList<Double> disaggs = rdFix.get(lamvals[i]).getDisaggs();
                for(int j = 0; j < disaggs.size(); j++){
                    writer.write(lamvals[i] + "," + j + "," + disaggs.get(j));
                    writer.newLine();
                }
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
            if (initialGppls == 0) {
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

    public void exportStepGppls(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/gpplsStep" + filename + ".csv")))) {
            writer.write("Lambda, step, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                ArrayList<Double> gppls = rdFix.get(lamvals[i]).getCdv();
                for(int j = 0; j < gppls.size(); j++){
                    writer.write(lamvals[i] + "," + j + "," + gppls.get(j));
                    writer.newLine();
                }
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

    public void exportStepStfs(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/stfsStep" + filename + ".csv")))) {
            writer.write("Lambda, step, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                ArrayList<Double> stfs = rdFix.get(lamvals[i]).getStfs();
                for(int j = 0; j < stfs.size(); j++){
                    writer.write(lamvals[i] + "," + j + "," + stfs.get(j));
                    writer.newLine();
                }
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
            if (initialUdv == 0) {
                initialUdv = 1;
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
                if (initialCdv != 0.0 || a >= cdv.size()) {
                    break;
                }
            }
            if (initialCdv == 0) {
                initialCdv = 1;
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

    public void exportStepCdv(ResultPair resultPair, String filename, double[] lamvals) {
        HashMap<Double, Result> rd = resultPair.getRd();
        HashMap<Double, Result> rdFix = resultPair.getRdFix();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(("results/cdvStep" + filename + ".csv")))) {
            writer.write("Lambda, step, val");
            writer.newLine();

            for (int i = 0; i < lamvals.length; i++) {
                ArrayList<Double> cdvs = rdFix.get(lamvals[i]).getCdv();
                for(int j = 0; j < cdvs.size(); j++){
                    writer.write(lamvals[i] + "," + j + "," + cdvs.get(j));
                    writer.newLine();
                }
            }
            //System.out.println("data has been written to the csv file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateHistogram(double[] z, double[] s, String outputPath) {
        // 距離 z[i] - s[i] を計算
        double[] differences = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            differences[i] = z[i] - s[i];
        }

        // ヒストグラム用のデータを準備
        int bins = 20; // ビン数
        double min = -1.0; // 横軸を-1から+1に固定
        double max = 1.0;
        double binWidth = (max - min) / bins;
        int[] frequencies = new int[bins];

        for (double diff : differences) {
            if (diff < min || diff > max) {
                continue; // 範囲外のデータは無視
            }
            int binIndex = (int) ((diff - min) / binWidth);
            if (binIndex >= bins) {
                binIndex = bins - 1; // 上限対策

                        }frequencies[binIndex]++;
        }

        // ヒストグラムを描画するための画像を生成
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 背景を白で塗りつぶす
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 軸やラベルを描画
        g.setColor(Color.BLACK);
        g.drawLine(50, 550, 750, 550); // X軸
        g.drawLine(50, 550, 50, 50);  // Y軸

        // 縦軸のメモリ追加
        int maxFrequency = Arrays.stream(frequencies).max().orElse(1);
        double scale = 500.0 / maxFrequency;
        for (int i = 0; i <= 10; i++) {
            int y = 550 - (int) (i * maxFrequency * scale / 10);
            g.drawLine(45, y, 50, y); // 目盛線
            g.drawString(String.valueOf(i * maxFrequency / 10), 10, y + 5); // ラベル
        }

        // 軸ラベル
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Difference (z[i] - s[i])", 350, 580);
        g.drawString("Frequency", 10, 40);

        // ヒストグラムの描画
        int barWidth = (700 / bins); // ヒストグラムのバーの幅
        for (int i = 0; i < bins; i++) {
            int barHeight = (int) (frequencies[i] * scale);
            int x = 50 + i * barWidth;
            int y = 550 - barHeight;
            g.setColor(Color.BLUE);
            g.fillRect(x, y, barWidth - 2, barHeight); // バーを描画
            g.setColor(Color.BLACK);
            g.drawRect(x, y, barWidth - 2, barHeight); // 枠線を描画
        }

        // 横軸のラベルを描画（-1から+1に固定）
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= bins; i++) {
            double binValue = min + i * binWidth;
            String label = String.format("%.2f", binValue);
            int labelX = 50 + i * barWidth - (label.length() > 3 ? 10 : 5);
            g.drawString(label, labelX, 570);
        }

        // グラフィックのリソースを解放
        g.dispose();

        // 画像を保存
        File directory = new File(outputPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File outputFile = new File(outputPath + "/difference_histogram.jpg");
        try {
            ImageIO.write(image, "jpg", outputFile);
            System.out.println("ヒストグラムが保存されました: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("ヒストグラムの保存中にエラーが発生しました: " + e.getMessage());
        }
    }

}
