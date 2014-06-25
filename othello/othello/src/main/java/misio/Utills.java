package misio;

import java.util.ArrayList;
import java.util.List;

import put.ci.cevo.games.othello.OthelloBoard;

public final class Utills {
	public static double[] toDoubleArray(final List<Double> list) {
		final double[] ret = new double[list.size()];
		int i = 0;
		for (final Double e : list) {
			ret[i++] = e.doubleValue();
		}
		return ret;
	}

	public static int[] toIntArray(final List<Integer> list) {
		final int[] ret = new int[list.size()];
		int i = 0;
		for (final Integer e : list) {
			ret[i++] = e.intValue();
		}
		return ret;
	}

	public static List<Integer> getPossibleMoves(final OthelloBoard board,
			final int color) {
		final List<Integer> moves = new ArrayList<>();
		for (int pos = 11; pos <= 88; pos++) {
			if (!board.isEmpty(pos)) {
				continue;
			}
			for (final int dir : OthelloBoard.DIRS) {
				final int neighbour = pos + dir;
				if (!board.isEmpty(neighbour) && isInBoard(neighbour, board)) {
					// moves.add(pos);
					if (board.isValidMove(pos, color)) {
						moves.add(pos);
					}
					break;
				}
			}
		}
		return moves;
	}

	public static boolean isInBoard(final int pos, final OthelloBoard board) {
		return board.buffer[pos] != -2;
	}
}
