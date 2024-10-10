import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Step 1: Load Network
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the social network type (0 for Reddit, 1 for Twitter): ");
        int whichSNS = scanner.nextInt();

        LoadNW loadNW = new LoadNW(whichSNS);
        double[][] A = loadNW.getAdjacencyMatrix(); // Aは隣接重み行列
        double[] s = loadNW.getIntrinsicOpinions(); // インスタンスメソッドの呼び出し
        System.out.println("Load NW finished");

        RunSimulate runSimulate = new RunSimulate(A, s);
        ResultPair resultPair = runSimulate.runDynamics();
        System.out.println("RunSimulate finished");

        // Step 3: Plot Results
        PlotResults plotResults = new PlotResults();
        if (whichSNS == 0) {
            plotResults.exportPls(resultPair, "Reddit");
            plotResults.exportDisagg(resultPair, "Reddit");
        } else {
            plotResults.exportPls(resultPair, "Twitter");
            plotResults.exportDisagg(resultPair, "Twitter");
        }

        scanner.close();
    }
}
