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
                if (W[i][j] > 0.0) {
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
        double connect_threshold = 0.0;
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

        /// calculate satisfaction
        double alpha = 0.8;
        double beta = 0.2;
        double gamma = 1.0 - (alpha + beta);
        double satisfaction = alpha * homogeneous + beta * connect;

        return satisfaction;
    }

    public static double computeUdv(double[] z, double[][] W) {
        double[] z_diversity = new double[z.length];

        for (int i = 0; i < z.length; i++) {
            double my_opinion = z[i];
            double adjacency_opinion_sum = 0; // calculate the number of agents having opposite opinion
            double adjacency_sum = 0;

            if (my_opinion > 0.5) {
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > 0) {
                        adjacency_sum += W[i][j];
                        if (z[j] < 0.5) {
                            adjacency_opinion_sum += W[i][j];
                        }
                    }
                }
            } else if (my_opinion < 0.5) {
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > 0) {
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
        int numPairs = (int) z.length / 2;
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
        int numPairs = (int) z.length / 2;
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

}
