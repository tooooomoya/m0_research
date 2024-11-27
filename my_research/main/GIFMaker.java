import java.util.ArrayList;
import java.util.List;

public class GIFMaker {
    // λと度数分布のペアを格納するデータ構造
    public static List<Pair<Double, int[]>> histograms = new ArrayList<>();
    private static final int numBins = 20;

    // 度数分布を記録する関数
    public static void recordHistogram(double lambda, double[] z) {
        int[] bins = new int[numBins];
        double binWidth = 1.0 / numBins;

        // 度数分布を計算
        for (double value : z) {
            int binIndex = (int) Math.min(value / binWidth, numBins - 1);
            bins[binIndex]++;
        }

        // λとヒストグラムをペアで保存
        histograms.add(new Pair<>(lambda, bins));
    }
}

// λとヒストグラムを表すペアクラス
class Pair<K, V> {
    private final K key;
    private final V value;

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
