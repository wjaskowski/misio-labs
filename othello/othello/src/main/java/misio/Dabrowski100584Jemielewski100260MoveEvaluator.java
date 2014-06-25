package misio;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

import com.google.common.base.Preconditions;

/**
 * @author Patryk D¹browski
 * @author Pawe³ Jemielewski
 */
public class Dabrowski100584Jemielewski100260MoveEvaluator implements
		BoardMoveEvaluator<OthelloBoard> {

	private double[] weights;
	private double[] evals;

	public Dabrowski100584Jemielewski100260MoveEvaluator() {
		initWeights();
	}

	private double calcUtility() {
		double utilit = 0.0;
		for (int i = 0; i < weights.length; i++) {
			utilit += weights[i] * evals[i];
		}
		return utilit;
	}

	@Override
	public double evaluateMove(final OthelloBoard board, final int move,
			final int player) {
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE
				|| player == Board.BLACK);

		final OthelloBoard newBoard = board.clone();
		newBoard.makeMove(move, player);

		evals[0] = Heuristic.coinParityHeuristic(newBoard, player);
		evals[1] = Heuristic.stabilityHeuristic(newBoard, player);
		evals[2] = Heuristic.oponentMobilityHeuristic(newBoard, player);
		evals[3] = Heuristic.fieldHeuristic(board, move, player);

		return calcUtility();
	}

	void normalize() {
		double sum = 0.0;
		for (double w : weights) {
			sum += w;
		}
		for (int i = 0; i < weights.length; i++) {
			weights[i] = weights[i] / sum;
		}
	}

	private void initWeights() {
		evals = new double[] { 0.0, 0.0, 0.0, 0.0 };
		weights = new double[] { 0.11764705882352942, 0.5882352941176471,
				-0.11764705882352942, 0.4117647058823529 };
	}
}
