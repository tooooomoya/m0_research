package main.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class calculater {

    // calculate Group Polarization
    public static double computeGpPls(double[] z) {
        int extreme = 0;
        for (int i = 0; i < z.length; i++) {
            if (z[i] <= 0.1 || z[i] >= 0.9) {
                extreme++;
            }
        }
        return (double) extreme / z.length;
    }

    // calculate user's satisfaction
    public static double computeStf(double[] z, double[][] W, Map<Integer, List<Integer>> communities) {
        /// homogeneous effect
        double homogeneous = 0.0;
        for (int i = 0; i < z.length; i++) {
            int links = 0;
            int similar = 0;
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > Constants.W_THRES) {
                    links++;
                    if (z[j] >= z[i] - 0.2 && z[j] <= z[i] + 0.2) {
                        similar++;
                    }
                }
            }
            if (links > 0) {
                homogeneous += (double) similar / links;
            } else {
                homogeneous = 0;
            }
        }
        homogeneous = homogeneous / z.length;

        /// connection effect
        double connect = 0.0;
        double connect_threshold = Constants.W_THRES;
        double p = 0.05;
        for (int i = 0; i < z.length; i++) {
            double my_connect = 0.0;
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > connect_threshold) {
                    //my_connect += W[i][j];
                    my_connect += 1;
                }
            }
            if (my_connect == 1) {
                my_connect = 0;
            } else {
                my_connect = 1 - 1 / Math.pow(Math.exp(my_connect), p);
            }
            connect += my_connect;
        }
        connect = connect / z.length;

        ///Diversity Effect
                int n = W.length; // ノード数
        double totalEntropy = 0.0;
        int validNodeCount = 0;

        for (int i = 0; i < n; i++) {
            // ノード i の接続ノードを収集
            List<Double> neighborOpinions = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (W[i][j] > Constants.W_THRES) {
                    neighborOpinions.add(z[j]);
                }
            }

            // 接続ノードが存在する場合のみエントロピーを計算
            if (!neighborOpinions.isEmpty()) {
                double[] probabilities = neighborOpinions.stream().mapToDouble(Double::doubleValue).toArray();
                double entropy = calculateEntropy(probabilities);
                totalEntropy += entropy;
                validNodeCount++;
            }
        }

        // エントロピーの平均値を計算（接続のないノードは無視）
        double avgentropy =  validNodeCount > 0 ? totalEntropy / validNodeCount : 0.0;
        

        /// calculate satisfaction
        double alpha = 0.4;
        double beta = 0.2;
        double gamma = 1.0 - (alpha + beta);
        double satisfaction = alpha * homogeneous + beta * connect + gamma * avgentropy;

        return satisfaction;
    }

    // エントロピーを計算する関数
    private static double calculateEntropy(double[] values) {
        // 値を正規化して確率分布に変換
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        if (sum == 0) {
            return 0.0; // 全ての値が0ならエントロピーは0
        }

        double entropy = 0.0;
        for (double value : values) {
            double p = value / sum; // 確率値
            if (p > 0) { // log(0) の計算を防ぐ
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        return entropy;
    }

    public static double computeUdv(double[] z, double[][] W) {
        double[] z_diversity = new double[z.length];

        for (int i = 0; i < z.length; i++) {
            double my_opinion = z[i];
            double adjacency_opinion_sum = 0; // calculate the number of agents having opposite opinion
            double adjacency_sum = 0;

            if (my_opinion > 0.5) {
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > Constants.W_THRES) {
                        adjacency_sum += W[i][j];
                        if (z[j] < 0.5) {
                            adjacency_opinion_sum += W[i][j];
                        }
                    }
                }
            } else if (my_opinion < 0.5) {
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > Constants.W_THRES) {
                        adjacency_sum += W[i][j];
                        if (z[j] > 0.5) {
                            adjacency_opinion_sum += W[i][j];
                        }
                    }
                }
            } else if (my_opinion == 0.5) {
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > 0) {
                        adjacency_sum += W[i][j];
                        adjacency_opinion_sum += W[i][j];
                    }
                }
            }

            if (adjacency_sum > 0) {
                z_diversity[i] = adjacency_opinion_sum / adjacency_sum;
            } else {
                z_diversity[i] = 0.0;
            }
        }

        double temp = 0;
        for (int i = 0; i < z.length; i++) {
            temp += z_diversity[i];
        }
        double diversity = temp / z_diversity.length;

        return diversity;
    }

    /// compute Community Diversity
    public static double computeCdv(double[] z, double[][] W, Map<Integer, List<Integer>> communities) {
        double diversity = 0.0;
        for (Map.Entry<Integer, List<Integer>> entry : communities.entrySet()) {
            double my_diversity = 0.0;

            List<Integer> agents = entry.getValue();
            double[] groupCounts = new double[5]; // 5つのグループに対応

            for (int agentIndex : agents) {
                double opinion = z[agentIndex];

                if (opinion < 0.2) {
                    groupCounts[0]++;
                } else if (opinion < 0.4) {
                    groupCounts[1]++;
                } else if (opinion < 0.6) {
                    groupCounts[2]++;
                } else if (opinion < 0.8) {
                    groupCounts[3]++;
                } else {
                    groupCounts[4]++;
                }
            }

            int num = 0;
            for (double count : groupCounts) {
                num += count;
            }

            if (num > 0) {

                for (double count : groupCounts) {
                    if (count > 0) {
                        double p = count / num;
                        my_diversity -= p * Math.log(p);
                    }
                }

                diversity += my_diversity;
            }
        }

        return diversity;
    }

    /// Algorithm of Randomy Change of W
    public static List<int[]> selectPairs_v0(double[][] W, double[] z) {
        int numPairs = (int) Constants.ALPHA * z.length;
        List<int[]> zeroPairs = new ArrayList<>();

        // W行列から値が0の(i, j)ペアを見つけてリストに格納
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W[i].length; j++) {
                if (W[i][j] == 0) {
                    zeroPairs.add(new int[]{i, j});
                }
            }
        }

        // 選びたいペアの数がリストにあるペアの数より多い場合は、リスト全体を返す
        if (numPairs >= zeroPairs.size()) {
            return zeroPairs;
        }

        // ランダムにnumPairs個のペアを選択
        List<int[]> selectedPairs = new ArrayList<>();
        Random random = new Random();
        while (selectedPairs.size() < numPairs) {
            int index = random.nextInt(zeroPairs.size());
            selectedPairs.add(zeroPairs.remove(index));
        }

        return selectedPairs;
    }

    /// Algorithm of Randomy Change of W
    public static List<int[]> selectPairs_v1(double[][] W, double[] z) {
        int numPairs = (int) Constants.ALPHA * z.length;
        List<int[]> Pairs = new ArrayList<>();

        // W行列から(i, j)ペアを見つけてリストに格納
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W[i].length; j++) {
                Pairs.add(new int[]{i, j});
            }
        }

        numPairs = Math.min(numPairs, Pairs.size());

        // ランダムにnumPairs個のペアを選択
        List<int[]> selectedPairs = new ArrayList<>();
        Random random = new Random();
        while (selectedPairs.size() < numPairs) {
            int index = random.nextInt(Pairs.size());
            selectedPairs.add(Pairs.remove(index));
        }

        return selectedPairs;
    }

    public static double[][] friendRecommend(double[][] W, double[] z) {

        int n = W.length; // 隣接行列のサイズ
        Random random = new Random();
        double[][] W1 = matrix_util.copyMatrix(W);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (W1[i][j] > Constants.W_THRES) {
                    W1[i][j] = 1;
                }else{
                    W1[i][j] = 0;
                }
            }
        }

        // 隣接行列を二乗して友達の友達を計算
        double[][] W2 = matrix_util.multiply(W1, W1);
        int changedlink = 0;
        double avWeight = 0.0;

        // 各ノードについて最も重みが小さいリンクを特定
        for (int i = 0; i < n; i++) {
            int minIndex = -1;
            double maxDiff = 0;

            // 最小重みのリンクを探索
            for (int j = 0; j < n; j++) {
                if (W[i][j] > 0.1) {
                    double diff = Math.abs(z[i] - z[j]);
                    if (diff > maxDiff) {
                        maxDiff = diff;
                        minIndex = j;
                    }
                }
            }

            // 友達の友達からランダムに1つ選ぶ
            if (minIndex != -1) {
                int newFriend = -1;
                int attempts = 0; // 安全対策でループ回数を制限
                do {
                    newFriend = random.nextInt(n);
                    attempts++;
                    //W2[i][newFriend]が1だったら、そいつは友達の友達だからそいつで決定！。
                } while (attempts < 100 && (W2[i][newFriend] == 0 || newFriend == i || newFriend == minIndex));

                // リンクを付け替える
                if (newFriend != -1 && W2[i][newFriend] > 0) {
                    double swapWeight = W[i][minIndex];
                    W[i][minIndex] = 0; // 元のリンクを削除
                    W[i][newFriend] = swapWeight; // 新しいリンクを追加
                    changedlink++;
                    avWeight += swapWeight;
                }
            }
        }
        System.out.println("\nthe num of links changed in friendRecommend: " + changedlink);
        System.out.println("the avg of weight changed in friendReccomend: " + avWeight / changedlink);
        return W;
    }

}
