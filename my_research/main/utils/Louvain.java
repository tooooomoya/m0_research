package main.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Louvain {

    // 隣接行列を受け取ってコミュニティに分割する
    public static Map<Integer, List<Integer>> louvainCommunityDetection(double[][] adjacencyMatrix) {
        for (int a = 0; a < adjacencyMatrix.length; a++) {
            for (int b = 0; b < adjacencyMatrix.length; b++) {
                if (adjacencyMatrix[a][b] > 0) {
                    adjacencyMatrix[a][b] = 1;
                }
            }
        }
        int n = adjacencyMatrix.length;

        // 初期コミュニティ割当 (各ノードが独自のコミュニティ)
        int[] communities = new int[n];
        for (int i = 0; i < n; i++) {
            communities[i] = i;
        }

        boolean improvement = true;
        double lastModularity = calculateModularity(adjacencyMatrix, communities);
        System.out.println("Modularity: " + lastModularity);
        int iterationsWithoutImprovement = 0;
        int maxIterationsWithoutImprovement = 2;  // ここで最大反復回数を設定
        int maxiter = 15;
        int num=0;

        while (improvement) {
            num++;
            System.out.println("Iteration:"+num);
            double currentModularity = 0;
            //System.out.println("start calculation");

            int[] next_community = new int[n];
            for (int a = 0; a < communities.length; a++) {
                next_community[a] = communities[a];
            }

            // 各ノードに対して、最もモジュラリティが向上するコミュニティに移動
            for (int node = 0; node < n; node++) {
                double my_basic_modu = lastModularity;
                int my_community = communities[node];

                // ノードの現在のコミュニティを一時的に解除
                communities[node] = -1;

                // 隣接ノードのコミュニティでのモジュラリティの計算
                for (int neighbor = 0; neighbor < n; neighbor++) {
                    if (adjacencyMatrix[node][neighbor] > 0 && node != neighbor) {
                        //nodeが隣接エージェントneighborに移動することを考えてみよう。で、このときのModularityが今より増えるなら記憶しておいて、それが最大となるコミュニティに移ろう。
                        int community = communities[neighbor];
                        communities[node] = community;
                        double ifmodularity = calculateModularity(adjacencyMatrix, communities);
                        if (ifmodularity > my_basic_modu) {
                            next_community[node] = community;
                            my_basic_modu = ifmodularity;
                        }else{
                            communities[node] = my_community;
                        }
                    }
                }
            }

            //各nodeが自分が移るべきcommunityを知っているから移る。そして新たにModularityを計算する。
            for (int a = 0; a < communities.length; a++) {
                communities[a] = next_community[a];
            }
            currentModularity = calculateModularity(adjacencyMatrix, communities);

            // モジュラリティの増加が小さい場合、もしくは一定回数改善がない場合に終了
            if (Math.abs(currentModularity - lastModularity) < 1e-6) {
                iterationsWithoutImprovement++;
            } else {
                iterationsWithoutImprovement = 0;
            }

            // 収束判定：モジュラリティの増加が小さい、または一定回数改善がない
            if (iterationsWithoutImprovement >= maxIterationsWithoutImprovement) {
                break;
            }
            if(num >= maxiter){
                break;
            }

            lastModularity = currentModularity;
            System.out.println("improved modularity: "+lastModularity);
        }

        // 結果をコミュニティごとにまとめる
        Map<Integer, List<Integer>> communityGroups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            communityGroups.computeIfAbsent(communities[i], k -> new ArrayList<>()).add(i);
        }

        return communityGroups;
    }

    // モジュラリティを計算
    private static double calculateModularity(double[][] adjacencyMatrix, int[] communities) {
        double modularity = 0.0;
        double total_weight = totalWeight(adjacencyMatrix);
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix[i].length; j++) {
                if (communities[i] == communities[j]) {
                    modularity += adjacencyMatrix[i][j] - (sumRow(adjacencyMatrix, i) * sumRow(adjacencyMatrix, j)) / (2.0 * total_weight);
                }
            }
        }
        return modularity / (2 * total_weight);
    }

    // 隣接行列の行の合計を計算
    private static double sumRow(double[][] matrix, int row) {
        double sum = 0.0;
        for (double val : matrix[row]) {
            sum += val;
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

    // コミュニティ結果をファイルに保存するメソッド
    public static void saveCommunitiesToFile(Map<Integer, List<Integer>> communities, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (Map.Entry<Integer, List<Integer>> entry : communities.entrySet()) {
                writer.write("コミュニティ " + entry.getKey() + ": " + entry.getValue().toString() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing community results to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        // 隣接行列を外部ファイルから読み込む
        double[][] adjacencyMatrix = readAdjacencyMatrix("results/adjacency_matrix.csv");

        if (adjacencyMatrix != null) {
            Map<Integer, List<Integer>> communities = louvainCommunityDetection(adjacencyMatrix);
            System.out.println("コミュニティに分割された結果:");
            for (Map.Entry<Integer, List<Integer>> entry : communities.entrySet()) {
                System.out.println("コミュニティ " + entry.getKey() + ": " + entry.getValue());
            }

            // 結果を外部ファイルに保存
            saveCommunitiesToFile(communities, "community_results.csv");
            System.out.println("結果が community_results.csv に保存されました。");
        } else {
            System.out.println("隣接行列の読み込みに失敗しました。");
        }
    }
}
