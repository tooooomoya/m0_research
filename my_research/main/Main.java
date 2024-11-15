import java.util.Scanner;
import main.structure.*;

public class Main {
    public static void main(String[] args) {
        // Step 1: Load Network
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the social network type (0 for Reddit, 1 for Twitter, 2 for Test): ");
        int whichSNS = scanner.nextInt();

        System.out.println("Wanna apply random method? (true, false) : ");
        boolean random = scanner.nextBoolean();

        double[][] A = null;
        double[] s = null;

        if (whichSNS != 2) {
            LoadNW loadNW = new LoadNW(whichSNS);
            A = loadNW.getAdjacencyMatrix(); // Aは隣接重み行列
            s = loadNW.getIntrinsicOpinions(); // インスタンスメソッドの呼び出し
            System.out.println("Load NW finished");
        } else {
            System.out.println("You chose Test Matrix.");
            TestLoad loadTest = new TestLoad(whichSNS);
            A = loadTest.getAdjacencyMatrix();
            s = loadTest.getIntrinsicOpinions();
        }

        double[] lamvals = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };
        //double[] lamvals = { 0.1, 0.2, 0.3, 0.4, 0.5 };

        RunSimulate runSimulate = new RunSimulate(A, s);
        ResultPair resultPair = runSimulate.runDynamics(lamvals, random);
        System.out.println("RunSimulate finished");

        // Step 3: Plot Results
        PlotResults plotResults = new PlotResults();
        if (whichSNS == 0) {
            plotResults.exportPls(resultPair, "Reddit", lamvals);
            plotResults.exportDisagg(resultPair, "Reddit", lamvals);
            plotResults.exportGppls(resultPair, "Reddit", lamvals);
            plotResults.exportStfs(resultPair, "Reddit", lamvals);
            plotResults.exportDvs(resultPair, "Reddit", lamvals);
        } else if (whichSNS == 1) {
            plotResults.exportPls(resultPair, "Twitter", lamvals);
            plotResults.exportDisagg(resultPair, "Twitter", lamvals);
            plotResults.exportGppls(resultPair, "Twitter", lamvals);
            plotResults.exportStfs(resultPair, "Twitter", lamvals);
            plotResults.exportDvs(resultPair, "Twitter", lamvals);
        } else if (whichSNS == 2) {
            plotResults.exportPls(resultPair, "Test", lamvals);
            plotResults.exportDisagg(resultPair, "Test", lamvals);
            plotResults.exportGppls(resultPair, "Test", lamvals);
            plotResults.exportStfs(resultPair, "Test", lamvals);
            plotResults.exportDvs(resultPair, "Test", lamvals);
        }

        scanner.close();
    }
}
