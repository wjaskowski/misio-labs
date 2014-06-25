package misio;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

import com.google.common.base.Preconditions;

public class Poszwa88405MoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {
	public Poszwa88405BoardEvaluator boardEvaluator;

	public Poszwa88405MoveEvaluator(double alpha) {
		this.boardEvaluator = new Poszwa88405BoardEvaluator(alpha);
	}

	/**
	 * @param move is a value rc, where (1 <= r,c <= 8), e.g. 23 means row=2, col=3 
	 * @player is a player I'm playing with
	 * @board is an Othello board with Board.WHITE, Board.BLACK or Board.EMPTY pieces
	 */
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE || player == Board.BLACK);

		board = board.clone();
		board.makeMove(move, player);
		return boardEvaluator.evaluateBoard(board, player);
	}
}
