package misio;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class ExampleMoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {
	
	private RandomDataGenerator random;

	public ExampleMoveEvaluator(RandomDataGenerator random) {
		this.random = random;
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

		// I should return the evaluation (the utility) of the move. The higher the better.
		return random.nextUniform(-100.0, 100.0);
	}
}
