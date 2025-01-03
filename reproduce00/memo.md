#### ローカルリポジトリを最新の状態に保つ
```
git fetch origin main
```
```
git merge origin/main
```

## Remind
* \nを打つためには、Option + ¥　で打てます。
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
	フレームワークを導入すれば逆行列も計算できるはず。utils.matrix_utilに逆行列計算は未実装。逆行列をまだ使ってないということかな、、？
* 9/24
	minZは逆行列が計算できればいける。
	existingはすでに重みがあるところのみ変化させる。ただ、全部Falseになっている、、？
	逆行列は別にApacheCommonMathを外部ぶらりぶらりとしてインストールしちゃおう。（gurobiでも間接的に最適化問題として解けるけど）
	gurobiの設定を完了。動作も確認済み。
	残りは、optimization.javaを完成させる（逆行列と、gurobiの使い方とか）
	あとは他のコードの動作確認
* 9/25
	とりあえずApacheCommonsMathが終わりました。
	optimizationもエラーが消えたので、あとは全部コードを一覧してチェックして
	試しにコンパイルしてみる。
	それぞれ逐次的にやってみて、（ダミーの結果を用意するなどして）pythonとか使って結果を可視化する箇所もやってみてはいかが。
* 10/8
	1つ目の制約のエラーは解消された。
	次は二つ目の制約を作る。
	結果が怪しいことになってるから、それを確認しよう。
* 10/9
	二つ目の制約も完成→結果を確認するために結果を描画させるpythonコードを書く。
	SNS_opinion.csvの２個目の要素は特に関係ない、、？時間とか？
	多分、一個目の要素がエージェントのID、３個目が投稿のopinion
	→sの値は、zの初期値ではないのか？zがすでにFJモデルに従って周りから影響を受けているとみて、sを逆算するのか。
* 10/15
	どうやらAdmingameでterminal criterionに全部最初で引っかかっているっぽい
	Wは最初、Aから始まっている？→0か1の値しかない隣接行列。重み行列ではない。
	w_ijは０以上の実数です。
* 10/16
	簡単なグラフでバグを検出することにします。とりあえずランダムグラフ大きさ１００を作りました。
	最初にsを作るが（FJモデルの逆算で）、その時点でもうほぼ分極が完成している。（真ん中が10％くらいしかいない。）

	重みは適切に変化している。Terminal条件で引っかかっているのが原因か。あとは結果格納するときにミスが起きてるかもしれない。あとはワンちゃんJavaの環境が悪い？→インデックスの問題ではあった。

	次回確認すべきは、終了条件のところと、結果格納Result型の変数に代入するところが成功してるかどうか。
* 10/21
	一度、withFixなしで確認してみる。→RunSimulateから削除、PlotResultにはもともと記述なし。

	plsが実験前後で0.013から変化していない。→だからplsのグラフは"1"で一定

	disaggは実験前後で変化しているが、全てのλ(ε)において変化率が全く同じなので、結果が一定

	→良かったことは、結果の格納過程にはバグはなさそう。やはりguroubiが原因か

	最初にLoadするときのClippingで、FJモデル効果によってほとんどが0,1の外側だから0か1になってしまう→結局そこで丸められるせいで、すぐ偏っちゃってCriteriaに引っかかってしまう。

	要はguroubiがあまりに大きい変化を生じさせるから、というか、zの値が小さいすぎるというか

	zとznewが変わってない。嘘だろ。→引数のWとWnewは違っているのに、結果のzとznewが変わっていない。ここが問題。引数が違うのに結果が同じなの欠陥関数すぎる。この関数だけチェックしてもいいかもしれない。

* 10/22
	問題が２つあって、

		guroubiの最適化による影響が大きすぎて、criterionに引っかかっている←まじ？

		z関数が機能していない説がある。
	
	z関数のバグはおそらく修正完了（I行列がWになっていた。）

	次の問題は、λによって結果が変わっていないこと。

	面倒なので、ipynbを動かして、途中結果を出させて比較して、間違いを見つけます。

	ipynbを用いたバグ検査の結果、disagg, plsの計算方法は間違っていない(同じzで同じinitialの値になっている。)が、やはりFJモデル(minZ)かAdmin(min_W)かで計算違いが起こっていて、最終的な結果、finalの方は違っている。このどちらかを確認してください。

* 10/23
	Wの計算結果が違う。
	min_Z関数は合っていた。やはりmin_w関数が何か違う。

	直せました。あとは、pythonの方ではWが0,1だけの行列が一度デバッグされるんだけど、Javaではない。なんか回数が違うとかカモ。でも、計算は小数まであってます！！！

* 10/28
	おそらく前回、計算がほんの少しずつずれているのが、numpyとJavaの性能の違いなんじゃないか、という気がしていて。そこを治すために新しく逆行列計算の部分でフレームワークを導入した。ようとしている。
	
	全く違ったかもしれない

	まずW-Wnewの計算が違う。次にCriterionが機能してない。

	→完璧に動きそう。Maven必要なし

	non-null対策だけmi_Wのところで必要かも

	テストデータではPLSもDISAGGもほとんど一致している。ただ、桁的にみてだんだんずれていっている可能性はある。でも大まかな傾向が変わるとは思えない、、

* 10/29
	最初のs,zから違うことが判明した。→Aからzを計算するところで=1のところが+=1になっていました。

	notConnectedも一応直した。

	若干disaggの結果が違うが、、Testの方ではほとんどdisaggの値も一致しているので、誤差とみるのがいいかな。
		→ただ誤差はdisaggもpltも同じぐらいIterationによる差があるとも思えない。
	
	元の論文の提案手法じゃない方は再現できた。（ほぼ）

	ただ、提案手法の方は、regularizedしてる方は、sub-optimal statusということで最適解が存在しない？みたいな感じ。

	まあ、reduceplsがtrueの方のコードがどこか間違っている可能性もあるので、それを検証しつつ、自分のやつもやってみればいいのでは？

	早速、W-AのところをW-Wpastにしてみるとか→実はこれは合っているのか？Iみたいな内部意見みたいな話のとこ
	ろで

* 10/30
	minWの効果をなくしたら、結果が全て0になった。→つまり最初と最後でDisaggとPltが変化していないということ。

		→Wが変わらないと、zは変わらない！

	→エージェントの自発的な効果をいれてここを変えてもいいかもね。「フォロー」という動き→重みを＋0.5するとかね。

	こちらは引き続き再現をすることを目標にしましょう。特にFixの方を再現できるように。

## 謎
* 個々のノードがもつエッジの重みの合計値は保存されるはずだが、それで変化は起きるのか？まあ起きるのか、、

* Aは最初、エッジがある要素に+1をするだけです。いいのか.いやそうじゃない。インタラクション記録あるところを毎回+1している可能性もある。


## Memo
* Shift+option+Fで整形
* グラフ描画フレームワーク：JgraphTが一番有名らしい。Jungというのがあるらしい
	→例のMavenを使わなきゃいけない。自分で作れんのか？
	→VSCodeで外部ライブラリを追加できる方法があった。
* なぜ、sの作り方があれなのか。初期値を反映しているのかあれで。
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
tomoyatakeda@takedatomoonarinoMacBook-Pro main % javac -cp "/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" -d out utils/matrix_util.java utils/optimization.java *.java
tomoyatakeda@takedatomoonarinoMacBook-Pro main % java -cp "out:/Library/gurobi1103/macos_universal2/lib/gurobi.jar:/Library/ApacheCommonsMath/commons-math3-3.6.1.jar" Main
```



* ディレクトリ構造を作る。以下参考
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
