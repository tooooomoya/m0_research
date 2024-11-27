import networkx as nx
import pandas as pd
import random

#### 0は含まないので１５に変えてください。

# ノード数とエッジ数のパラメータ
num_nodes = 500  # ノード数を1から100に設定
edge_prob = 0.01  # エッジが存在する確率 (0から1の範囲)

# ランダムグラフを作成 (gnp_random_graph はランダムにエッジを生成)
random_graph = nx.gnp_random_graph(num_nodes, edge_prob)

# エッジ情報をリスト形式で取得 (各エッジはノード1とノード2の組み合わせ)
edges = list(random_graph.edges())

# エッジ情報をデータフレームに変換
edge_df = pd.DataFrame(edges)

# タブ区切りのTSVファイルに書き出し
tsv_filename = 'edges_random.txt'
edge_df.to_csv(tsv_filename, sep='\t', index=False)

print(f"エッジ情報を {tsv_filename} に保存しました。")