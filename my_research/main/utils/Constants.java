package main.utils;

public class Constants {
    // 定数の宣言の例
    public static final String APP_NAME = "My Application";
    public static final int MAX_RETRIES = 5;
    public static final double PI = 3.14159;
    public static final String FILE_PATH = "/path/to/file";

    //wの値で閾値以上なら関係性がある、と判断する
    public static final double W_THRES = 0.1;

    //リンクのランダム追加アルゴリズムの定数
    public static final double ALPHA = 1.0;
    public static final double ADD_WEIGHT = 0.5;
    
    // コンストラクタをprivateにしてインスタンス化を防ぐ
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
