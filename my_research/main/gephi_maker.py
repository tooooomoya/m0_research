import os
import networkx as nx
import xml.etree.ElementTree as ET
import numpy as np 

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
input_folder = "GEXF/lambda_0.04"
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
    
def create_dynamic_graph2(gexf_folder, output_file):
    """
    指定されたフォルダ内の .gexf ファイルを 10 ステップごとに統合し、
    各ノードの "z" の平均値を時間情報とともに保存する。

    Args:
        gexf_folder (str): .gexf ファイルが格納されたフォルダのパス
        output_file (str): 統合された動的グラフの出力ファイル名
    """
    gexf_files = sorted(
        [f for f in os.listdir(gexf_folder) if f.endswith(".gexf")],
        key=lambda x: int(x.split('_')[-1].split('.')[0])  # ステップ番号でソート
    )

    dynamic_graph = nx.DiGraph()
    node_history = {}  # 各ノードの "z" の履歴

    # 10 ステップごとにグループ化
    step_groups = [gexf_files[i:i+10] for i in range(0, len(gexf_files), 10)]

    for time_step, group in enumerate(step_groups):
        t = time_step * 10  # 時間は 0, 10, 20, ..., 100

        z_values = {}  # ノードごとの "z" のリスト

        for gexf_file in group:
            file_path = os.path.join(gexf_folder, gexf_file)
            g = nx.read_gexf(file_path)

            for node, data in g.nodes(data=True):
                z = float(data.get("z", 0))  # "z" がない場合は 0
                if node not in z_values:
                    z_values[node] = []
                z_values[node].append(z)

        # 10 ステップの平均値を計算し、動的属性として保存
        for node, values in z_values.items():
            avg_z = np.mean(values)  # 10ステップの平均

            if node not in node_history:
                node_history[node] = []

            node_history[node].append((t, avg_z))  # (時間, 平均 z)

            dynamic_graph.add_node(node)
            dynamic_graph.nodes[node]["z"] = node_history[node]  # "z" の時間変化を保存

    # GEXF にエクスポート
    write_dynamic_gexf(dynamic_graph, output_file)
    
def create_dynamic_graph_edges_only(gexf_folder, output_file):
    """
    指定されたフォルダ内の .gexf ファイルを順番に読み込んで、
    動的グラフとしてエッジ情報のみ動的に統合し、1つの出力ファイルに保存します。

    Args:
        gexf_folder (str): .gexf ファイルが格納されたフォルダのパス
        output_file (str): 統合された動的グラフの出力ファイル名
    """
    gexf_files = sorted(
        [f for f in os.listdir(gexf_folder) if f.endswith(".gexf")],
        key=lambda x: int(x.split('_')[-1].split('.')[0])  # ステップ番号でソート
    )

    dynamic_graph = nx.DiGraph()

    # ノードは最初のファイルでのみ登録（属性は静的）
    first_file_path = os.path.join(gexf_folder, gexf_files[0])
    g_first = nx.read_gexf(first_file_path)
    for node, data in g_first.nodes(data=True):
        dynamic_graph.add_node(node, **data)

    # エッジ情報を動的に登録
    for time_step, gexf_file in enumerate(gexf_files):
        file_path = os.path.join(gexf_folder, gexf_file)
        g = nx.read_gexf(file_path)

        # 現在存在するエッジのセット
        current_edges = set(g.edges())

        # 既存のエッジを更新または削除
        for source, target, data in dynamic_graph.edges(data=True):
            if (source, target) in current_edges:
                # エッジがまだ存在するなら終了時刻を延長
                dynamic_graph.edges[source, target]["end"] = time_step + 1
            else:
                # エッジが消滅したら終了時刻を確定
                if "end" not in dynamic_graph.edges[source, target]:
                    dynamic_graph.edges[source, target]["end"] = time_step

        # 新規エッジを追加
        for source, target, data in g.edges(data=True):
            if not dynamic_graph.has_edge(source, target):
                dynamic_graph.add_edge(source, target, **data)
                dynamic_graph.edges[source, target]["start"] = time_step
                dynamic_graph.edges[source, target]["end"] = time_step + 1

    # 統合された動的グラフを出力
    write_dynamic_gexf(dynamic_graph, output_file)
    print(f"動的グラフが作成されました（エッジのみ動的）: {output_file}")

def indent(elem, level=0):
    """
    XML要素をインデントして整形するヘルパー関数
    """
    i = "\n" + level * "  "
    if len(elem):
        if not elem.text or not elem.text.strip():
            elem.text = i + "  "
        for child in elem:
            indent(child, level + 1)
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            elem.tail = i

def write_dynamic_gexf(graph, output_file):
    """
    networkx のグラフを GEXF フォーマットにエクスポート。
    エッジの時間変化を動的属性として出力。

    Args:
        graph (nx.DiGraph): 動的グラフ
        output_file (str): 出力 GEXF ファイル
    """
    root = ET.Element("gexf", xmlns="http://www.gexf.net/1.2draft", version="1.2")
    graph_elem = ET.SubElement(root, "graph", mode="dynamic", timeformat="integer")

    # ノード定義
    nodes_elem = ET.SubElement(graph_elem, "nodes")
    for node, data in graph.nodes(data=True):
        node_elem = ET.SubElement(nodes_elem, "node", id=str(node), label=str(node))
        attvalues = ET.SubElement(node_elem, "attvalues")
        if "z" in data:
            ET.SubElement(attvalues, "attvalue", {"for": "0", "value": str(data["z"])})

    # エッジ定義
    edges_elem = ET.SubElement(graph_elem, "edges")
    for i, (source, target, data) in enumerate(graph.edges(data=True)):
        start = str(data.get("start", 0))
        end = str(data.get("end", 0))
        edge_elem = ET.SubElement(edges_elem, "edge", id=str(i), source=str(source), target=str(target), start=start, end=end)
        attvalues = ET.SubElement(edge_elem, "attvalues")
        for key, value in data.items():
            if key not in ["start", "end"]:
                ET.SubElement(attvalues, "attvalue", {"for": key, "value": str(value)})

    # 整形して出力
    indent(root)
    tree = ET.ElementTree(root)
    tree.write(output_file, encoding="utf-8", xml_declaration=True)
    print(f"動的グラフがGEXF形式で保存されました: {output_file}")


# 実行例
input_folder = "GEXF/lambda_0.04/"
output_file = "dynamic_graph_lambda_0.04.gexf"
create_dynamic_graph_edges_only(input_folder, output_file)
