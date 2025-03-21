import pandas as pd
import numpy as np

number = 500

#tolとは違うシード値にする
seed_value = 42
np.random.seed(seed_value)

# 1列目: 1から100までの整数
col1 = list(range(1, number + 1))

# 2列目: 全て0
col2 = [0] * number

# 3列目: 正規分布に従った実数を生成 (平均0.5, 標準偏差0.1)
col3 = np.random.normal(0.5, 0.25, number)
#col3 = np.random.uniform(0, 1, number)

# 0.4を平均、0.2を標準偏差とした正規分布
dist1 = np.random.normal(0.2, 0.1, number//2)
# 0.6を平均、0.2を標準偏差とした正規分布
dist2 = np.random.normal(0.8, 0.1, number//2)
# 二つの分布を重ねる（結合する）
#col3 = np.concatenate([dist1, dist2])
np.random.shuffle(col3)

# 0以下と1以上の値をクリッピング
col3 = np.clip(col3, 0, 1)

# データフレームに変換
df = pd.DataFrame(zip(col1, col2, col3))

# タブ区切りでファイルに書き出し
tsv_filename = 'random_opinion.txt'
df.to_csv(tsv_filename, sep='\t', index=False, header=False)

print(f"ファイル {tsv_filename} が作成されました。")
