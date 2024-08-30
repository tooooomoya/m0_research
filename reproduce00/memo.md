#### ローカルリポジトリを最新の状態に保つ
```
git fetch origin main
```
```
git merge origin/main
```

## Remind
* 普通にipynbファイルはVSCodeで見れました。
* 俺のDockerのromantic_formetイメージにipynbファイルをアップロードした。
  →立ち上げ方：	

	```docker login```
	```docker run -it -p 8888:8888 kikagaku/pytorch-topgear```
	
	8888 port で kikagaku と入力

* portが占有されている場合は、まずPIDを確認
	* windows

	```netstat -ano | findstr :8888```
	```taskkill /PID <enter pid here> /F```

	* Mac

	```lsof -i :8888```
	```kill -9 <enter pid here>```

## Progress
* 7/28
	ln[268]の"load NW and Opinion"まで終了
* 7/29
	
* 7/30
	結果の出力もフレームワーク導入で何とかなるが、とりあえずはcsvに書き出すようにした。
* 7/31
* 8/30
	多分machinesの内容は適当に作ってあるだけだから修正が必要
	LoadNW.java, Runsimulate.java(中身のAdminGame.javaも), PlotResults.javaを修正、とりあえず完了
	次は、AdminGame.javaで使用しているmachinesの整備（おそらくフレームワークを導入する）
	フレームワークを導入すれば逆行列も計算できるはず。utils.matrix_utilに逆行列計算は未実装。まだ使ってないということかな、、？
	

## Memo
* Shift+option+Fで整形
* グラフ描画フレームワーク：JgraphTが一番有名らしい。Jungというのがあるらしい
	->例のMavenを使わなきゃいけない。自分で作れんのか？
* なぜ、sの作り方があれなのか。初期値を反映しているのかあれで。
* 環境変数にしたいもの
	ノード数nSNS、Regularized指標
* グラフ描画用のコードも載ってる。

## ToDo
* 逆行列の計算と、最適化の部分でJavaフレームワークを導入する。
* 結果を格納するrdの型が違いそう。本家では、２個目のインデックスでdisaggとplsを区別できている。
* machinesの内容を完成させる。
* フレームワーク導入できたら、あとは一通り確認して、実行してみるだけ。

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
├── Data			結果データ等の格納　ここのcsvをpythonで読み取って描画させよう。python使って描画させたいグラフ構造とかもココに吐き出させる。
│   ├── 
│   ├── 
│   ├── 
│   └── 
└── utils
    ├── graph_util.java		グラフ描画
    ├── 
    └── matrix_util.java	行列計算
</pre>

## Remind
* 普通にipynbファイルはVSCodeで見れました。
* 俺のDockerのromantic_formetイメージにipynbファイルをアップロードした。
  →立ち上げ方：	

	```docker login```
	```docker run -it -p 8888:8888 kikagaku/pytorch-topgear```
	
	8888 port で kikagaku と入力

* portが占有されている場合は、まずPIDを確認
	* windows

	```netstat -ano | findstr :8888```
	```taskkill /PID <enter pid here> /F```

	* Mac

	```lsof -i :8888```
	```kill -9 <enter pid here>```
