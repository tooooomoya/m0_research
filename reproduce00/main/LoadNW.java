import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import utils.matrix_util;

public class LoadNW {
    public static void main(String[] args){
            int whichSNS = Integer.parseInt(args[0].trim());
            //whichSNS = 0 -> Reddit
            //whichSNS = 1 -> Twitter
            int nSNS = 0;
            String directory = "";
            String name = "";
            if(whichSNS==0){
                nSNS = 556;
                directory = "Reddit";
                name = "reddit";
            }else if(whichSNS==1){
                nSNS = 548;
                directory = "Twitter";
                name = "twitter";
            }

            String edgesFilePath = directory + "/edges_" + name +".txt";
            String opinionFilePath = directory + "/" + name + "_opinion.txt";

            double[][] A = new double[nSNS][nSNS];
            Map<Integer, List<Double>> zDict = new HashMap<>();
            for(int i=0; i < nSNS; i++){
                zDict.put(i, new ArrayList<>());
            }

            try (BufferedReader br = new BufferedReader(new FileReader(edgesFilePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\t"); // 正しいタブ文字のエスケープ
                    if (parts.length >= 2) { // 分割結果が2つ以上の要素を持つことを確認
                        try {
                            int u = Integer.parseInt(parts[0].trim()) - 1;
                            int v = Integer.parseInt(parts[1].trim()) - 1;
                            if (u >= 0 && u < nSNS && v >= 0 && v < nSNS) {
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
            

            try(BufferedReader br = new BufferedReader(new FileReader(opinionFilePath))){
                String line;
                while((line = br.readLine())!=null){
                    String[] parts = line.split("\t");
                    int u = Integer.parseInt(parts[0]) -1;
                    double w = Double.parseDouble((parts[1]));
                    zDict.get(u).add(w);
                }
            }catch(IOException e){
                e.printStackTrace();
            }

            // remove nodes not connected in the graph
            Set<Integer> notConnected = new HashSet<>();
            for(int i=0; i < nSNS; i++){
                boolean connected = false;
                for(int j = 0; j < nSNS; j++){
                    if(A[i][j] > 0){
                        connected = true;
                        break;
                    }
                }
                if(!connected){
                    notConnected.add(i);
                }
            }


            //Create z
            double[] z = new double[nSNS];
            for(int i=0; i < nSNS; i++){
                List<Double> opinions = zDict.get(i);
                double sum = 0;
                for(Double opinion : opinions){
                    sum += opinion;
                }
                z[i] = sum / opinions.size();
            }


            double[][] L = matrix_util.createL(A, nSNS);
            double[][] I = matrix_util.createIdentityMatrix(nSNS);
            double[][] LPlusI = matrix_util.add(L, I);
            matrix_util.printMatrix(LPlusI);
    }
}
