
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.structure.*;
import main.utils.Constants;

public class RunSimulate {

    private double[][] A;
    private double[] s;

    public RunSimulate(double[][] A, double[] s) {
        this.A = A;
        this.s = s;
    }

    public ResultPair runDynamics(double[] lamList, boolean random, int whichSNS) {
        // because of time issues, we run the NA dynamics separately 
        HashMap<Double, Result> rd = new HashMap<>();       // results of NA dynamics
        HashMap<Double, Result> rdFix = new HashMap<>();    // results of regularized NA dynamics

        int maxIter = Constants.MAT_ITERATION;
        double gam = 0.0; // L2 regularization coefficient 
        ArrayList<Double> ErrorLambda = new ArrayList<>();
        double[] AddedWeight = new double[lamList.length];

        Map<Integer, List<Integer>> communities = null;
        if (whichSNS == 0) {
            communities = loadCommunitiesFromFile("community/Redditcommunity_results.csv");
        } else if (whichSNS == 1) {
            communities = loadCommunitiesFromFile("community/Twittercommunity_results.csv");
        } else {
            communities = loadCommunitiesFromFile("community/Testcommunity_results.csv");
        }

        for (int i = 0; i < lamList.length; i++) {
            System.out.println("\n---------------Start the Experiment with lambda:" + lamList[i]);

            /*System.out.println(("\nno fix"));
            //System.out.println("lam:" + lamList[i]);
            Result resultNoFix = AdminGame.am(A, s, lamList[i], false, 0, maxIter, false, random, communities);
            // no fix -> gamma = 0
            rd.put(lamList[i], resultNoFix);
            
            if(resultNoFix.getFindError()){
                ErrorLambda.add(lamList[i]);
            }
            
            AddedWeight[i] = resultNoFix.getWeightadded();
             */
            System.out.println("with fix");
            System.out.println("lam:" + lamList[i]);
            Result resultFix = AdminGame.am(A, s, lamList[i], true, gam, maxIter, false, random, communities);
            rdFix.put(lamList[i], resultFix);
            if (resultFix.getFindError()) {
                ErrorLambda.add(lamList[i]);
            }
            AddedWeight[i] = resultFix.getWeightadded();

        }

        System.out.println("\nGurobi Error Report\n");
        for (double lambda : ErrorLambda) {
            System.out.println("error reported lambda : " + lambda);
        }
        System.out.println("\nCost(total weight added to the graph)");
        for (int a = 0; a < AddedWeight.length; a++) {
            System.out.println("lambda : " + a + ", added weight : " + AddedWeight[a]);
        }

        ResultPair resultPair = new ResultPair(rd, rdFix);

        return resultPair;
    }

    // 各ノードの所属コミュニティ情報をファイルから読み込むメソッド
    public static Map<Integer, List<Integer>> loadCommunitiesFromFile(String filename) {
        Map<Integer, List<Integer>> communities = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // ヘッダー行を読み飛ばす

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue; // 不正な行はスキップ
                }
                int node = Integer.parseInt(parts[0].trim());
                int community = Integer.parseInt(parts[1].trim());

                // コミュニティごとにノードを追加
                communities.computeIfAbsent(community, k -> new ArrayList<>()).add(node);
            }

        } catch (IOException e) {
            System.err.println("Error reading communities from file: " + e.getMessage());
        }

        // 各コミュニティのノード数を出力
        for (Map.Entry<Integer, List<Integer>> entry : communities.entrySet()) {
            int communityId = entry.getKey();
            int nodeCount = entry.getValue().size();
            System.out.println("Community " + communityId + ": " + nodeCount + " nodes");
        }

        return communities;
    }
}
