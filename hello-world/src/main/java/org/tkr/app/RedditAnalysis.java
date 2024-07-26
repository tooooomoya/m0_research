import org.apache.commons.math3.linear.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RedditAnalysis {
    public static void main(String[] args) {
        int nReddit = 556;
        RealMatrix A = MatrixUtils.createRealMatrix(nReddit, nReddit);
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
                A.addToEntry(u, v, 1.0);
                A.addToEntry(v, u, 1.0);
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
            if (A.getRowVector(i).getL1Norm() == 0) {
                notConnected.add(i);
            }
        }

        int newSize = nReddit - notConnected.size();
        RealMatrix ATrimmed = MatrixUtils.createRealMatrix(newSize, newSize);
        int index = 0;
        for (int i = 0; i < nReddit; i++) {
            if (!notConnected.contains(i)) {
                int innerIndex = 0;
                for (int j = 0; j < nReddit; j++) {
                    if (!notConnected.contains(j)) {
                        ATrimmed.setEntry(index, innerIndex, A.getEntry(i, j));
                        innerIndex++;
                    }
                }
                index++;
            }
        }
        nReddit = newSize;

        // Create z (averaging posts)
        double[] z = new double[nReddit];
        for (int i = 0; i < nReddit; i++) {
            z[i] = zDict.get(i).stream().mapToDouble(val -> val).average().orElse(0.0);
        }

        // Create innate opinions from z (i.e. s = (L + I) * z)
        RealMatrix L = MatrixUtils.createRealMatrix(nReddit, nReddit);
        for (int i = 0; i < nReddit; i++) {
            double sum = ATrimmed.getRowVector(i).getL1Norm();
            L.setEntry(i, i, sum);
        }
        L = L.subtract(ATrimmed);
        RealMatrix I = MatrixUtils.createRealIdentityMatrix(nReddit);
        RealVector zVector = new ArrayRealVector(z);
        RealVector s = L.add(I).operate(zVector);

        // Clamp s values to be within [0, 1]
        for (int i = 0; i < nReddit; i++) {
            double value = s.getEntry(i);
            s.setEntry(i, Math.max(0, Math.min(1, value)));
        }

        // Output s
        System.out.println(Arrays.toString(s.toArray()));
    }
}
