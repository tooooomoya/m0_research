import pandas as pd
import numpy as np

number = 556

# 1列目: 1からnumberまでの整数
col1 = list(range(1, number + 1))

# 2列目: 全て0
col2 = [0] * number

# 3列目: 0から1までの正規分布値
# 標準正規分布の値を生成し、最小値0, 最大値1にスケーリング
normal_values = np.random.normal(loc=0.5, scale=0.15, size=number)  # 平均0.5、標準偏差0.15
# 最小値と最大値を取得
min_val = np.min(normal_values)
max_val = np.max(normal_values)

# min-maxスケーリングを適用
col3 = (normal_values - min_val) / (max_val - min_val)

# データフレームに変換
df = pd.DataFrame({
    'Column1': col1,
    'Column2': col2,
    'Column3': col3
})

# タブ区切りでファイルに書き出し
tsv_filename = 'reddit_opinion.txt'
df.to_csv(tsv_filename, sep='\t', index=False)

print(f"ファイル {tsv_filename} が作成されました。")
