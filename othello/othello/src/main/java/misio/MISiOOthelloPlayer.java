package misio;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.player.BoardMoveEvaluator;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;

public class MISiOOthelloPlayer implements OthelloPlayer {

	private final MoveEvaluatorPlayer<OthelloBoard> evaluatorPlayer;
	private final double RANDOM_MOVE_PROBABILITY = 0.1;

	public MISiOOthelloPlayer(BoardMoveEvaluator<OthelloBoard> moveEvaluator) {
		evaluatorPlayer = new MoveEvaluatorPlayer<>(moveEvaluator, RANDOM_MOVE_PROBABILITY, BoardEvaluationType.BOARD_INVERSION);
	}

	@Override
	public int getMove(OthelloBoard board, int player, int[] possibleMoves, RandomDataGenerator random) {
		return evaluatorPlayer.getMove(board, player, possibleMoves, random);
	}
}
