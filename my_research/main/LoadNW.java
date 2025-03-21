
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class LoadNW {

    private double[][] A;
    private double[] s;

    public LoadNW(int whichSNS) {
        // whichSNS = 0 -> Reddit
        // whichSNS = 1 -> Twitter
        int nSNS = 0;
        String directory = "";
        String name = "";
        if (whichSNS == 0) {
            nSNS = 556;
            directory = "Reddit";
            name = "reddit";
        } else if (whichSNS == 1) {
            nSNS = 548;
            directory = "Twitter";
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
                        if (u >= 0 && u < nSNS && v >= 0 && v < nSNS && u != v) {
                            // 2 sets of nodes indexes in the "edges_SNS.txt" file mean interaction between
                            // them
                            A[u][v] += 1.0;
                            
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
                    writer.write(A[i][j] + ((j < nSNS - 1) ? "," : "")); // カンマで区切る
                }
                writer.newLine(); // 行の終わりに改行を追加
            }
            System.out.println("Adjacency matrix saved to " + outputCsvPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[] z = new double[nSNS];
        try (BufferedReader br = new BufferedReader(new FileReader(opinionFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                int u = Integer.parseInt(parts[0]) - 1; // draw node index
                double w = Double.parseDouble((parts[2]));// draw the node's opinion
                z[u] = w;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // スケーリング範囲
        double minTarget = 0.0;
        double maxTarget = 1.0;

        // 元データの最小値と最大値を計算
        double minZ = Arrays.stream(z).min().getAsDouble();
        double maxZ = Arrays.stream(z).max().getAsDouble();

        // min-maxスケーリングを適用
        for (int i = 0; i < z.length; i++) {
            z[i] = minTarget + (z[i] - minZ) / (maxZ - minZ) * (maxTarget - minTarget);
        }

        s = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            s[i] = z[i];
        }

        //System.out.println("\nthe intrinsic s (calculate the situation before FJ model): ");
        //matrix_util.printVector(s);

        int a = 0, b = 0, c = 0, d = 0, e = 0;
        for (int t = 0; t < z.length; t++) {
            if (0 <= z[t] && z[t] < 0.2) {
                a++;
            } else if (z[t] < 0.4) {
                b++;
            } else if (z[t] < 0.6) {
                c++;
            } else if (z[t] < 0.8) {
                d++;
            } else if (z[t] <= 1.0) {
                e++;
            }
        }

        System.out.println("Confirm the distribution of z (opinions) ↓↓↓");
        System.out.printf("0 ~ 0.2: %d\n", a);
        System.out.printf("0.2 ~ 0.4: %d\n", b);
        System.out.printf("0.4 ~ 0.6: %d\n", c);
        System.out.printf("0.6 ~ 0.8: %d\n", d);
        System.out.printf("0.8 ~ 1.0: %d\n", e);

    }

    public double[][] getAdjacencyMatrix() {
        return A;
    }

    public double[] getIntrinsicOpinions() {
        return s;
    }

    public static double[][] removeRowAndColumn(double[][] matrix, int i) {
        int n = matrix.length;

        // Check for valid index and square matrix
        if (i < 0 || i >= n || matrix[0].length != n) {
            throw new IllegalArgumentException("Invalid index or non-square matrix.");
        }

        double[][] newMatrix = new double[n - 1][n - 1];

        for (int row = 0, newRow = 0; row < n; row++) {
            if (row == i) {
                continue; // Skip the specified row
            }
            for (int col = 0, newCol = 0; col < n; col++) {
                if (col == i) {
                    continue; // Skip the specified column
                }
                newMatrix[newRow][newCol] = matrix[row][col];
                newCol++;
            }
            newRow++;
        }

        return newMatrix;
    }

    public static void main(String[] args) {
        int whichSNS = Integer.parseInt(args[0].trim());
        LoadNW loadNW = new LoadNW(whichSNS);
        // mainメソッド内では値を返さない
    }

}
