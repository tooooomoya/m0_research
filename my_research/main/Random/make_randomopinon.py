import pandas as pd
import random

number = 500

# 1列目: 1から100までの整数
col1 = list(range(1, number + 1))

# 2列目: 全て0
col2 = [0] * number

# 3列目: 0から1までの実数をランダムに生成
col3 = [random.random() for _ in range(number)]

# データフレームに変換
df = pd.DataFrame(zip(col1, col2, col3))

# タブ区切りでファイルに書き出し
tsv_filename = 'random_opinion.txt'
df.to_csv(tsv_filename, sep='\t', index=False, header=False)

print(f"ファイル {tsv_filename} が作成されました。")
