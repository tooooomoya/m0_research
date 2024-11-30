import csv
import networkx as nx
import matplotlib.pyplot as plt
import glob
import imageio
import os

# CSVファイルの読み込み (隣接行列)
def load_adjacency_matrix(file_path):
    with open(file_path, 'r') as file:
        reader = csv.reader(file)
        adjacency_matrix = []
        for row in reader:
            adjacency_matrix.append([float(val) for val in row])
    return adjacency_matrix

# CSVファイルの読み込み (zの値)
def load_z_values(file_path, num_nodes):
    with open(file_path, 'r') as file:
        reader = csv.reader(file)
        z_values = [float(row[0]) for row in reader]  # 各行の最初の値をzとして読み込む
        
        # ノード数と一致するようにz_valuesの長さを調整
        if len(z_values) < num_nodes:
            z_values.extend([0.5] * (num_nodes - len(z_values)))  # 例えば、足りない分を0.5で補充
        elif len(z_values) > num_nodes:
            z_values = z_values[:num_nodes]  # 余分な分は切り捨てる
            
    return z_values

# 隣接行列からNetworkXグラフを作成
def create_graph_from_adjacency_matrix(adjacency_matrix):
    G = nx.Graph()
    n = len(adjacency_matrix)
    for i in range(n):
        for j in range(n):
            if adjacency_matrix[i][j] > 0:  # 重みが0より大きい場合エッジを追加
                G.add_edge(i, j, weight=adjacency_matrix[i][j])
    return G

# ノードの色をzの値に基づいて設定
def get_node_color(z_value):
    # z_valueが0の場合赤、1の場合青、0〜1の間でグラデーションを作成
    return plt.cm.coolwarm(z_value)  # 'coolwarm'カラーマップを使用

# グラフを描画して画像保存
def save_graph_image(G, z_values, step, output_dir, lambda_value=None):
    pos = nx.spring_layout(G)  # Fruchterman-Reingoldアルゴリズム
    plt.figure(figsize=(6, 6))

    # ノードの色をzの値に基づいて設定
    node_colors = [get_node_color(z_values[i]) for i in range(len(G.nodes()))]

    nx.draw(
        G, pos,
        with_labels=False,
        node_size=10,
        node_color=node_colors,  # zの値に基づくノードの色
        edge_color="gray",
        width=[G[u][v]['weight'] for u, v in G.edges()]  # エッジ幅は重みに対応
    )
    
    # λ の値を図に追加
    if lambda_value is not None:
        plt.text(0.05, 0.95, f"λ = {lambda_value:.2f}", transform=plt.gca().transAxes,
                 fontsize=12, verticalalignment='top', horizontalalignment='left', color='black')

    plt.title(f"Step {step}")
    output_path = os.path.join(output_dir, f"step_{step}.png")
    plt.savefig(output_path)
    plt.close()
    return output_path

# メイン処理
def main():
    # ファイルを取得して、ステップ順にソート
    csv_files = sorted(glob.glob("Temp/graph_step*.csv"), key=lambda x: int(x.split('step')[1].split('.')[0]))
    z_files = sorted(glob.glob("Temp/z_step*.csv"), key=lambda x: int(x.split('step')[1].split('.')[0]))
    lambda_files = sorted(glob.glob("Temp/lambda_step*.csv"), key=lambda x: int(x.split('step')[1].split('.')[0]))

    output_dir = "graph_images"
    os.makedirs(output_dir, exist_ok=True)

    images = []  # GIF用の画像リスト
    for step, (adj_matrix_file, z_file, lambda_file) in enumerate(zip(csv_files, z_files, lambda_files)):
        # 隣接行列とzの値を読み込む
        adjacency_matrix = load_adjacency_matrix(adj_matrix_file)
        G = create_graph_from_adjacency_matrix(adjacency_matrix)
        
        # z_valuesの長さを調整
        z_values = load_z_values(z_file, len(G.nodes()))  # ノード数を渡す
        
        # λ の値を読み込む
        with open(lambda_file, 'r') as file:
            lambda_value = float(file.read().strip())

        # グラフの画像を保存
        image_path = save_graph_image(G, z_values, step, output_dir, lambda_value)
        images.append(image_path)

    # GIFを作成
    gif_path = "graph_animation.gif"
    with imageio.get_writer(gif_path, mode="I", duration=500.0) as writer:
        for image_path in images:
            image = imageio.imread(image_path)
            writer.append_data(image)

    print(f"GIF animation saved as {gif_path}")

if __name__ == "__main__":
    main()
