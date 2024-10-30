import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Step 1: Load Network
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the social network type (0 for Reddit, 1 for Twitter, 2 for Test): ");
        int whichSNS = scanner.nextInt();

        double[][] A=null;
        double[] s=null;

        if(whichSNS != 2){
        LoadNW loadNW = new LoadNW(whichSNS);
        A = loadNW.getAdjacencyMatrix(); // Aは隣接重み行列
        s = loadNW.getIntrinsicOpinions(); // インスタンスメソッドの呼び出し
        System.out.println("Load NW finished");
        }
        else{
            System.out.println("You chose Test Matrix.");
            TestLoad loadTest = new TestLoad(whichSNS);
            A = loadTest.getAdjacencyMatrix();
            s = loadTest.getIntrinsicOpinions();
        }
        
        double[] lamvals = {0.2};

        RunSimulate runSimulate = new RunSimulate(A, s);
        ResultPair resultPair = runSimulate.runDynamics(lamvals);
        System.out.println("RunSimulate finished");

        // Step 3: Plot Results
        PlotResults plotResults = new PlotResults();
        if (whichSNS == 0) {
            plotResults.exportPls(resultPair, "Reddit", lamvals);
            plotResults.exportDisagg(resultPair, "Reddit", lamvals);
        } else if(whichSNS == 1) {
            plotResults.exportPls(resultPair, "Twitter", lamvals);
            plotResults.exportDisagg(resultPair, "Twitter", lamvals);
        } else if(whichSNS == 2) {
            plotResults.exportPls(resultPair, "Test", lamvals);
            plotResults.exportDisagg(resultPair, "Test", lamvals);
        } 

        scanner.close();
    }
}
