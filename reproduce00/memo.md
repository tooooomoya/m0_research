## Remind
* 俺のDockerのromantic_formetイメージにipynbファイルをアップロードした。
  →立ち上げ方：	docker login
  		docker run -it -p 8888:8888 kikagaku/pytorch-topgear
  		8888 port で kikagaku と入力

## Progress
* 7/28
	ln[268]の"load NW and Opinion"まで終了
* 7/29
	

## Memo
* グラフ描画フレームワーク：JgraphTが一番有名らしい。Jungというのがあるらしい
	->例のMavenを使わなきゃいけない。自分で作れんのか？

## ToDo
* 行列計算をutilとして実装しておく。
	→逆行列だけは面倒い。フレームワークもだるい。pythonかませるか？

### Finished 
参考にして

* ディレクトリ構造を作る。以下参考
<pre>
.
├── README.md
├── config
│   ├── 
│   ├── 
│   ├── 
│   ├──　
│   └──　
├── machine			最適化処理、とか色々な計算？
│   ├── LastFM.py
│   ├── MovieLens100k.py
│   ├── MovieLens1M.py
│   ├── SyntheticClick.py
│   ├── __init__.py
│   └── converter.py
├── experiment.java		メイン
├── Reddit
│   ├── 
│   ├── 
│   ├── 
│   └── 
├── Twitter
│   ├── 
│   ├── 
│   ├── 
│   └── 
├── Network
│   ├── Agent.java
│   ├── Edge.java
│   ├── Network.java
│   └── 	
├── Data			結果データの格納
│   ├── 
│   ├── 
│   ├── 
│   └── 
└── utils
    ├── graph_util.java		グラフ描画
    ├── 
    └── matrix_util.java	行列計算
</pre>
