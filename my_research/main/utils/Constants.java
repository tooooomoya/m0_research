package main.utils;

public class Constants {

    //wの値で閾値以上なら関係性がある、と判断する
    public static final double W_THRES = 0.01;
    public static final int MAT_ITERATION = 20;
    //public static final double[] LAMBDA_ARRAY = { 0.0, 0.03, 0.07, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };
    public static final double[] LAMBDA_ARRAY = {0.0, 0.03, 0.07, 0.1, 0.2, 0.3, 0.4, 0.5};
    //public static final double[] LAMBDA_ARRAY = {0.01, 0.02, 0.03};
    //public static final double[] LAMBDA_ARRAY = {0.2};

    //FriendRecommendRewiringの紹介できる友達の重み閾値（ゆるいつながりの友達は紹介しない。本当に仲良い友達）
    public static final double FR_THRES = 0.01;
    public static final double MAX_DIFF = 0.1; //これより小さく意見が近いのに、縁を切る必要性はない。

    //リンクのランダム追加アルゴリズムの定数
    public static final double ALPHA = 2.0;//ユーザ数の何倍か
    public static final double ADD_WEIGHT = 0.2;
    //(1,0.5)D0.10若干弱い。 (2, 0.2)の方がいい。
    //(2, 0.5)だと強い。D0.06
    //(10,0.1)も強い。
    //(5, 0.2)は悪くない。Dは依然小さい。若干強いか、
    //(1, 0.5)も悪くない。もうちょい強くていい。
    //(5, 0.1)は悪くないが、二峰化してない。
    //(3, 0.2)は悪くないが、二峰化とまではいかない。D0.12
    //この辺は全て二峰化まではいかないから、それはいいとして、Dが大きくなるものを探そう。
    //(5, 0.2)(3, 0.3)はD0.08
    //(4, 0.2)はD0.098
    //(2, 0.2)は0.13で極端な意見も死んでいない。
    //(2, 0.2)(3, 0.2)がベストというところか。

    //グラフ構造描画の定数
    public static final double LINK_THRES = 0.1;

    // コンストラクタをprivateにしてインスタンス化を防ぐ
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
