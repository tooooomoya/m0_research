import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import main.utils.matrix_util;

public class TestLoad{
    private double[][] A;
    private double[] s;

    public TestLoad(int whichSNS) {
        // whichSNSによってグラフ構造選べるようにとかするか。

        int nSNS = 0;
        String directory = "";
        String name = "";
        if (whichSNS == 2) {
            // choose Random Graph
            nSNS = 15;
            directory = "Random"; 
            name = "random";
        } else if (whichSNS == 3) {
            nSNS = 100;
            directory = "Twitter";//何かのグラフを作るときはここから
            name = "twitter";
        }

        String edgesFilePath = directory + "/edges_" + name + ".txt";
        String opinionFilePath = directory + "/" + name + "_opinion.txt";

        // create Ajacensy matrix "A"
        A = new double[nSNS][nSNS];

        try (BufferedReader br = new BufferedReader(new FileReader(edgesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t"); // split the data by tabs
                if (parts.length >= 2) { // confirm that the split data contains more than 2 elements
                    try {
                        // -1 because the index is ZERO based
                        int u = Integer.parseInt(parts[0].trim()) - 1;
                        int v = Integer.parseInt(parts[1].trim()) - 1;
                        if (u >= 0 && u < nSNS && v >= 0 && v < nSNS) {
                            // 2 sets of nodes indexes in the "edges_SNS.txt" file mean interaction between them
                            A[u][v] += 1;
                            A[v][u] += 1;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Number format exception: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 隣接行列 A をCSVファイルに保存
        String outputCsvPath = "results/adjacency_matrix.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvPath))) {
            for (int i = 0; i < nSNS; i++) {
                for (int j = 0; j < nSNS; j++) {
                    writer.write(A[i][j] + ((j < nSNS - 1) ? "," : ""));  // カンマで区切る
                }
                writer.newLine();  // 行の終わりに改行を追加
            }
            System.out.println("Adjacency matrix saved to " + outputCsvPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create "zDict"
        // Use "Map" Structure(contains opinon value z): key -> int, value -> list of
        // double
        Map<Integer, List<Double>> zDict = new HashMap<>();
        for (int i = 0; i < nSNS; i++) {
            // put `nSNS` vacant entries to zDict (initialization)
            zDict.put(i, new ArrayList<>());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(opinionFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                int u = Integer.parseInt(parts[0]) - 1; // draw node index
                double w = Double.parseDouble((parts[2]));// draw the node's opinion
                zDict.get(u).add(w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create "notConnected Matrix" in which we can know which node not being
        // connected with any other nodes.
        Set<Integer> notConnected = new HashSet<>();
        for (int i = 0; i < nSNS; i++) {
            boolean connected = false;
            for (int j = 0; j < nSNS; j++) {
                if (A[i][j] > 0) {
                    connected = true;
                    break;
                }
            }
            if (!connected) {
                notConnected.add(i);
            }
        }

        // Create z
        double[] z = new double[nSNS];
        for (int i = 0; i < nSNS; i++) {
            List<Double> opinions = zDict.get(i);
            double sum = 0;
            for (Double opinion : opinions) {
                sum += opinion;
            }
            //投稿のopinion値の平均を取ってZ行列に入れる。
            z[i] = sum / opinions.size();
            
            /*if (i % 10 == 0) {
                System.out.printf("%d opinion: %.2f\n", i, z[i]); // %.2f は小数点以下2桁まで表示するフォーマット
            }*/
        }

        double[][] L = matrix_util.createL(A, nSNS);
        double[][] I = matrix_util.createIdentityMatrix(nSNS);
        double[][] LPlusI = matrix_util.add(L, I);
        // "s" is intrinsic opinion(個人に潜在的で本質的な不変のopinion value)
        System.out.println("the innate z: ");
        matrix_util.printVector(z);
        s = matrix_util.multiplyMatrixVector(LPlusI, z);
        System.out.println("the intrinsic z before clip: ");
        matrix_util.printVector(s);
        // clipping to the scale of max 1, min 0
        for (int i = 0; i < s.length; i++) {
            s[i] = Math.min(Math.max(s[i], 0), 1);
        }
        System.out.println("\nthe intrinsic z after clip: ");
        matrix_util.printVector(s);

        /*int a = 0, b = 0, c = 0, d = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i] < 0.25) {
                a++;
            } else if (s[i] < 0.5) {
                b++;
            } else if (s[i] < 0.75) {
                c++;
            } else {
                d++;
            }
        }
        System.out.println("Confirm the distribution of intinsic opinions ↓↓↓");
        System.out.printf("0 ~ 0.25: %d\n", a);
        System.out.printf("0.25 ~ 0.5: %d\n", b);
        System.out.printf("0.5 ~ 0.75: %d\n", c);
        System.out.printf("0.75 ~ 1.0: %d\n", d);
        */

    }
    public double[][] getAdjacencyMatrix(){
        return A;
    }
    public double[] getIntrinsicOpinions(){
        return s;
    }
    public static void main(String[] args) {
        int whichSNS = Integer.parseInt(args[0].trim());
        LoadNW loadNW = new LoadNW(whichSNS);
        // mainメソッド内では値を返さない
    }

}
