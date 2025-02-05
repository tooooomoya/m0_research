
import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.io.exporter.api.ExportController;
import org.openide.util.Lookup;

import java.io.File;
import main.utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // ノード属性テーブルに "community" 属性を追加
        Column communityColumn = graphModel.getNodeTable().getColumn("community");
        if (communityColumn == null) {
            graphModel.getNodeTable().addColumn("community", Integer.class);
        }

        Column divColumn = graphModel.getNodeTable().getColumn("div_user");
        if (divColumn == null) {
            graphModel.getNodeTable().addColumn("div_user", Integer.class);
        }

        // ノードを初期化
        for (int i = 0; i < nodeCount; i++) {
            Node node = graphModel.factory().newNode(String.valueOf(i));
            node.setLabel("Node " + i);
            node.setAttribute("z", z[i]);
            node.setAttribute("community", -1); // 初期状態ではコミュニティ未定義 (-1)
            node.setAttribute("div_user", 0);
            graph.addNode(node);
        }

        // エッジを初期化
        for (int i = 0; i < W.length; i++) {
            for (int j = 0; j < W[i].length; j++) {
                if (W[i][j] > 0) {
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

    // ノードにコミュニティ情報を付与
    public void assignCommunities(Map<Integer, List<Integer>> communityGroups) {
        // ノード属性テーブルに "community" 属性を追加
        Column communityColumn = graphModel.getNodeTable().getColumn("community");
        if (communityColumn == null) {
            graphModel.getNodeTable().addColumn("community", Integer.class);
        }

        // 各ノードにコミュニティ情報を設定
        for (Map.Entry<Integer, List<Integer>> entry : communityGroups.entrySet()) {
            int communityId = entry.getKey();
            List<Integer> nodes = entry.getValue();

            for (int nodeId : nodes) {
                Node node = graph.getNode(String.valueOf(nodeId));
                if (node != null) {
                    node.setAttribute("community", communityId);
                }
            }
        }
    }

    public void assignDivUser(boolean[] isDiversityUser) {

        Column divColumn = graphModel.getNodeTable().getColumn("div_user");
        if (divColumn == null) {
            graphModel.getNodeTable().addColumn("div_user", Integer.class);
        }

        
        for(int i = 0; i < isDiversityUser.length; i++){
            if(isDiversityUser[i]){
                Node node = graph.getNode(String.valueOf(i));
                if (node != null) {
                    node.setAttribute("div_user", 1);
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

        // エッジの更新
        for (int i = 0; i < newW.length; i++) {
            for (int j = 0; j < newW[i].length; j++) {
                // 現在のエッジを取得
                Edge edge = graph.getEdge(
                        graph.getNode(String.valueOf(i)),
                        graph.getNode(String.valueOf(j))
                );

                if (newW[i][j] < Constants.LINK_THRES) {
                    // 新しい重みがLINK_THRES未満の場合、エッジを削除
                    if (edge != null) {
                        graph.removeEdge(edge);
                    }
                } else {
                    // LINK_THRES以上の場合、エッジの重みを設定
                    if (edge != null) {
                        edge.setWeight(newW[i][j]); // 既存エッジの重みを更新
                    } else {
                        // エッジが存在しない場合は新しく追加
                        edge = graphModel.factory().newEdge(
                                graph.getNode(String.valueOf(i)),
                                graph.getNode(String.valueOf(j)),
                                true
                        );
                        edge.setWeight(newW[i][j]); // 重みを設定
                        graph.addEdge(edge);
                    }
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
