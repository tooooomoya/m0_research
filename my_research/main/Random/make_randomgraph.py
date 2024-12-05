import networkx as nx
import pandas as pd
import random
import matplotlib.pyplot as plt

# ノード数とエッジの確率
num_nodes = 500  # ノード数
edge_prob = 0.01  # エッジが存在する確率 (0から1の範囲)

# 有向グラフを作成
random_digraph = nx.gnp_random_graph(num_nodes, edge_prob, directed=True)

# エッジ情報をリスト形式で取得 (各エッジはノード1とノード2の組み合わせ)
edges = list(random_digraph.edges())

# エッジ情報をデータフレームに変換
edge_df = pd.DataFrame(edges, columns=["Source", "Target"])

# タブ区切りのTSVファイルに書き出し
tsv_filename = 'edges_random.txt'
edge_df.to_csv(tsv_filename, sep='\t', index=False, header=False)

print(f"エッジ情報を {tsv_filename} に保存しました。")
