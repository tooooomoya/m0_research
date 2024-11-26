import pandas as pd
import networkx as nx
import community as community_louvain


# CSVファイルから隣接行列を読み込む
def load_adjacency_matrix(filename):
    return pd.read_csv(filename, index_col=0).values

# 隣接行列をNetworkXグラフに変換
def adjacency_matrix_to_graph(matrix):
    G = nx.Graph()
    for i in range(len(matrix)):
        for j in range(len(matrix[i])):
            if matrix[i][j] > 0:  # 0以外のエッジを追加
                G.add_edge(i, j, weight=matrix[i][j])
    return G

# コミュニティ検出を実行し、各ノードのコミュニティ情報を取得
def detect_communities(G):
    # Louvain法を使用してコミュニティを検出
    print('Start calculate')
    return community_louvain.best_partition(G)

# 結果をCSVファイルに保存
def save_communities_to_file(partition, filename):
    df = pd.DataFrame(list(partition.items()), columns=["Node", "Community"])
    df.to_csv(filename, index=False)
    print(f"コミュニティ結果が {filename} に保存されました。")

# メイン処理
def main():
    # 隣接行列ファイルのパス
    adjacency_matrix_file = "adjacency_matrix.csv"
    output_file = "community_results.csv"

    # 隣接行列の読み込み
    adjacency_matrix = load_adjacency_matrix(adjacency_matrix_file)

    # 隣接行列をグラフに変換
    G = adjacency_matrix_to_graph(adjacency_matrix)

    # Louvain法でのコミュニティ検出
    partition = detect_communities(G)
    
    # 各コミュニティのノードをまとめる
    communities = {}
    for node, community_id in partition.items():
        if community_id not in communities:
            communities[community_id] = []
        communities[community_id].append(node)
    
    # コミュニティの概要をログ出力
    print("コミュニティの概要:")
    print(f"検出されたコミュニティ数: {len(communities)}")
    
    for community_id, nodes in communities.items():
        print(f"コミュニティ {community_id}: ノード数 = {len(nodes)}")


    # 結果をファイルに保存
    save_communities_to_file(partition, output_file)

# 実行
if __name__ == "__main__":
    main()
