package main.utils;

import com.gurobi.gurobi.*;
import main.utils.matrix_util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.*;

import main.structure.OptResult;

public class calculater {

    // calculate Group Polarization
    public static double computeGpPls(double[] z) {
        int extreme = 0;
        for (int i = 0; i < z.length; i++) {
            if (z[i] <= 0.2 || z[i] >= 0.8) {
                extreme++;
            }
        }
        return (double) extreme / z.length;
    }

    // calculate user's satisfaction
    public static double computeStf(double[] z, double[][] W) {
        /// homogeneous effect
        double homogeneous = 0.0;
        for (int i = 0; i < z.length; i++) {
            int links = 0;
            int similar = 0;
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > 0) {
                    links++;
                    if (z[j] >= z[i] - 0.2 || z[j] <= z[i] + 0.2) {
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
        double connect_threshold = 0.2;
        for (int i = 0; i < z.length; i++) {
            double my_connect = 0.0;
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > connect_threshold) {
                    my_connect += W[i][j];
                }
            }
            connect += my_connect;
        }
        connect = connect / z.length;

        /// calculate satisfaction
        double satisfaction = 0.0;
        double alpha = 0.5;
        satisfaction = alpha * homogeneous + (1 - alpha) * connect;

        return satisfaction;
    }

    public static double computeDvs(double[] z, double[][] W) {
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

    /// Algorithm of Randomy Change of W
    public static List<int[]> selectPairs_v0(double[][] W, double[] z) {
        int numPairs = (int) z.length / 2;
        List<int[]> zeroPairs = new ArrayList<>();

        // W行列から値が0の(i, j)ペアを見つけてリストに格納
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W[i].length; j++) {
                if (W[i][j] == 0) {
                    zeroPairs.add(new int[] { i, j });
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

        // W行列から値が0の(i, j)ペアを見つけてリストに格納
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W[i].length; j++) {
                Pairs.add(new int[] { i, j });
            }
        }

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
