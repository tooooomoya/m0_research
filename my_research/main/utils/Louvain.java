
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Louvain {

    public static Map<Integer, List<Integer>> louvainCommunityDetection(double[][] adjacencyMatrix) {
        int n = adjacencyMatrix.length;

        // 初期コミュニティ割り当て (各ノードが独自のコミュニティ)
        int[] communities = new int[n];
        for (int i = 0; i < n; i++) {
            communities[i] = i;
        }

        double totalWeight = totalWeight(adjacencyMatrix);
        boolean improvement = true;
        int maxIterations = 100;  // 適度な上限を設定
        int numIterations = 0;

        while (improvement && numIterations < maxIterations) {
            numIterations++;
            improvement = false;

            // 各ノードを移動させる
            for (int node = 0; node < n; node++) {
                int originalCommunity = communities[node];  // 元のコミュニティを保持
                double maxDeltaQ = 0.0;
                int bestCommunity = originalCommunity;

                // 隣接ノードのコミュニティに対する ΔQ を計算
                for (int neighbor = 0; neighbor < n; neighbor++) {
                    if (adjacencyMatrix[node][neighbor] > 0 && node != neighbor) {
                        int neighborCommunity = communities[neighbor];
                        double deltaQ = calculateDeltaQ(adjacencyMatrix, communities, node, neighborCommunity, totalWeight);

                        // ΔQ が最大となるコミュニティを記録
                        if (deltaQ > maxDeltaQ) {
                            maxDeltaQ = deltaQ;
                            bestCommunity = neighborCommunity;
                        }
                    }
                }

                // ΔQ > 0 の場合のみコミュニティを変更
                if (maxDeltaQ > 0 && bestCommunity != originalCommunity) {
                    communities[node] = bestCommunity;
                    improvement = true;
                } else {
                    communities[node] = originalCommunity;
                }
            }
        }

        // 結果をコミュニティごとにまとめる
        Map<Integer, List<Integer>> communityGroups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            communityGroups.computeIfAbsent(communities[i], k -> new ArrayList<>()).add(i);
        }

        System.out.println("The number of communities: " + communityGroups.size());

        return communityGroups;
    }

    // 有向グラフ用のΔQを計算するメソッド
    private static double calculateDeltaQ(double[][] adjacencyMatrix, int[] communities, int node, int targetCommunity, double totalWeight) {
        double kout_i = sumRow(adjacencyMatrix, node); // ノード i の出次数
        double kin_i = sumColumn(adjacencyMatrix, node); // ノード i の入次数
        double kC_i = sumEdgesToCommunity(adjacencyMatrix, node, communities, targetCommunity); // ノード i とコミュニティC内のノードとのエッジ重みの合計

        // コミュニティC内のノードに接続する入次数と出次数の合計
        double sumC_tot_in = sumCommunityEdgesIn(adjacencyMatrix, communities, targetCommunity);
        double sumC_tot_out = sumCommunityEdgesOut(adjacencyMatrix, communities, targetCommunity);

        // ΔQ の計算式 (有向グラフ対応)
        double deltaQ = (kC_i / totalWeight) - ((kout_i * sumC_tot_in + kin_i * sumC_tot_out) / (totalWeight * totalWeight));
        return deltaQ;
    }

    // ノード i とコミュニティC 内のノードとのエッジ重みの合計
    private static double sumEdgesToCommunity(double[][] adjacencyMatrix, int node, int[] communities, int targetCommunity) {
        double sum = 0.0;
        for (int j = 0; j < adjacencyMatrix[node].length; j++) {
            if (communities[j] == targetCommunity) {
                sum += adjacencyMatrix[node][j];
            }
        }
        return sum;
    }

    // コミュニティC内のノードに向かう入次数の合計
    private static double sumCommunityEdgesIn(double[][] adjacencyMatrix, int[] communities, int targetCommunity) {
        double sum = 0.0;
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (communities[i] == targetCommunity) {
                sum += sumColumn(adjacencyMatrix, i);
            }
        }
        return sum;
    }

    // コミュニティC内のノードから出る出次数の合計
    private static double sumCommunityEdgesOut(double[][] adjacencyMatrix, int[] communities, int targetCommunity) {
        double sum = 0.0;
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (communities[i] == targetCommunity) {
                sum += sumRow(adjacencyMatrix, i);
            }
        }
        return sum;
    }

    // 隣接行列の行 (出次数) の合計
    private static double sumRow(double[][] matrix, int row) {
        double sum = 0.0;
        for (double val : matrix[row]) {
            sum += val;
        }
        return sum;
    }

    // 隣接行列の列 (入次数) の合計
    private static double sumColumn(double[][] matrix, int column) {
        double sum = 0.0;
        for (double[] row : matrix) {
            sum += row[column];
        }
        return sum;
    }

    // 隣接行列の全体の重みを計算
    private static double totalWeight(double[][] matrix) {
        double total = 0.0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                total += matrix[i][j];
            }
        }
        return total;
    }

    public static void main(String[] args) {
        // 隣接行列を外部ファイルから読み込む
        double[][] adjacencyMatrix = readAdjacencyMatrix("adjacency_matrix.csv");

        if (adjacencyMatrix != null) {
            Map<Integer, List<Integer>> communities = louvainCommunityDetection(adjacencyMatrix);
            System.out.println("コミュニティに分割された結果:");
            for (Map.Entry<Integer, List<Integer>> entry : communities.entrySet()) {
                System.out.println("コミュニティ " + entry.getKey() + ": " + entry.getValue());
            }
        } else {
            System.out.println("隣接行列の読み込みに失敗しました。");
        }
    }

    // CSVファイルから隣接行列を読み込むメソッド
    public static double[][] readAdjacencyMatrix(String filename) {
        List<double[]> matrixList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double[] row = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Double.parseDouble(values[i]);
                }
                matrixList.add(row);
            }
        } catch (IOException e) {
            System.err.println("Error reading adjacency matrix: " + e.getMessage());
            return null;
        }

        // リストから2次元配列に変換
        double[][] adjacencyMatrix = new double[matrixList.size()][];
        for (int i = 0; i < matrixList.size(); i++) {
            adjacencyMatrix[i] = matrixList.get(i);
        }
        return adjacencyMatrix;
    }
}
