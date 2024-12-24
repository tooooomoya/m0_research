import pandas as pd
import numpy as np

# 人数を指定
number_of_people = 556

#opinionとは違うシード値にする
seed_value = 10
np.random.seed(seed_value)

# 許容度をベータ分布に従って生成 (α=2, β=5)
alpha, beta = 2, 5
tolerance = np.random.beta(alpha, beta, number_of_people)
#tolerance = np.random.normal(0.5, 0.25, number_of_people)
tolerance = np.clip(tolerance, 0, 1)

# データフレームに変換（人IDと対応させる）
df = pd.DataFrame({
    'Person_ID': range(1, number_of_people + 1),  # 人ID: 1から開始
    'Tolerance': tolerance                       # 許容度
})

# データフレームの確認
print(df.head())

# タブ区切りでファイルに書き出し
tsv_filename = 'tolerance_distribution.txt'
df.to_csv(tsv_filename, sep='\t', index=False, header=False)

print(f"ファイル {tsv_filename} が作成されました。")
