import networkx as nx
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

# CSVファイルから隣接行列を読み込む
adj_matrix = pd.read_csv('adjacency_matrix.csv', header=None).values

# NetworkXのグラフに変換する
G = nx.from_numpy_array(adj_matrix)

# レイアウトを計算（spring_layoutなども試せる）
pos = nx.spring_layout(G)  # spring_layoutはノードが重ならないように配置

# 描画のための設定
plt.figure(figsize=(8, 8))  # 図のサイズを設定

# ノード数が多い場合、ノードサイズをさらに小さく
node_size = 10  # 小さくしたいので10に設定

edge_width = 0.001

# ノードの識別ラベルを消すためにwith_labels=Falseを設定
nx.draw(G, pos, node_size=node_size, with_labels=False, node_color="lightblue", edge_color="gray", alpha=0.7)

# グラフを表示
plt.show()
