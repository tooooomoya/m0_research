
import org.apache.commons.math3.linear.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import java.util.Random;

public class GIFCreator {

    public static void createGIF(String outputFileName) {
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(outputFileName);
        gifEncoder.setDelay(500); // 各フレームの遅延時間 (ミリ秒)
        gifEncoder.setRepeat(0); // 繰り返し設定 (0 = 無限ループ)

        int width = 600; // 画像の幅
        int height = 400; // 画像の高さ

        // `Triple<Double, int[], double[][]>` を処理
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

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // 背景を白で塗りつぶす
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // 画像を2つの領域に分割
            int barGraphWidth = width / 2; // 左側: 棒グラフ
            int graphVisualizationWidth = width / 2; // 右側: グラフ可視化

            // 棒グラフを描画
            drawBarGraph(g2d, bins, lambda, barGraphWidth, height);

            // グラフの隣接行列を描画
            drawGraphVisualization(g2d, adjacencyMatrix, z, barGraphWidth, graphVisualizationWidth, height);

            gifEncoder.addFrame(image);
            g2d.dispose();
        }

        gifEncoder.finish();
        System.out.println("GIF created: " + outputFileName);
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
            // 色の決定
            double fraction = (double) i / bins.length;
            Color barColor = fraction <= 0.2 ? Color.RED
                    : fraction >= 0.8 ? Color.BLUE
                            : new Color(
                                    (int) (255 * (1 - ((fraction - 0.2) / 0.6))),
                                    0,
                                    (int) (255 * ((fraction - 0.2) / 0.6))
                            );

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
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(String.format("λ = %.2f", lambda), 10, 20);
    }

    private static void drawGraphVisualization(Graphics2D g2d, double[][] adjacencyMatrix, double[] nodeOpinions, int offsetX, int width, int height) {
        int n = adjacencyMatrix.length;
        Point[] nodePositions = new Point[n];
        int iterations = 100; // 配置計算の反復回数
        double stepSize = 10.0; // ノードの移動ステップサイズ
        int margin = 50; // 描画領域の余白
        int canvasWidth = width - margin * 2;
        int canvasHeight = height - margin * 2;

        nodePositions = computeLayout(adjacencyMatrix, offsetX, canvasWidth, canvasHeight, margin, 100, 0.5);

        /*
        // ノードの初期位置をランダムに設定
        for (int i = 0; i < n; i++) {
            nodePositions[i] = new Point(
                    offsetX + (int) (Math.random() * canvasWidth) + margin,
                    margin + (int) (Math.random() * canvasHeight)
            );
        }
        

        for (int iter = 0; iter < iterations; iter++) {
            Point[] newPositions = new Point[n];
            for (int i = 0; i < n; i++) {
                double dx = 0;
                double dy = 0;

                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        double weight = adjacencyMatrix[i][j];
                        if (weight > 0) {
                            Point p1 = nodePositions[i];
                            Point p2 = nodePositions[j];

                            double dist = p1.distance(p2);
                            double minDist = 5;
                            if (dist < minDist) {
                                dist = minDist; // 最小距離を設定
                            }

                            // 引力と反発力の計算
                            double force = weight / dist - 1 / (dist * dist);
                            dx += force * (p2.x - p1.x) / dist;
                            dy += force * (p2.y - p1.y) / dist;
                        }
                    }
                }

                // ステップサイズとキャンバス制限
                int newX = Math.min(offsetX + canvasWidth + margin,
                        Math.max(offsetX + margin, (int) (nodePositions[i].x + stepSize * dx)));
                int newY = Math.min(canvasHeight + margin,
                        Math.max(margin, (int) (nodePositions[i].y + stepSize * dy)));
                newPositions[i] = new Point(newX, newY);
            }
            nodePositions = newPositions;
        }*/
        // エッジを描画
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] > 0) {
                    Point p1 = nodePositions[i];
                    Point p2 = nodePositions[j];

                    // 重みによるエッジの色分け
                    double weight = adjacencyMatrix[i][j];
                    g2d.setColor(weight >= 5 ? Color.BLACK : Color.GRAY);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        // ノードを描画
        for (int i = 0; i < n; i++) {
            Point p = nodePositions[i];
            int nodeSize = 5;

            // ノードの色（意見属性に基づく赤-青グラデーション）
            double opinion = nodeOpinions[i];
            opinion = Math.min(1, Math.max(0, opinion));
            int red = (int) (255 * (1 - opinion));
            int blue = (int) (255 * opinion);
            g2d.setColor(new Color(red, 0, blue));
            g2d.fillOval(p.x - nodeSize / 2, p.y - nodeSize / 2, nodeSize, nodeSize);

            // ノードの枠線
            g2d.setColor(Color.BLACK);
            g2d.drawOval(p.x - nodeSize / 2, p.y - nodeSize / 2, nodeSize, nodeSize);
        }
    }

    public static Point[] computeLayout(
            double[][] adjacencyMatrix,
            int offsetX, int canvasWidth, int canvasHeight, int margin,
            int iterations, double stepSize) {

        int n = adjacencyMatrix.length;
        Random random = new Random();
        Point[] nodePositions = new Point[n];

        // 初期位置をランダムに設定
        for (int i = 0; i < n; i++) {
            int x = offsetX + margin + random.nextInt(canvasWidth - 2 * margin);
            int y = margin + random.nextInt(canvasHeight - 2 * margin);
            nodePositions[i] = new Point(x, y);
        }

        // 力学的レイアウト計算
        for (int iter = 0; iter < iterations; iter++) {
            Point[] newPositions = new Point[n];

            for (int i = 0; i < n; i++) {
                double dx = 0;
                double dy = 0;

                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        Point p1 = nodePositions[i];
                        Point p2 = nodePositions[j];

                        double weight = adjacencyMatrix[i][j];
                        double dist = p1.distance(p2);

                        if (dist == 0) {
                            dist = 0.1; // ゼロ除算を回避
                        }

                        // 重みが大きいほど引き寄せる力
                        if (weight > 0) {
                            double attraction = weight / dist; // 引力
                            dx += attraction * (p2.x - p1.x) / dist;
                            dy += attraction * (p2.y - p1.y) / dist;
                        }

                        // 距離が近すぎる場合の反発力
                        double repulsion = 1 / (dist * dist); // 反発力
                        dx -= repulsion * (p2.x - p1.x) / dist;
                        dy -= repulsion * (p2.y - p1.y) / dist;
                    }
                }

                // 新しい位置を計算（キャンバス範囲内に制限）
                int newX = Math.min(offsetX + canvasWidth - margin, Math.max(offsetX + margin, (int) (nodePositions[i].x + stepSize * dx)));
                int newY = Math.min(canvasHeight - margin, Math.max(margin, (int) (nodePositions[i].y + stepSize * dy)));
                newPositions[i] = new Point(newX, newY);
            }

            nodePositions = newPositions;
        }

        return nodePositions;
    }

    public static void main(String[] args) {
        // GIF を作成
        createGIF("output_with_graph.gif");
    }
}
