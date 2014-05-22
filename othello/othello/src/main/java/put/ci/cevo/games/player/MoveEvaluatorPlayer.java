package put.ci.cevo.games.player;

import static put.ci.cevo.games.board.BoardEvaluationType.BOARD_INVERSION;
import static put.ci.cevo.games.board.BoardEvaluationType.OUTPUT_NEGATION;
import static put.ci.cevo.games.othello.OthelloBoard.opponent;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.util.ArrayUtils;
import put.ci.cevo.util.RandomUtils;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.base.Preconditions;

/**
 * A player that evaluates each move and selects the one with the highest score or a random one in case of equal scores
 * 
 * @author Wojciech Jaśkowski
 */
public final class MoveEvaluatorPlayer<T extends Board> implements BoardGamePlayer<T> {

	private static final double EPSILON = 0.000001;

	private final BoardMoveEvaluator<T> moveEvaluator;
	private final BoardEvaluationType boardEvaluationType;

	private final double randomMoveProbability;

	public MoveEvaluatorPlayer(BoardMoveEvaluator<T> moveEvaluator) {
		this(moveEvaluator, 0.0, OUTPUT_NEGATION);
	}

	public MoveEvaluatorPlayer(BoardMoveEvaluator<T> moveEvaluator, double randomMoveProbability) {
		this(moveEvaluator, randomMoveProbability, OUTPUT_NEGATION);
	}

	public MoveEvaluatorPlayer(BoardMoveEvaluator<T> moveEvaluator, BoardEvaluationType boardEvaluationType) {
		this(moveEvaluator, 0.0, boardEvaluationType);
	}

	public MoveEvaluatorPlayer(BoardMoveEvaluator<T> moveEvaluator, double randomMoveProbability,
			BoardEvaluationType boardEvaluationType) {
		Preconditions.checkArgument(0.0 <= randomMoveProbability && randomMoveProbability <= 1.0);
		this.moveEvaluator = moveEvaluator;
		this.randomMoveProbability = randomMoveProbability;
		this.boardEvaluationType = boardEvaluationType;
	}

	@Override
	public int getMove(T board, int player, int[] validMoves, RandomDataGenerator random) {
		Preconditions.checkArgument(validMoves.length > 0);
		boolean inverted = false;
		if (boardEvaluationType == BOARD_INVERSION && player == Board.WHITE) {
			board.invert();
			player = opponent(player);
			inverted = true;
		}

		// Whether should I make a random move
		boolean chooseRandomMove = false;
		if (randomMoveProbability > 0.0) {
			if (random.nextUniform(0.0, 1.0) < randomMoveProbability) {
				chooseRandomMove = true;
			}
		}

		double bestEval = Double.NEGATIVE_INFINITY;
		final IntArrayList bestMoves = new IntArrayList();

		// Sorting moves is not required, but I needed it for comparison with old implementation
		int[] sortedPossibleMoves = ArrayUtils.sorted(validMoves);
		for (int move : sortedPossibleMoves) {
			double eval = moveEvaluator.evaluateMove(board, move, player);

			// bestMoves equals all valid moves if chooseRandomMove

			// EPSILON is harmless, but it prevents "nondeterminism" from numerical errors (gives an opportunity to
			// compare two implementations in a robust way)
			if (chooseRandomMove || eval == bestEval || Math.abs(eval - bestEval) < EPSILON) {
				bestMoves.add(move);
			} else if (bestEval < eval) {
				bestEval = eval;
				bestMoves.clear();
				bestMoves.add(move);
			}
		}

		if (inverted) {
			board.invert();
		}

		return RandomUtils.pickRandom(bestMoves.toArray(), random);
	}

	public BoardMoveEvaluator<T> getMoveEvaluator() {
		return moveEvaluator;
	}
}
