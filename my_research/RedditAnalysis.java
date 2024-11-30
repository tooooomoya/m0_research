import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RedditAnalysis {
    public static void main(String[] args) {
        int nReddit = 556;
        double[][] A = new double[nReddit][nReddit];
        Map<Integer, List<Double>> zDict = new HashMap<>();
        
        // Initialize zDict
        for (int i = 0; i < nReddit; i++) {
            zDict.put(i, new ArrayList<>());
        }

        // Load adjacency matrix
        try (BufferedReader br = new BufferedReader(new FileReader("Reddit/edges_reddit.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                int u = Integer.parseInt(parts[0]) - 1;
                int v = Integer.parseInt(parts[1]) - 1;
                A[u][v]++;
                A[v][u]++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load opinions
        try (BufferedReader br = new BufferedReader(new FileReader("Reddit/reddit_opinion.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                int u = Integer.parseInt(parts[0]) - 1;
                double w = Double.parseDouble(parts[2]);
                zDict.get(u).add(w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove nodes not connected in graph
        List<Integer> notConnected = new ArrayList<>();
        for (int i = 0; i < nReddit; i++) {
            boolean isConnected = false;
            for (int j = 0; j < nReddit; j++) {
                if (A[i][j] > 0) {
                    isConnected = true;
                    break;
                }
            }
            if (!isConnected) {
                notConnected.add(i);
            }
        }

        // Create a new adjacency matrix with disconnected nodes removed
        double[][] ATrimmed = new double[nReddit - notConnected.size()][nReddit - notConnected.size()];
        Map<Integer, Integer> indexMap = new HashMap<>();
        int newIndex = 0;
        for (int i = 0; i < nReddit; i++) {
            if (!notConnected.contains(i)) {
                indexMap.put(i, newIndex++);
            }
        }

        for (int i = 0; i < nReddit; i++) {
            if (!notConnected.contains(i)) {
                int newI = indexMap.get(i);
                for (int j = 0; j < nReddit; j++) {
                    if (!notConnected.contains(j)) {
                        int newJ = indexMap.get(j);
                        ATrimmed[newI][newJ] = A[i][j];
                    }
                }
            }
        }

        nReddit = ATrimmed.length;

        // Create z (averaging posts)
        double[] z = new double[nReddit];
        for (int i = 0; i < nReddit; i++) {
            List<Double> opinions = zDict.get(i);
            z[i] = opinions.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        }

        // Create innate opinions from z (i.e. s = (L + I) * z)
        double[][] L = new double[nReddit][nReddit];
        for (int i = 0; i < nReddit; i++) {
            double sum = Arrays.stream(ATrimmed[i]).sum();
            L[i][i] = sum;
        }

        // L = L - ATrimmed
        for (int i = 0; i < nReddit; i++) {
            for (int j = 0; j < nReddit; j++) {
                L[i][j] -= ATrimmed[i][j];
            }
        }

        // Add identity matrix I
        for (int i = 0; i < nReddit; i++) {
            L[i][i] += 1.0;
        }

        // Multiply (L + I) * z
        double[] s = new double[nReddit];
        for (int i = 0; i < nReddit; i++) {
            s[i] = 0;
            for (int j = 0; j < nReddit; j++) {
                s[i] += L[i][j] * z[j];
            }
            s[i] = Math.max(0, Math.min(1, s[i]));
        }

        // Output s
        System.out.println(Arrays.toString(s));
    }
}
