import java.awt.*;
import java.awt.image.BufferedImage;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

public class GIFCreator {
    public static void createGIF(String outputFileName) {
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(outputFileName);
        gifEncoder.setDelay(500); // 各フレームの遅延時間 (ミリ秒)
        gifEncoder.setRepeat(0); // 繰り返し設定 (0 = 無限ループ)

        int width = 400;
        int height = 300;

        // `Pair<Double, int[]>` を受け取る
        for (Pair<Double, int[]> entry : GIFMaker.histograms) {
            double lambda = entry.getKey();
            int[] bins = entry.getValue();

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // 背景を白で塗りつぶす
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // 棒グラフ描画の準備
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
                // 色の決定
                double fraction = (double) i / bins.length;
                Color barColor;
                if (fraction <= 0.2) {
                    barColor = Color.RED; // 下位20%は赤
                } else if (fraction >= 0.8) {
                    barColor = Color.BLUE; // 上位20%は青
                } else {
                    // 赤から青へのグラデーション
                    double ratio = (fraction - 0.2) / 0.6;
                    int red = (int) (255 * (1 - ratio));
                    int green = 0;
                    int blue = (int) (255 * ratio);
                    barColor = new Color(red, green, blue);
                }

                // 棒グラフの高さ
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
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString(String.format("λ = %.2f", lambda), 10, 20);

            gifEncoder.addFrame(image);
            g2d.dispose();
        }

        gifEncoder.finish();
        System.out.println("GIF created: " + outputFileName);
    }

    public static void main(String[] args) {
        // GIF を作成
        createGIF("output.gif");
    }
}
