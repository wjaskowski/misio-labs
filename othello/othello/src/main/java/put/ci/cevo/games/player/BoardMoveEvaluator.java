package put.ci.cevo.games.player;

import put.ci.cevo.games.board.Board;

public interface BoardMoveEvaluator<T extends Board> {

	public final static double INVALID_MOVE = Double.NEGATIVE_INFINITY;

	/**
	 * Should return <code>INVALID_MOVE</code> if the move is invalid
	 */
	public double evaluateMove(T board, int move, int player);
}
