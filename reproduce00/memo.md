ディレクトリ構造を作る。以下参考。

<pre>
.
├── README.md
├── config
│   ├── LastFM
│   ├── ML100k
│   ├── ML1M
│   ├── click
│   └── example.ini
├── converter
│   ├── LastFM.py
│   ├── MovieLens100k.py
│   ├── MovieLens1M.py
│   ├── SyntheticClick.py
│   ├── __init__.py
│   └── converter.py
├── data
├── experiment.py
├── notebook
│   ├── LastFM.ipynb
│   ├── claim-iMF.ipynb
│   ├── paper-iFMs.ipynb
│   └── paper-sketch.ipynb
├── requirements.txt
├── results
└── tool
    ├── clickgenerator.jl
    └── parse_result.py
</pre>

* 俺のDockerのromantic_formetイメージにipynbファイルをアップロードした。
  →立ち上げ方：
	
  →行列の計算、ゆうて転置と逆行列が算出できればよさそう。
* 行列計算をutilとして実装しておく。
  →グラフ描画フレームワーク：JgrapthTが一番有名らしい。Jungというのがあるらしい


