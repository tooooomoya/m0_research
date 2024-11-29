
import java.util.ArrayList;
import java.util.List;
import main.utils.Constants;

public class GIFMaker {

    // λ、度数分布、隣接行列を格納するデータ構造
    public static List<Triple<Double, double[], double[][]>> histogramsWithAdjacency = new ArrayList<>();
    private static final int numBins = 20;

    // 度数分布と隣接行列を記録する関数
    public static void recordHistogram(double lambda, double[] z, double[][] adjacencyMatrix) {

        for (int i = 0; i < z.length; i++) {
            for (int j = 0; j < z.length; j++) {
                if (adjacencyMatrix[i][j] < Constants.LINK_THRES) {
                    adjacencyMatrix[i][j] = 0;
                }
            }
        }

        // λ、ヒストグラム、隣接行列をペアで保存
        histogramsWithAdjacency.add(new Triple<>(lambda, z, adjacencyMatrix));
    }
}

// λ、ヒストグラム、隣接行列を表すトリプルクラス
class Triple<K, V, T> {

    private final K key;
    private final V value1;
    private final T value2;

    public Triple(K key, V value1, T value2) {
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
    }

    public K getKey() {
        return key;
    }

    public V getValue1() {
        return value1;
    }

    public T getValue2() {
        return value2;
    }
}
