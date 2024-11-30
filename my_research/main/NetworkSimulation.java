import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.io.exporter.api.ExportController;
import org.openide.util.Lookup;

import java.io.File;

public class NetworkSimulation {
    private double lambda;
    private int step;
    private GraphModel graphModel;
    private Graph graph;
    private ExportController exportController;
    private double[] z;       // ノード属性
    private double[][] W;     // エッジ重み行列

    // コンストラクタ
    public NetworkSimulation(double lambda, int nodeCount) {
        this.lambda = lambda;
        this.step = 0;

        // Gephiプロジェクトの初期化
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        graph = graphModel.getDirectedGraph(); // 有向グラフを使用
        exportController = Lookup.getDefault().lookup(ExportController.class);

        // ノードとエッジを初期化
        initializeGraph(nodeCount);
    }

    // グラフを初期化
    private void initializeGraph(int nodeCount) {
        // ノード属性 z を初期化
        z = new double[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            z[i] = Math.random(); // ランダム初期化（必要に応じて変更）

            // ノードを作成
            Node node = graphModel.factory().newNode(String.valueOf(i));
            node.setLabel("Node " + i);
            node.setAttribute("z", z[i]);
            graph.addNode(node);
        }

        // エッジ重み行列 W を初期化
        W = new double[nodeCount][nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (i != j) {
                    W[i][j] = Math.random() * lambda; // λを反映した重み
                }
            }
        }
    }

    // 1ステップ分のシミュレーションを実行
    public void runStep() {
        // ノード属性を更新
        for (int i = 0; i < z.length; i++) {
            z[i] += lambda * 0.01; // λを反映して属性値を更新
            Node node = graph.getNode(String.valueOf(i));
            if (node != null) {
                node.setAttribute("z", z[i]);
            }
        }

        // エッジの重みを更新
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W[i].length; j++) {
                if (W[i][j] != 0) {
                    W[i][j] += lambda * 0.01; // 重みの更新ロジック
                    Edge edge = graph.getEdge(graph.getNode(String.valueOf(i)), graph.getNode(String.valueOf(j)));
                    if (edge == null) {
                        edge = graphModel.factory().newEdge(graph.getNode(String.valueOf(i)), graph.getNode(String.valueOf(j)), true);
                        graph.addEdge(edge);
                    }
                    edge.setWeight(W[i][j]);
                }
            }
        }

        step++;
    }

    // GEXFファイルとしてエクスポート
    public void exportGraph() {
        try {
            String fileName = "lambda_" + lambda + "_step_" + step + ".gexf";
            File file = new File(fileName);
            exportController.exportFile(file);
            System.out.println("Graph exported to " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
