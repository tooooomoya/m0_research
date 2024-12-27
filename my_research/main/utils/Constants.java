package main.utils;

public class Constants {
    public static final String directory = "Random";

    //wの値で閾値以上なら関係性がある、と判断する
    public static final double W_THRES = 0.01;
    public static final int MAT_ITERATION = 100;
    //public static final double[] LAMBDA_ARRAY = { 0.0, 0.03, 0.07, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };
    //public static final double[] LAMBDA_ARRAY = {0.0, 0.01, 0.02, 0.05, 0.08, 0.1, 0.2};
    //public static final double[] LAMBDA_ARRAY = {0.01, 0.02, 0.03};
    public static final double[] LAMBDA_ARRAY = {0.02};

    //FriendRecommendRewiringの紹介できる友達の重み閾値（ゆるいつながりの友達は紹介しない。本当に仲良い友達）
    public static final double FR_THRES = 0.01;
    public static final double MAX_DIFF = 0.05; //これより小さく意見が近いのに、縁を切る必要性はない。
    public static final double NEW_WEIGHT = 0.2;
    public static final int NEW_USER_NUM = 10;
    public static final int FR_PROB = 50;
    public static final double DIV_RATE = 0.0;
    public static final double DIV_ACTION_RATE = 0.2;
    public static final double DIV_DIFF = 0.2;

    //リンクのランダム追加アルゴリズムの定数
    public static final double ALPHA = 0.5;//ユーザ数の何倍か
    public static final double ADD_WEIGHT = 0.1;

    //グラフ構造描画の定数
    public static final double LINK_THRES = 0.01;

    // コンストラクタをprivateにしてインスタンス化を防ぐ
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
