TicTacToe
=========
Suppose two million bots pair up and play one million random games of Tic Tac Toe...

<pre>
Suppose two million bots pair up and play one million random games of Tic Tac Toe!

Two questions -
1. How many of those 1 million games end in 5 moves ?
2. How many unique 5-move games exist ?

If you'd like to take a stab at it, here are the solutions -
A1. If you play 1 million games, approximately 95238 games end in 5 moves. ( ie. 9.5%)
A2. There are exactly 2880 5-moves games.


scala> TicTacToe.playGame(true)

Move:0
_ _ _
_ _ _
_ _ _

Move:1
_ x _
_ _ _
_ _ _

Move:2
_ x _
_ _ _
_ o _

Move:3
_ x _
_ x _
_ o _

Move:4
_ x _
_ x _
o o _

Move:5
_ x _
x x _
o o _

Move:6
_ x _
x x _
o o o
We have a winner! o wins in 6 moves!
res1: (String, Int) = (_________~_x_______~_x_____o_~_x__x__o_~_x__x_oo_~_x_xx_oo_~_x_xx_ooo,6)
</pre>

1 MILLION GAMES
===============

<pre>
$scald.rb --hdfs-local TicTacToe.scala --n 1000000 --show false

This takes about 2 minutes, and once we're done, we get
$ more tictactoe1000000/part-00000
Count    Game
264068      7
225460      9
200363      8
126894     -1
95401       5
87814       6

So of the 1 million games, 95401 games ended in exactly 5 moves.
That answers the first question.

We could rephrase the first question thusly - What are the chances a typical game takes exactly 5 moves ?
That's obviously
95401/1000000 = 0.095 = about 9.5%
</pre>
