package main.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class calculater {

    // calculate Disagreement
    public static double computeDisagreement(double[] z, double[][] W, boolean[] isDiversityUser) {
        double disagg = 0.0;
        for (int i = 0; i < z.length; i++) {
            if (isDiversityUser[i]) {
                continue;
            }
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > 0 && i != j) {
                    disagg += W[i][j] * (z[i] - z[j]) * (z[i] - z[j]);
                }
            }
        }
        return disagg;
    }

    // calculate Group Polarization
    /*public static double computeGpPls(double[] z) {
        int extreme = 0;
        for (int i = 0; i < z.length; i++) {
            if (z[i] <= 0.1 || z[i] >= 0.9) {
                extreme++;
            }
        }
        return (double) extreme / z.length;
    }*/
    public static double computeGpPls(double[] z, double[] s) {
        double diff = 0;
        int num = 0;
        for (int i = 0; i < z.length; i++) {
            if (s[i] == 0.5) {
                continue;
            }
            num++;
            diff += Math.abs((z[i] - 0.5) / (s[i] - 0.5));
        }
        if (diff == 0) {
            return 0;
        } else {
            return (double) diff / num;
        }
    }

    // calculate user's satisfaction
    public static double computeStf(double[] z, double[][] W, Map<Integer, List<Integer>> communities, boolean[] isDiversityUser) {
        /// homogeneous effect
        double homogeneous = 0.0;
        double total = 0.0;
        for (int i = 0; i < z.length; i++) {
            if (isDiversityUser[i]) {
                continue;
            }
            double links = 0.0;
            double similar = 0.0;
            for (int j = 0; j < z.length; j++) {
                total += W[i][j];
                if (W[i][j] > Constants.W_THRES) {
                    links += W[i][j];
                    if (z[j] >= z[i] - 0.1 && z[j] <= z[i] + 0.1) {
                        double z_diff = Math.abs(z[i] - z[j]);
                        similar += W[i][j] * (-10 * z_diff + 1);
                    }
                }
            }
            if (links > 0) {
                homogeneous += (double) similar / links;
            }
        }
        //homogeneous = homogeneous / total;//全体のリンクのうち、何割が意見が近い人との交流に当てられたか。
        homogeneous = homogeneous / z.length;
        //homogeneous = 1 / (1 + Math.exp(- 2 * homogeneous + 4));

        /// connection effect
        double connect = 0.0;
        double connect_threshold = Constants.W_THRES;
        double p = 0.05;
        for (int i = 0; i < z.length; i++) {
            if (isDiversityUser[i]) {
                continue;
            }
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
            if (isDiversityUser[i]) {
                continue;
            }
            // ノード i の接続ノードを収集
            List<Double> neighborOpinions = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (W[i][j] > Constants.W_THRES) {
                    neighborOpinions.add(z[j]);
                }
            }

            // 接続ノードが存在する場合のみエントロピーを計算
            if (!neighborOpinions.isEmpty()) {
                // 0.2刻みのビンに分類
                int[] bins = new int[5]; // 0.2刻みで5グループ
                for (double opinion : neighborOpinions) {
                    int binIndex = (int) Math.min(opinion / 0.2, 4); // 0.2で割り、範囲外を防ぐ
                    bins[binIndex]++;
                }

                // 確率を計算
                double[] probabilities = new double[5];
                int totalCount = neighborOpinions.size();
                for (int k = 0; k < 5; k++) {
                    probabilities[k] = (double) bins[k] / totalCount;
                }

                // エントロピーを計算
                double entropy = calculateEntropy(probabilities);
                totalEntropy += entropy;
                validNodeCount++;
            }
        }

        // エントロピーの平均値を計算（接続のないノードは無視）
        double avgentropy = validNodeCount > 0 ? totalEntropy / validNodeCount : 0.0;
        avgentropy = avgentropy / 2.322;
        /*if (avgentropy > 1) {
            avgentropy = 1.0;
            System.out.println("Avg Entropy went over 1.0, so this gonna be clipped to 1.0.");
        }*/

        /// calculate satisfaction
        double alpha = 0.5;
        double beta = 0.0;
        double gamma = 1.0 - (alpha + beta);
        double satisfaction = alpha * homogeneous + beta * connect + gamma * avgentropy;
        System.out.println("\nEcho effect in Stfs: " + homogeneous);
        System.out.println("Diversity effect in Stfs: " + avgentropy);
        //System.out.println("Connect effect in Stfs: " + connect);

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

    /*public static double computeUdv(double[] z, double[][] W) {
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
    }*/
    public static double computeUdv(double[] z, double[][] W, boolean[] isDiversityUser) {
        double entropy = 0.0;

        for (int i = 0; i < z.length; i++) {
            if (isDiversityUser[i]) {
                continue;
            }
            int[] bins = new int[20];
            int totalCount = 0;
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > Constants.W_THRES) {
                    int binIndex = (int) Math.min(z[j] / 0.05, 19);
                    bins[binIndex]++;
                    totalCount++;
                }
            }
            if (totalCount == 0) {
                continue;
            }
            double[] probabilities = new double[20];
            for (int k = 0; k < 20; k++) {
                probabilities[k] = (double) bins[k] / totalCount;
            }
            entropy += calculateEntropy(probabilities);
        }
        return entropy / z.length;
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
                if (W[i][j] == 0 && i != j) {
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
    public static List<int[]> selectPairs_v1(double[][] W) {
        int numPairs = (int) (Constants.ALPHA * W.length);
        List<int[]> Pairs = new ArrayList<>();

        // W行列から(i, j)ペアを見つけてリストに格納
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W[i].length; j++) {
                if (i != j) {
                    Pairs.add(new int[]{i, j});
                }
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

    public static double[][] friendRecommend(double[][] W, double[] z, int[] diversityUserList) {
        Random random = new Random();
        
        double[][] W_01 = new double[z.length][z.length];
        for (int i = 0; i < z.length; i++) {
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > Constants.W_THRES && i != j) {
                    W_01[i][j] = 1;
                } else {
                    W_01[i][j] = 0;
                }
            }
        }

        int[] my_follow = new int[z.length];
        for (int i = 0; i < z.length; i++) {
            for (int j = 0; j < z.length; j++) {
                if (W_01[i][j] > 0) {
                    my_follow[i]++;
                }
            }
        }

        double[][] FRofFR = matrix_util.multiply(W_01, W_01);

        double[] user_weight_sum = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            for (int j = 0; j < z.length; j++) {
                user_weight_sum[i] += W[i][j];
                if (i == j) {
                    FRofFR[i][j] = 0;
                }
            }
        }

        // ノードごとのフォロワーリストを事前計算
        List<List<Integer>> followers = new ArrayList<>();
        for (int i = 0; i < z.length; i++) {
            followers.add(new ArrayList<>());
            for (int j = 0; j < z.length; j++) {
                if (W_01[j][i] == 1) {
                    followers.get(i).add(j); // ノードiをフォローしているノードを記録
                }
            }
        }

        double[][] collabo_matrix = new double[z.length][z.length];
        for (int i = 0; i < z.length; i++) {
            for (int j = 0; j < z.length; j++) {
                if (W_01[i][j] == 1) {
                    for (int follower : followers.get(j)) {
                        if (follower != i) {
                            collabo_matrix[i][follower] = 1;
                        }
                    }
                }
            }
        }

        int rnd = random.nextInt(101);
        boolean[] div_label = new boolean[z.length];
        for (int i = 0; i < z.length; i++) {
            for (int k = 0; k < diversityUserList.length; k++) {
                if (i == diversityUserList[k] && rnd > Constants.DIV_NORMAL_RATE) {
                    div_label[i] = true;
                }
            }
        }

        boolean[] echo_label = new boolean[z.length];

        for (int i = 0; i < z.length; i++) {
            int echo_num = 0;
            int friend_num = 0;
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > 0) {
                    friend_num++;
                }
                if (W[i][j] > 0 && Math.abs(z[i] - z[j]) < Constants.MAX_DIFF) {
                    echo_num++;
                }
            }
            if (echo_num == friend_num) {
                echo_label[i] = true;
            }
        }

        
        int div_action_num = 0;
        int unfollow_num = 0;
        double avg_unfollow_weight = 0.0;
        double avg_unfollow_diff = 0.0;
        int echo_follow_num = 0;
        double total_add_weight = 0.0;
        double total_sub_weight = 0.0;

        for (int i = 0; i < z.length; i++) {
            if (div_label[i]) {
                //多様性志向のあるユーザは意見が遠い人を意図的に選んでいく。
                int randomNumber = random.nextInt(101);
                if (randomNumber < (int) (100 * Constants.DIV_ACTION_RATE)) {
                    int attempts = 0;
                    while (attempts < 10) {
                        int new_follow_id = random.nextInt(z.length);
                        if (i != new_follow_id && Math.abs(z[i] - z[new_follow_id]) > Constants.DIV_DIFF) {
                            int friend_num = 0;
                            double overflow = 0.0;
                            for (int j = 0; j < z.length; j++) {
                                if (W[i][j] > 0) {
                                    friend_num++;
                                }
                            }
                            for (int j = 0; j < z.length; j++) {
                                if (W[i][j] > 0) {
                                    double temp = 0.0;
                                    W[i][j] -= (double) (Constants.DIV_NEW_WEIGHT / friend_num);
                                    if (W[i][j] < 0) {
                                        overflow += Math.abs(W[i][j]);
                                        temp = Math.abs(W[i][j]);
                                        W[i][j] = 0;
                                    }
                                    total_sub_weight += ((double) (Constants.DIV_NEW_WEIGHT / friend_num) + temp);
                                }
                            }

                            W[i][new_follow_id] += (Constants.DIV_NEW_WEIGHT + overflow);
                            total_add_weight += (Constants.DIV_NEW_WEIGHT + overflow);
                            div_action_num++;
                            break;
                        }
                        attempts++;
                    }
                }
            } /*else if (!echo_label[i]) {
                //ランキング最下位をUnfollowして、探索してランダムに意見が近いユーザをFollowする
                int randomNumber = random.nextInt(101);
                if (randomNumber < (int) (100 * Constants.FR_PROB)) {
                    List<AgentRanking> rankings = new ArrayList<>();
                    for (int j = 0; j < z.length; j++) {
                        if (W[i][j] > Constants.W_THRES && i != j) { // iがjをフォローしている場合
                            double weight = W[i][j];
                            double opinionDifference = Math.abs(z[i] - z[j]);
                            if(opinionDifference < Constants.MAX_DIFF){
                                continue;
                            }
                            double score = weight * opinionDifference;
                            rankings.add(new AgentRanking(j, score));
                        }
                    }
                    rankings.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
                    AgentRanking ranking = rankings.get(0);
                    int agentId = ranking.getAgentId();
                    double sub_weight = W[i][agentId];
                    total_sub_weight += sub_weight;
                    avg_unfollow_weight += sub_weight;
                    avg_unfollow_diff += ranking.getScore() / W[i][agentId];
                    W[i][agentId] = 0;
                    
                    int attempts = 0;
                    while (attempts < 100) {
                        int new_follow_id = random.nextInt(z.length);
                        if (new_follow_id != agentId && i != new_follow_id && Math.abs(z[i] - z[new_follow_id]) < Constants.NOT_DIV_DIFF) {
                            W[i][new_follow_id] += sub_weight;
                            total_add_weight += sub_weight;
                            unfollow_num++;
                            //double[] weight_temp = calculateUserTotalWeight(W);
                            //System.out.println("DIFF : "+(user_weight_sum[i] - weight_temp[i]));
                            break;
                        }
                        attempts++;
                    }
                }
            } else if(echo_label[i]){*/
            else {

                //探索してランダムに意見が近いユーザをFollowする
                int randomNumber = random.nextInt(101);
                if (randomNumber < (int) (100 * Constants.FR_PROB)) {
                    int attempts = 0;
                    while (attempts < 10) {
                        int new_follow_id = random.nextInt(z.length);

                        //if (i != new_follow_id && Math.abs(z[i] - z[new_follow_id]) < Constants.NOT_DIV_DIFF && collabo_matrix[i][new_follow_id] == 1) {
                        //if (i != new_follow_id && Math.abs(z[i] - z[new_follow_id]) < Constants.NOT_DIV_DIFF && FRofFR[i][new_follow_id] == 1) {
                        if (i != new_follow_id && Math.abs(z[i] - z[new_follow_id]) < Constants.NOT_DIV_DIFF) {
                            double overflow = 0.0;
                            int friend_num = 0;
                            for (int j = 0; j < z.length; j++) {
                                if (W[i][j] > 0 && i != j) {
                                    friend_num++;
                                }
                            }
                            for (int j = 0; j < z.length; j++) {
                                if (W[i][j] > 0 && i != j) {
                                    double temp = 0.0;
                                    W[i][j] -= (double) Constants.NEW_WEIGHT / friend_num;
                                    if (W[i][j] < 0) {
                                        overflow += Math.abs(W[i][j]);
                                        temp = Math.abs(W[i][j]);
                                        W[i][j] = 0;
                                        //System.out.println("overflow" + temp);
                                    }
                                    total_sub_weight += (Constants.NEW_WEIGHT / friend_num);
                                }
                            }
                            //すでに重みがある人に関しては本当は別に実装する必要がある

                            W[i][new_follow_id] += (Constants.NEW_WEIGHT - overflow);
                            total_add_weight += (Constants.NEW_WEIGHT - overflow);
                            echo_follow_num++;
                            //double[] weight_temp = calculateUserTotalWeight(W);
                            //System.out.println("DIFF : "+(user_weight_sum[i] - weight_temp[i]));
                            break;
                        }
                        attempts++;
                    }
                }
            }
        }

        System.out.println("diversity action : " + div_action_num);
        System.out.println("unfollow action : " + unfollow_num);
        System.out.println("avg unfollow weight : " + avg_unfollow_weight / unfollow_num);
        System.out.println("avg unfollow diff : " + avg_unfollow_diff / unfollow_num);
        System.out.println("echo & just follow action : " + echo_follow_num);
        System.out.println("total added weight : " + total_add_weight);
        System.out.println("total subed weight : " + total_sub_weight);

        return W;
    }

    public static double[] calculateUserTotalWeight(double[][] W) {
        double[] user_total_weight = new double[W.length];
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W.length; j++) {
                user_total_weight[i] += W[i][j];
            }
        }
        return user_total_weight;
    }

    /*public static double[][] friendRecommend(double[][] W, double[] z, int[] diversityUserList) {
        double[] sub_weight = new double[z.length];
        double[] user_weight_sum = new double[z.length];
        double[][] W_01 = new double[z.length][z.length];

        for (int i = 0; i < z.length; i++) {
            for (int j = 0; j < z.length; j++) {
                if (W[i][j] > Constants.W_THRES && i != j) {
                    W_01[i][j] = 1;
                } else {
                    W_01[i][j] = 0;
                }
            }
        }

        double[][] New_FR_Matrix = matrix_util.multiply(W_01, W_01);

        for (int i = 0; i < z.length; i++) {
            for (int j = 0; j < z.length; j++) {
                user_weight_sum[i] += W[i][j];
                if (i == j) {
                    New_FR_Matrix[i][j] = 0;
                }
            }
        }

        int avg_ranking_size = 0;
        double avg_ranking_score = 0.0;
        int delete_num = 0;
        boolean[] echo = new boolean[z.length];
        boolean[] div_usr = new boolean[z.length];
        int div_action_num = 0;

        for (int i = 0; i < z.length; i++) {
            List<AgentRanking> rankings = new ArrayList<>();
            double opinionDifference = 0.0;

            boolean youarediv = false;
            for (int j = 0; j < diversityUserList.length; j++) {
                if (i == diversityUserList[j]) {
                    youarediv = true;
                    break;
                }
            }

            Random random_2 = new Random();
            int randomNumber = random_2.nextInt(101);
            if (youarediv && randomNumber < (int) (100 * Constants.DIV_ACTION_RATE)) {
                int attempts = 0;
                while (attempts < 100) {
                    int new_follow_id = random_2.nextInt(z.length);
                    if (Math.abs(z[i] - z[new_follow_id]) > Constants.DIV_DIFF) {
                        int friend_num = 0;
                        for (int j = 0; j < z.length; j++) {
                            if (W[i][j] > 0) {
                                friend_num++;
                            }
                        }
                        for (int j = 0; j < z.length; j++) {
                            if (W[i][j] > 0) {
                                W[i][j] -= (double) Constants.DIV_NEW_WEIGHT / friend_num;
                                if (W[i][j] < 0) {
                                    W[i][j] = 0;
                                }
                            }
                        }

                        W[i][new_follow_id] += Constants.DIV_NEW_WEIGHT;
                        div_action_num++;

                        div_usr[i] = true;
                        break;
                    }
                    attempts++;
                }
            }

            for (int j = 0; j < z.length; j++) {

                if (W[i][j] > 0) { // iがjをフォローしている場合
                    double weight = W[i][j];
                    opinionDifference = Math.abs(z[i] - z[j]);
                    double score = weight * opinionDifference;

                    if (opinionDifference < Constants.MAX_DIFF) {
                        continue;
                    }

                    rankings.add(new AgentRanking(j, score));
                }
            }
            Random random = new Random();
            int randomNum = random.nextInt(101);
            if (rankings.isEmpty() || randomNum >= Constants.FR_PROB) {
                echo[i] = true;
            }
            rankings.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            avg_ranking_size += rankings.size();

            double temp = 0.0;

            for (AgentRanking ranking : rankings) {
                int agentId = ranking.getAgentId();
                if (temp > user_weight_sum[i] * 0.1) {
                    break;
                }
                temp += W[i][agentId];
                W[i][agentId] = 0;
                sub_weight[i] = temp;
            }
    if (!echo
        

     [i]) {
                AgentRanking ranking = rankings.get(0);
        int agentId = ranking.getAgentId();
        double my_score = ranking.getScore();
        avg_ranking_score += my_score;
        delete_num++;
        temp = W[i][agentId];
        W[i][agentId] = 0;
        sub_weight[i] = temp;
    }

}

System.out.println("the num of users who act div plan : " + div_action_num);
        System.out.println("Avg ranking size: " + (double) avg_ranking_size / z.length);
        System.out.println("Avg ranking score: " + (double) avg_ranking_score / delete_num);
        System.out.println("Not Deleting just adding num" + (z.length - delete_num));

        Random random = new Random(42);
        double Rewire_weight = 0.0;
        int Rewire_num = 0;
        int candidates_num = 0;
        int who_can_num = 0;
        int act_div_num = 0;
        int echo_num = 0;

        for (int i = 0; i < z.length; i++) {
            int newFriend = -1;

            List<Integer> candidates = new ArrayList<>();
            for (int j = 0; j < z.length; j++) {
                if (New_FR_Matrix[i][j] == 1 && j != i) {
                    candidates.add(j);
                }
            }

            if (echo[i] == true) {
                echo_num++;
                newFriend = candidates.get(random.nextInt(candidates.size()));
                candidates_num += candidates.size();
                who_can_num++;
                int friend_num = 0;
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > 0) {
                        friend_num++;
                    }
                }
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > 0) {
                        W[i][j] -= (double) Constants.NEW_WEIGHT / friend_num;
                        if (W[i][j] < 0) {
                            W[i][j] = 0;
                        }
                    }
                }
                W[i][newFriend] += Constants.NEW_WEIGHT;
            } else if (div_usr[i] == true) {
                act_div_num++;
                newFriend = candidates.get(random.nextInt(candidates.size()));
                candidates_num += candidates.size();
                who_can_num++;
                int friend_num = 0;
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > 0) {
                        friend_num++;
                    }
                }
                for (int j = 0; j < z.length; j++) {
                    if (W[i][j] > 0) {
                        W[i][j] -= (double) Constants.NEW_WEIGHT / friend_num;
                        if (W[i][j] < 0) {
                            W[i][j] = 0;
                        }
                    }
                }
                W[i][newFriend] += Constants.NEW_WEIGHT;
            } else if (!candidates.isEmpty()) {//友達の友達がいる。
                newFriend = candidates.get(random.nextInt(candidates.size()));
                candidates_num += candidates.size();
                who_can_num++;
                if (sub_weight[i] > 0) {
                    W[i][newFriend] += sub_weight[i];
                    Rewire_weight += W[i][newFriend];
                    Rewire_num++;
                } else {
                    int friend_num = 0;
                    for (int j = 0; j < z.length; j++) {
                        if (W[i][j] > 0) {
                            friend_num++;
                        }
                    }
                    for (int j = 0; j < z.length; j++) {
                        if (W[i][j] > 0) {
                            W[i][j] -= (double) Constants.NEW_WEIGHT / friend_num;
                            if (W[i][j] < 0) {
                                W[i][j] = 0;
                            }
                        }
                    }
                    W[i][newFriend] += Constants.NEW_WEIGHT;
                }
            } else if (sub_weight[i] > 0) {//友達の友達がいない->友達の友達が自分しかいない、という可能性もある。
                System.out.println("Tomodachi no tomodachi inai !!!");
                newFriend = random.nextInt(z.length);
                W[i][newFriend] += sub_weight[i];
            } else {
                System.out.println("tomodachi ga inai !!!");
            }
        }
        System.out.println("Rewire_num: " + Rewire_num + ", avg_rewire_weight" + Rewire_weight / Rewire_num);
        System.out.println("Avg candidates num: " + (double) candidates_num / who_can_num);
        System.out.println("The num of diversity plan user who follow new agent : " + act_div_num);
        System.out.println("The num of echo : " + echo_num);

        return W;

}*/
    public static class AgentRanking {

        private final int agentId;  // フォローされているエージェントのID
        private final double score; // スコア（重み * 意見の差の絶対値）

        public AgentRanking(int agentId, double score) {
            this.agentId = agentId;
            this.score = score;
        }

        public int getAgentId() {
            return agentId;
        }

        public double getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "AgentID: " + agentId + ", Score: " + score;
        }
    }

    /*public static double[][] friendRecommend(double[][] W, double[] z) {

        int n = W.length; // 隣接行列のサイズ
        Random random = new Random();
        double[][] W1 = matrix_util.copyMatrix(W);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (W1[i][j] > Constants.FR_THRES) {
                    W1[i][j] = 1;
                } else {
                    W1[i][j] = 0;
                }
            }
        }

        // 隣接行列を二乗して友達の友達を計算
        double[][] W2 = matrix_util.multiply(W1, W1);

        int changedlink = 0;
        double avWeight = 0.0;
        int dislike_found = 0;

        // 各ノードの閾値以上の関係性がある隣接ノードのうち、最も意見が遠いノードを選ぶ。
        for (int i = 0; i < n; i++) {
            int dislike = -1;
            double maxDiff = 0;

            // 最小重みのリンクを探索
            for (int j = 0; j < n; j++) {
                if (W[i][j] > 0.0) {
                    double diff = Math.abs(z[i] - z[j]);
                    if (diff > maxDiff) {
                        maxDiff = diff;
                        dislike = j;
                    }
                }
            }

            // 友達の友達からランダムに1つ選ぶ
            if (dislike != -1 && maxDiff > Constants.MAX_DIFF) {//意見の差が0.1以上の友達がいた。
            dislike_found++;
                //System.out.println("Find Dislike !!!!!");
                int newFriend = -1;
                int attempts = 0; // 安全対策でループ回数を制限
                do {
                    newFriend = random.nextInt(n);
                    attempts++;
                    //W2[i][newFriend]が1だったら、そいつは友達の友達だからそいつで決定！。
                } while (attempts < 1000 && (W2[i][newFriend] != 1 || newFriend == i || newFriend == dislike));

                // リンクを付け替える
                if (newFriend != -1 && W2[i][newFriend] == 1) {
                    double swapWeight = W[i][dislike];
                    W[i][dislike] = 0; // 元のリンクを削除
                    W[i][newFriend] += swapWeight; // 新しいリンクを追加
                    changedlink++;
                    avWeight += swapWeight;
                }
            } else {//嫌いになれる（Unfollowしたくなる）人がいなかった->全体から間引く
                int newFriend = -1;
                int attempts = 0; // 安全対策でループ回数を制限
                do {
                    newFriend = random.nextInt(n);
                    attempts++;
                } while (attempts < 1000 && (W2[i][newFriend] != 1 || newFriend == i));//友達の友達でかつ、自分でもなければ->友達の友達じゃない、または、自分だったらTrueで続く。

                if (newFriend != -1) {//見つかった
                    changedlink++;
                    avWeight += Constants.NEW_WEIGHT;
                    int follow_num = 0;
                    for (int k = 0; k < W.length; k++) {
                        if (W[i][k] > 0) {
                            follow_num++;
                        }
                    }
                    if (follow_num > 0) {
                        W[i][newFriend] = Constants.NEW_WEIGHT;
                        double sub_wieght = (double) Constants.NEW_WEIGHT / follow_num;
                        for (int k = 0; k < W.length; k++) {
                            if (W[i][k] > 0 && W[i][k] > sub_wieght) {
                                W[i][k] -= sub_wieght;
                            }
                        }
                    }
                }

            }
        }
        System.out.println("\nthe num of links changed in friendRecommend: " + changedlink);
        System.out.println("the avg of weight changed in friendReccomend: " + avWeight / changedlink);
        System.out.println("The num of agents did unfollow: "+dislike_found);
        return W;
    }*/
}
