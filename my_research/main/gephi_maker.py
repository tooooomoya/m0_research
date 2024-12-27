import os
import networkx as nx

import os

def downgrade_gexf_version(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()

    # 1.3を1.2に変換
    content = content.replace(
        'http://gexf.net/1.3',
        'http://www.gexf.net/1.2draft'
    ).replace(
        'version="1.3"',
        'version="1.2"'
    ).replace(
        'http://gexf.net/1.3/gexf.xsd',
        'http://www.gexf.net/1.2draft/gexf.xsd'
    )

    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(content)

# フォルダ内の全てのGEXFファイルを処理
input_folder = "GEXF/lambda_0.02"
for file_name in os.listdir(input_folder):
    if file_name.endswith(".gexf"):
        downgrade_gexf_version(os.path.join(input_folder, file_name))


def create_dynamic_graph(gexf_folder, output_file):
    """
    指定されたフォルダ内の .gexf ファイルを順番に読み込んで、
    動的グラフとして統合し、1つの出力ファイルに保存します。

    Args:
        gexf_folder (str): .gexf ファイルが格納されたフォルダのパス
        output_file (str): 統合された動的グラフの出力ファイル名
    """
    # フォルダ内の gexf ファイルを取得してソート
    gexf_files = sorted(
        [f for f in os.listdir(gexf_folder) if f.endswith(".gexf")],
        key=lambda x: int(x.split('_')[-1].split('.')[0])  # ファイル名のステップ番号でソート
    )

    dynamic_graph = nx.DiGraph()

    # 各ファイルを読み込み、タイムステップ情報を追加
    for time_step, gexf_file in enumerate(gexf_files, start=0):  # start=0は最初のタイムステップ
        file_path = os.path.join(gexf_folder, gexf_file)
        g = nx.read_gexf(file_path)

        # ノードとタイムステップ情報を統合
        for node, data in g.nodes(data=True):
            dynamic_graph.add_node(node, **data)
            dynamic_graph.nodes[node]["start"] = dynamic_graph.nodes[node].get("start", time_step)
            dynamic_graph.nodes[node]["end"] = time_step + 1  # 現在のタイムステップ終了

        # エッジとタイムステップ情報を統合
        for source, target, data in g.edges(data=True):
            dynamic_graph.add_edge(source, target, **data)
            dynamic_graph.edges[source, target]["start"] = dynamic_graph.edges[source, target].get("start", time_step)
            dynamic_graph.edges[source, target]["end"] = time_step + 1  # 現在のタイムステップ終了

    # 統合された動的グラフを出力
    nx.write_gexf(dynamic_graph, output_file)
    print(f"動的グラフが作成されました: {output_file}")


# 実行例
input_folder = "GEXF/lambda_0.02/"
output_file = "dynamic_graph_lambda_0.02.gexf"
create_dynamic_graph(input_folder, output_file)
