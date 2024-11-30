
import org.apache.commons.math3.linear.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import java.io.FileWriter;
import java.io.IOException;

public class GIFCreator {

    public static void createGIF(String outputFileName) {
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(outputFileName);
        gifEncoder.setDelay(500); // 各フレームの遅延時間 (ミリ秒)
        gifEncoder.setRepeat(0); // 繰り返し設定 (0 = 無限ループ)

        int width = 600; // 画像の幅
        int height = 400; // 画像の高さ

        int step = 0;
        for (Triple<Double, double[], double[][]> entry : GIFMaker.histogramsWithAdjacency) {
            double lambda = entry.getKey();
            double[] z = entry.getValue1();
            double[][] adjacencyMatrix = entry.getValue2();
            int numBins = 20;

            int[] bins = new int[numBins];
            double binWidth = 1.0 / numBins;

            // 度数分布を計算
            for (double value : z) {
                int binIndex = (int) Math.min(value / binWidth, numBins - 1);
                bins[binIndex]++;
            }

            // 隣接行列をCSVに出力
            saveAdjacencyMatrixToCSV(step, adjacencyMatrix);

            // zの情報をCSVに出力
            saveZToCSV(step, z);

            // λの情報をCSVに出力
            saveLambdaToCSV(step, lambda);

            // 画像生成
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // 背景を白で塗りつぶす
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // 度数分布を描画
            drawBarGraph(g2d, bins, lambda, width, height);

            gifEncoder.addFrame(image);
            g2d.dispose();

            step++;
        }

        gifEncoder.finish();
        System.out.println("GIF created: " + outputFileName);
    }

    private static void saveAdjacencyMatrixToCSV(int step, double[][] adjacencyMatrix) {
        String fileName = String.format("Temp/graph_step%d.csv", step);
        try (FileWriter writer = new FileWriter(fileName)) {
            // 隣接行列を保存
            for (double[] row : adjacencyMatrix) {
                for (int j = 0; j < row.length; j++) {
                    writer.append(String.valueOf(row[j]));
                    if (j < row.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // zの情報をCSVに出力
    private static void saveZToCSV(int step, double[] z) {
        String fileName = String.format("Temp/z_step%d.csv", step);
        try (FileWriter writer = new FileWriter(fileName)) {
            // zの各値を保存
            for (double value : z) {
                writer.append(String.valueOf(value)).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // λの情報をCSVに出力
    private static void saveLambdaToCSV(int step, double lambda) {
        String fileName = String.format("Temp/lambda_step%d.csv", step);
        try (FileWriter writer = new FileWriter(fileName)) {
            // λの値を保存
            writer.append(String.valueOf(lambda)).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawBarGraph(Graphics2D g2d, int[] bins, double lambda, int width, int height) {
        int barWidth = (width - 50) / bins.length; // x軸左に50の余白を考慮
        int maxCount = 0;
        for (int count : bins) {
            maxCount = Math.max(maxCount, count);
        }

        // y軸のスケール
        int yAxisHeight = height - 70; // 上下の余白を考慮
        int yAxisLabelCount = 5; // y軸ラベルの数

        // y軸のラベルを描画
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i <= yAxisLabelCount; i++) {
            int yLabelValue = maxCount * i / yAxisLabelCount;
            int yPosition = height - 50 - (yAxisHeight * i / yAxisLabelCount);
            g2d.drawString(String.valueOf(yLabelValue), 5, yPosition + 5);
            g2d.drawLine(50, yPosition, width - 10, yPosition); // グリッドライン
        }

        // 棒グラフの描画
        for (int i = 0; i < bins.length; i++) {
            double fraction = (double) i / bins.length;
            Color barColor = fraction <= 0.2 ? Color.RED
                    : fraction >= 0.8 ? Color.BLUE
                            : new Color(
                                    (int) (255 * (1 - ((fraction - 0.2) / 0.6))),
                                    0,
                                    (int) (255 * ((fraction - 0.2) / 0.6))
                            );

            int barHeight = (int) ((double) bins[i] / maxCount * yAxisHeight);
            g2d.setColor(barColor);
            g2d.fillRect(50 + i * barWidth, height - barHeight - 50, barWidth - 2, barHeight); // -2で棒の間に間隔
        }

        // x軸のラベルを描画
        g2d.setColor(Color.BLACK);
        for (int i = 0; i <= 5; i++) {
            double xLabelValue = 0.2 * i;
            int xPosition = 50 + (int) (bins.length * xLabelValue / 1.0 * barWidth);
            g2d.drawString(String.format("%.1f", xLabelValue), xPosition - 10, height - 30);
        }

        // λ の値を表示
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(String.format("λ = %.2f", lambda), 10, 20);
    }

    public static void main(String[] args) {
        createGIF("output_with_adjacency_and_z.gif");
    }
}
