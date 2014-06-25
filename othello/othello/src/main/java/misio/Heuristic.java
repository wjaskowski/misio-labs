package misio;

import java.util.List;

import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.OthelloBoard;

public final class Heuristic {
	public static double coinParityHeuristic(final OthelloBoard newBoard,
			final int player) {
		final double black = BoardUtils.countPieces(newBoard, player);
		final double white = BoardUtils.countPieces(newBoard,
				OthelloBoard.opponent(player));
		return black / (black + white);
	}

	public static double fieldHeuristic(final OthelloBoard board,
			final int move, final int player) {
		if ((move == 12 || move == 21) && board.getValue(11) != player) {
			return 0.01;
		}
		if (move == 28 || move == 17) {
			if (board.getValue(18) != player) {
				return 0.01;
			}
		}
		if (move == 71 || move == 82) {
			if (board.getValue(81) != player) {
				return 0.01;
			}
		}
		if (move == 78 || move == 87) {
			if (board.getValue(88) != player) {
				return 0.01;
			}
		}

		if (move == 22) {
			if (board.getValue(11) != player) {
				return 0.005;
			}
		}
		if (move == 77) {
			if (board.getValue(88) != player) {
				return 0.005;
			}
		}
		if (move == 27) {
			if (board.getValue(18) != player) {
				return 0.005;
			}
		}
		if (move == 72) {
			if (board.getValue(81) != player) {
				return 0.005;
			}
		}
		return 1.0;
	}

	private static int[][] margin(final OthelloBoard newBoard,
			final int player, final int[][] s) {
		for (int i = 0; i < OthelloBoard.SIZE; i++) {
			if (newBoard.getValue(i, 0) == player) {
				s[i][0] = 1;
			} else {
				break;
			}
		}

		for (int i = 0; i < OthelloBoard.SIZE; i++) {
			if (newBoard.getValue(0, i) == player) {
				s[0][i] = 1;
			} else {
				break;
			}
		}

		for (int i = 0; i < OthelloBoard.SIZE; i++) {
			if (newBoard.getValue(7, i) == player) {
				s[7][i] = 1;
			} else {
				break;
			}
		}

		for (int i = 0; i < OthelloBoard.SIZE; i++) {
			if (newBoard.getValue(i, 7) == player) {
				s[i][7] = 1;
			} else {
				break;
			}
		}

		for (int i = 7; i >= 0; i--) {
			if (newBoard.getValue(i, 0) == player) {
				s[i][0] = 1;
			} else {
				break;
			}
		}

		for (int i = 7; i > 0; i--) {
			if (newBoard.getValue(0, i) == player) {
				s[0][i] = 1;
			} else {
				break;
			}
		}

		for (int i = 7; i >= 0; i--) {
			if (newBoard.getValue(i, 7) == player) {
				s[i][7] = 1;
			} else {
				break;
			}
		}

		for (int i = 7; i >= 0; i--) {
			if (newBoard.getValue(7, i) == player) {
				s[7][i] = 1;
			} else {
				break;
			}
		}
		return s;
	}

	public static double oponentMobilityHeuristic(final OthelloBoard newBoard,
			final int player) {
		return Utills.getPossibleMoves(newBoard, OthelloBoard.opponent(player))
				.size();
	}

	public static double opponentBadMoveHeuristic(final OthelloBoard newBoard,
			final int player) {
		final List<Integer> l = Utills.getPossibleMoves(newBoard,
				OthelloBoard.opponent(player));
		double sum = 0.0;
		for (final Integer m : l) {
			if (m.intValue() == 12 || m.intValue() == 21 || m.intValue() == 22
					|| m.intValue() == 28 || m.intValue() == 82
					|| m.intValue() == 78 || m.intValue() == 87
					|| m.intValue() == 17 || m.intValue() == 77
					|| m.intValue() == 27 || m.intValue() == 72
					|| m.intValue() == 71) {
				sum += 1.0;
			}
		}
		return sum / new Integer(l.size()).doubleValue();
	}

	public static double stabilityHeuristic(final OthelloBoard newBoard,
			final int player) {
		int sum = 0;
		int[][] s = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };

		s = margin(newBoard, player, s);

		for (int i = 1; i < OthelloBoard.SIZE - 1; i++) {
			for (int j = 1; j < OthelloBoard.SIZE - 1; j++) {

				final int p = newBoard.getValue(i, j);
				if (s[i][j - 1] == 1 && s[i - 1][j] == 1 && p == player) {
					s[i][j] = 1;
				} else if (s[i][j - 1] == 1 && s[i + 1][j] == 1 && p == player) {
					s[i][j] = 1;
				} else if (s[i][j + 1] == 1 && s[i - 1][j] == 1 && p == player) {
					s[i][j] = 1;
				} else if (s[i][j + 1] == 1 && s[i + 1][j] == 1 && p == player) {
					s[i][j] = 1;
				} else {
				}
			}
		}

		for (final int[] is : s) {
			for (final int element : is) {
				sum += element;
			}
		}
		return sum / 64.0;
	}
}
