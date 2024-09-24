import com.gurobi.gurobi.*;

public class GurobiExample {
    public static void main(String[] args) {
        try {
            // Gurobi環境の作成
            GRBEnv env = new GRBEnv("gurobi.log");
            GRBModel model = new GRBModel(env);

            // 変数の追加
            GRBVar x = model.addVar(0.0, 1.0, 1.0, GRB.BINARY, "x");

            // 目的関数の設定
            GRBLinExpr objective = new GRBLinExpr();
            objective.addTerm(1.0, x);  // xに対する係数を追加
            model.setObjective(objective, GRB.MAXIMIZE);

            // 制約の追加
            model.addConstr(x, GRB.LESS_EQUAL, 1.0, "c0");

            // 最適化の実行
            model.optimize();

            // 解の表示
            System.out.println("Optimal Solution: x = " + x.get(GRB.DoubleAttr.X));

            // モデルの解放
            model.dispose();
            env.dispose();
        } catch (GRBException e) {
            System.out.println("Gurobiエラー: " + e.getMessage());
        }
    }
}
