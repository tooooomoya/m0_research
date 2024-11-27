# To Do
* 次回のSNSミーティングまでに実装すること
	
	1. ランダム性の導入

	2. 指標の導入と結果格納コードの作成
		→指標は、集団極性化指標(極端な意見を持ったエージェントの割合)、多様性指標(①意見が違うエージェントの割合→リンクの重みが0.1以下はないとする、とかでいんじゃね②コミュニティ内部での意見の多様性→これこそ分散値とかだろ。)

		コミュニティのやつはむずそうだな、各エージェントに対してコミュニティのラベルをつけちゃうのか、それともコミュニティを毎回作るのか。

	3. もう一個のフォルダでFixの再現

	どのタイミングでグラフ用のAdjecencyファイルを保存するか。一回の実験で毎回するわけにもいかない。
		→一番均衡したタイミングを結果で見て、そのタイミングでAdjecncyを保存するようにすればいいか。

## Progress

* 11/1
	①を終了②も終了、結果待ち

	λが大きくなってくると、重み最大値が１０越えのリンクが出てくる。→先行研究の提案手法が実現できたら一番いいが、これは放って置けないだろう。

	ランダム性の結果だけ得て終わりたい。だからまずは大元の比較のための結果がなきゃいけない。

	λが大きくなるとエラーが頻発するが、特にIterが何回か進んだあとだし、λが大きいほうはあまり現実的ではないし？収束してるし、とりあえず無視。

	λが大きくなるにつれて、Disが大きくなっているのは明らかに最適化が失敗してないか？

* 10/30
	自分の実験用にこのフォルダを作成しました。

	とりあえず、集団極性化指標を導入しよう。

	その前にmin_Wの引数のAを毎回変化するWに変えてみます。変わるところとしては、毎回Dを計算するときにリンクがある箇所が変化する。他には、最適化の拘束条件の一番目が変わる。毎回の変化率が制限される。

	エラーが発生した箇所を報告するように変更したい。

	次回することリスト①エラーが発生した箇所を最後に報告するように変更。②集団極性化指標を導入する③ランダム性④Fixの方のメンテ

## 謎
* 個々のノードがもつエッジの重みの合計値は保存されるはずだが、それで変化は起きるのか？まあ起きるのか、、



## Memo
* Shift+option+Fで整形
* グラフ描画フレームワーク：JgraphTが一番有名らしい。Jungというのがあるらしい
	→例のMavenを使わなきゃいけない。自分で作れんのか？
	→VSCodeで外部ライブラリを追加できる方法があった。
* 環境変数にしたいもの
	ノード数nSNS、Regularized指標
* グラフ描画用のコードも載ってる。
* opinionの平均(zの初期値)がほとんど0.5付近、たしかに元のデータを見ても、極端な意見の投稿はあまりない。
	→でも計算した後のsは全体的にばらけている。
* グラフ描画も早めにできるようになりたい。
* constsフォルダを作成して、constファイルを置いて、そこからの参照で定数を設定する。植木さんのnetwork.javaに書いてある。
* existingの方についてはどうせ使わないから、チェックしてません。

## ToDo
* 逆行列の計算と、最適化の部分でJavaフレームワークを導入する。
~~ * 結果を格納するrdの型が違いそう。本家では、２個目のインデックスでdisaggとplsを区別できている。 ~~
~~ * machinesの内容を完成させる。~~
* フレームワーク導入できたら、あとは一通り確認して、実行してみるだけ。
* 結果を描画するにおいて、pythonでグラフを書いて、GIF作ったりして、グラフの描画は何かのライブラリ使ったらできるのでは？

### Finished 
参考にして

#### コンパイルと実行
* LoadNW.java
	実行時に0でReddit、1でTwitterネットワークを選択
```
tomoyatakeda@takedatomoonarinoMacBook-Pro reproduce00 % cd main
tomoyatakeda@takedatomoonarinoMacBook-Pro main % javac -d bin utils/matrix_util.java LoadNW.java 
tomoyatakeda@takedatomoonarinoMacBook-Pro main % java -cp bin LoadNW 0       
```

* RunSimulate.java
```
tomoyatakeda@takedatomoonarinoMacBook-Pro main % javac -d out utils/matrix_util.java utils/optimization.java RunSimulate.java
```

これでうまくいかないときは以下

```
javac -cp "/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" -d out utils/matrix_util.java utils/optimization.java RunSimulate.java
```

以下でMainが実行できます。

```
javac -cp "/Users/tomoyatakeda/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Users/tomoyatakeda/Library/ApacheCommonsMath/commons-math3-3.6.1.jar:/Users/tomoyatakeda/Library/AnimatedGIF/animated-gif-lib-1.4.jar" -d out utils/*.java structure/*.java *.java
```

```
java -cp "out:/Users/tomoyatakeda/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Users/tomoyatakeda/Library/ApacheCommonsMath/commons-math3-3.6.1.jar:/Users/tomoyatakeda/Library/AnimatedGIF/animated-gif-lib-1.4.jar" Main 
```

以下は、マシン上で動かすとき。

```
javac -cp "/Users/tomoyatakeda/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Users/tomoyatakeda/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" -d out utils/*.java structure/*.java *.java
```

```
java -cp "out:/Users/tomoyatakeda/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Users/tomoyatakeda/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" Main
```

以下は、マシン上で動かすとき。

```
javac -cp "/Users/tomoyatakeda/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Users/tomoyatakeda/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" -d out utils/*.java structure/*.java *.java
```

```
java -cp "out:/Users/tomoyatakeda/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Users/tomoyatakeda/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" Main
```

#### ローカルリポジトリを最新の状態に保つ
```
git fetch origin main
```
```
git merge origin/main
```

## Remind
* \nを打つためには、Option + ¥　で打てます。

#### gurobi関連
	ライセンス	98@bWcTTzd2x$j2
	
	パス	

```
readlink `which gurobi.sh`
```
```
//Library/gurobi1103/macos_universal2/bin/gurobi.sh`
```
* 
	libgurobi110.dylibの場所を変更したら動いた。
	import文を```import com.gurobi.gurobi.*;```に変更した
	ライセンスファイルのパスを環境変数に設定した。
	Referenced Librariesにjarファイルのパスを追加した。→Java、外部ライブラリ、VScodeで検索
	などなど

#### ApacheCommonsMath
* jarファイルは 
	```/Library/ApacheCommonsMath/commons-math3-3.6.1.jar```
	にある。
* コンパイル方法は、上のjarファイルのパスと、自分のカラントディレクトリを含めて(":"で区切る)以下の通り
```
javac -cp ".:/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" MatrixInversionExample.java
java -cp ".:/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" MatrixInversionExample
```

### ND4J
```
javac -cp ".:/Library/Java/Extensions/nd4j-native-0.9.1.jar" -d out ND4JTest.java
```


### ディレクトリ構造を作る。以下参考
<pre>
.
├── README.md
├── LoadNW.java	NWのデータセットを読み込んで隣接行列A, intrinsic行列sを作成
├── RunSimulate.java	AdminGame.javaをマシーンとして使用して結果を返す
├── PlotResults.java	結果のデータを整理して、Resultsフォルダにcsvとして吐き出す
├── ResultPair.java		修正前後の結果をペアとして格納するコンストラクタ用
├── Result.java			シミュレーション一回の結果を格納するコンストラクタ用
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
