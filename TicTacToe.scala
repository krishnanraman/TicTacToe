import com.twitter.scalding._

class TicTacToe(args:Args) extends Job(args) {
  val n = args("n").toInt
  val show = args("show").toBoolean

  val gamesAndMoves = IterableSource(1 to n, 'times)
  .read
  .mapTo('times -> ('game, 'nummoves)) { x:Int => TicTacToe.playGame(show) }

  gamesAndMoves.groupBy('nummoves) { _.size }
  .groupAll {
    _.sortedReverseTake[(Int, Int)](('size, 'nummoves) -> 'top, 10)
  }.map('top -> 'top){
    x:List[(Int, Int)] =>
    ("%5s\t%5s".format("Count", "Game") :: x.map( i => "%5d\t%5d".format( i._1, i._2 ))).mkString("\n")
  }.write(Tsv("tictactoe" + n))

  for( moves <- Seq(5,6,7,8,9,-1)) {
    gamesAndMoves.filter('nummoves){ x:Int => x == moves }
    .groupBy('game) { _.size }
    .groupAll {
      _.sortedReverseTake[(Int, String)](('size, 'game) -> 'top, 10000)
    }.map('top -> 'top){
      x:List[(Int, String)] =>
      ("%5s\t%s".format("Count", "Game") :: x.map( i => "%5d\t%s".format( i._1, i._2 ))).mkString("\n")
    }.write(Tsv("tictactoe"+moves + "moves" + n))
  }
}

object TicTacToe {

  type C = Char
  type Board = Seq[C]
  type Boards = Seq[Board]
  val empty = Seq.fill[C](9)('_')
  val rnd = {
    val x = util.Random
    x.setSeed(123456) // want the same sequence of pseudorandom numbers
    x
  }

  def printBoard(board:Board) = Seq(0,3,6).map(i => Seq(i,i+1,i+2).map(board(_)).mkString(" ")+"\n").mkString

  def winner(board:Board) = Seq((0,1,2),(3,4,5), (6,7,8), (0,3,6),(1,4,7), (2,5,8), (0,4,8), (2,4,6))
    .map{ i =>
      val tics = Seq(i._1,i._2,i._3).map(board(_))
      if (tics.count(_=='x')==3) 'x' else if (tics.count(_=='o')==3) 'o' else '_'
  }.filterNot(_=='_').headOption

  def play(player:C, board:Board):Board = {
    val pos = rnd.nextInt(9)
    if (board(pos) == '_') board.updated(pos,player) else play(player, board)
  }

  def playGame(show:Boolean):(String,Int) = {

    val players = {
      val (first, second) = if (rnd.nextDouble < 0.5) ('x','o') else ('o', 'x')
      Seq.tabulate[C](9)(i=> if (i%2==0) first else second)
    }

    val allMoves = players.foldLeft(Seq(empty))((board,player)=> Seq(play(player,board.head)) ++ board).reverse
    val whoWon = allMoves.indexWhere(winner(_).isDefined)
    val game = if(whoWon == -1) allMoves else allMoves.slice(0, 1 + whoWon)
    val gameStr = game.map(_.mkString).mkString("~")
    val numMovesToWin = if( whoWon != -1) { game.size - 1 /* since first move is a blank board */ } else -1
    if( show) {
      game.zipWithIndex.foreach{ i=> val(board,move)=i; println("\nMove:"+move); print(printBoard(board)) }
      if( whoWon != -1) println("We have a winner! " + winner(allMoves(whoWon)).get + " wins in " + numMovesToWin + " moves!")
    }
    (gameStr, numMovesToWin )
  }
}
