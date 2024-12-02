import java.util.ArrayList;
import java.util.List;

public class GIFMaker {

    // λと度数分布を格納するデータ構造
    public static List<Pair<Double, int[]>> lambdasWithHistogram = new ArrayList<>();

    // z[]を記録する関数
    public static void recordHistogram(double lambda, double[] z) {
        int numBins = 20;
        int[] bins = new int[numBins];
        double binWidth = 1.0 / numBins;

        // 度数分布を計算
        for (double value : z) {
            int binIndex = (int) Math.min(value / binWidth, numBins - 1);
            bins[binIndex]++;
        }

        // λと度数分布をペアで保存
        lambdasWithHistogram.add(new Pair<>(lambda, bins));
    }
}

// λと度数分布を表すペアクラス
class Pair<K, V> {

    private final K key;  // λ
    private final V value;  // 度数分布

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
