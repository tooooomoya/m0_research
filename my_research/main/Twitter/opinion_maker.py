import pandas as pd
import numpy as np

number = 548

# 1列目: 1からnumberまでの整数
col1 = list(range(1, number + 1))

# 2列目: 全て0
col2 = [0] * number

# 3列目: 0から1までの正規分布値
# 標準正規分布の値を生成し、最小値0, 最大値1にスケーリング
#normal_values = np.random.normal(loc=0.5, scale=0.25, size=number)  # 平均0.5、標準偏差0.15
normal_values = np.random.uniform(0, 1, number)
col3 = np.clip(normal_values, 0, 1)  # 0から1の範囲にクリップ

# データフレームに変換
df = pd.DataFrame({
    'Column1': col1,
    'Column2': col2,
    'Column3': col3
})

# タブ区切りでファイルに書き出し
tsv_filename = 'twitter_opinion.txt'
df.to_csv(tsv_filename, sep='\t', index=False, header=False)

print(f"ファイル {tsv_filename} が作成されました。")
