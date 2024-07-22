import java.io.*;
import java.util.*;
import org.apache.commons.math3.linear.*;

public class RedditAnalysis {
    public static void main(String[] args) {
        int n_reddit = 556;
        RealMatrix A = new Array2DRowRealMatrix(n_reddit, n_reddit);
        Map<Integer, List<Double>> z_dict = new HashMap<>();

        for (int i = 0; i < n_reddit; i++) {
            z_dict.put(i, new ArrayList<>());
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
                z_dict.get(u).add(w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove nodes not connected in graph
        List<Integer> notConnected = new ArrayList<>();
        for (int i = 0; i < n_reddit; i++) {
            if (A.getRowVector(i).getL1Norm() == 0) {
                notConnected.add(i);
            }
        }

        int newSize = n_reddit - notConnected.size();
        RealMatrix A_trimmed = new Array2DRowRealMatrix(newSize, newSize);
        int index = 0;
        for (int i = 0; i < n_reddit; i++) {
            if (!notConnected.contains(i)) {
                int innerIndex = 0;
                for (int j = 0; j < n_reddit; j++) {
                    if (!notConnected.contains(j)) {
                        A_trimmed.setEntry(index, innerIndex, A.getEntry(i, j));
                        innerIndex++;
                    }
                }
                index++;
            }
        }
        n_reddit = newSize;

        // Create z (averaging posts)
        double[] z = new double[n_reddit];
        for (int i = 0; i < n_reddit; i++) {
            z[i] = z_dict.get(i).stream().mapToDouble(val -> val).average().orElse(0.0);
        }

        // Create innate opinions from z (i.e. s = (L + I) * z)
        RealMatrix L = new Array2DRowRealMatrix(n_reddit, n_reddit);
        for (int i = 0; i < n_reddit; i++) {
            double sum = A_trimmed.getRowVector(i).getL1Norm();
            L.setEntry(i, i, sum);
        }
        L = L.subtract(A_trimmed);
        RealMatrix I = MatrixUtils.createRealIdentityMatrix(n_reddit);
        RealMatrix s = L.add(I).operate(new ArrayRealVector(z));

        for (int i = 0; i < n_reddit; i++) {
            s.setEntry(i, Math.max(0, Math.min(1, s.getEntry(i))));
        }

        // Output s
        System.out.println(Arrays.toString(s.getColumn(0)));
    }
}
