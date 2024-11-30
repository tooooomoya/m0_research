
import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.io.exporter.api.ExportController;
import org.openide.util.Lookup;

import java.io.File;
import main.utils.Constants;

public class NetworkSimulation {

    private double lambda;
    private GraphModel graphModel;
    private Graph graph;
    private ExportController exportController;

    // コンストラクタ
    public NetworkSimulation(double lambda, int nodeCount, double[] initialZ, double[][] initialW) {
        this.lambda = lambda;

        // Gephiプロジェクトの初期化
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        graph = graphModel.getDirectedGraph(); // 有向グラフを使用
        exportController = Lookup.getDefault().lookup(ExportController.class);

        // ノードとエッジを初期化
        initializeGraph(nodeCount, initialZ, initialW);
    }

    // グラフを初期化
    private void initializeGraph(int nodeCount, double[] z, double[][] W) {
        // ノード属性テーブルに "z" 属性を追加
        Column zColumn = graphModel.getNodeTable().getColumn("z");
        if (zColumn == null) {
            graphModel.getNodeTable().addColumn("z", Double.class);
        }
        // ノードを初期化
        for (int i = 0; i < nodeCount; i++) {
            Node node = graphModel.factory().newNode(String.valueOf(i));
            node.setLabel("Node " + i);
            node.setAttribute("z", z[i]);
            graph.addNode(node);
        }

        // エッジを初期化
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W[i].length; j++) {
                if (W[i][j] != 0) {
                    Edge edge = graphModel.factory().newEdge(
                            graph.getNode(String.valueOf(i)),
                            graph.getNode(String.valueOf(j)),
                            true
                    );
                    edge.setWeight(W[i][j]);
                    graph.addEdge(edge);
                }
            }
        }
    }

    // グラフの状態を更新 (外部計算結果を反映)
    public void updateGraph(double[] newZ, double[][] newW) {
        // ノード属性を更新
        for (int i = 0; i < newZ.length; i++) {
            Node node = graph.getNode(String.valueOf(i));
            if (node != null) {
                node.setAttribute("z", newZ[i]);
            }
        }

        for (int i = 0; i < newW.length; i++) {
            for (int j = 0; j < newW.length; j++) {
                if (newW[i][j] < Constants.LINK_THRES) {
                    Edge edge = graph.getEdge(
                            graph.getNode(String.valueOf(i)),
                            graph.getNode(String.valueOf(j))
                    );
                    if (edge != null) {
                        graph.removeEdge(edge); // エッジを削除
                    }
                }
            }
        }

        // エッジの重みを更新
        for (int i = 0; i < newW.length; i++) {
            for (int j = 0; j < newW[i].length; j++) {
                Edge edge = graph.getEdge(
                        graph.getNode(String.valueOf(i)),
                        graph.getNode(String.valueOf(j))
                );

                if (newW[i][j] != 0) {
                    if (edge == null) {
                        edge = graphModel.factory().newEdge(
                                graph.getNode(String.valueOf(i)),
                                graph.getNode(String.valueOf(j)),
                                true
                        );
                        graph.addEdge(edge);
                    }
                    edge.setWeight(newW[i][j]);
                } else if (edge != null) {
                    graph.removeEdge(edge);
                }
            }
        }
    }

    // GEXFファイルとしてエクスポート
    public void exportGraph(int step) {
        try {
            // λごとのディレクトリパスを作成
            String lambdaFolder = "GEXF/lambda_" + lambda;
            File lambdaDir = new File(lambdaFolder);
            if (!lambdaDir.exists()) {
                lambdaDir.mkdirs();  // フォルダがない場合は作成
            }

            // タイムステップごとのファイルパスを作成
            String fileName = lambdaFolder + "/step_" + step + ".gexf";
            File file = new File(fileName);

            // エクスポート処理
            exportController.exportFile(file);
            System.out.println("Graph exported to " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
