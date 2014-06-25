package misio;

import java.util.HashMap;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class Surdyk101235MoveEvaluator implements
		BoardMoveEvaluator<OthelloBoard> {

	private HashMap<String, Double> weights = new HashMap<>();

	double discountFactor = 0.1;
	double learningSpeed = 0.1;

	Surdyk101235MoveEvaluator() {
		weights.put("myFFields", 185208.1110000198);
		weights.put("oponentCFields", 191876.23999993817);
		weights.put("oponentEFields", 191183.5499999663);
		weights.put("myBFields", 191175.59999996686);
		weights.put("myAFields", 191183.59999996627);
		weights.put("myEFields", 189358.8699999968);
		weights.put("oponentBFields", 191276.74999996147);
		weights.put("oponentDFields", 191370.25999996395);
		weights.put("oponentFFields", 194283.28899990948);
		weights.put("myDFields", 191049.48999996626);
		weights.put("myCFields", 191044.5399999711);
		weights.put("oponentAFields", 191542.51999994562);
	}

	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		int result = 0;
		OthelloBoard boardAfterMove = board.clone();
		boardAfterMove.makeMove(move, player);

		if (calculateNumberOfPawns(boardAfterMove, Board.WHITE) == 0) {
			return 100;
		}

		for (String s : weights.keySet()) {
			result += weights.get(s)
					* getFactor(s, board, boardAfterMove, move, player);
		}
		return result;
	}

	private int getFactor(String factorName, OthelloBoard board,
			OthelloBoard boardAfterMove, int move, int player) {

		if (factorName.equals("myAFields")) {
			return calculateAFields(boardAfterMove, Board.BLACK);
		}

		if (factorName.equals("oponentAFields")) {
			return calculateAFields(boardAfterMove, Board.WHITE);
		}

		if (factorName.equals("myBFields")) {
			return calculateBFields(boardAfterMove, Board.BLACK);
		}

		if (factorName.equals("oponentBFields")) {
			return calculateBFields(boardAfterMove, Board.WHITE);
		}

		if (factorName.equals("myCFields")) {
			return calculateCFields(boardAfterMove, Board.BLACK);
		}

		if (factorName.equals("oponentCFields")) {
			return calculateCFields(boardAfterMove, Board.WHITE);
		}

		if (factorName.equals("myDFields")) {
			return calculateDFields(boardAfterMove, Board.BLACK);
		}

		if (factorName.equals("oponentDFields")) {
			return calculateDFields(boardAfterMove, Board.WHITE);
		}

		if (factorName.equals("myEFields")) {
			return calculateEFields(boardAfterMove, Board.BLACK);
		}

		if (factorName.equals("oponentFFields")) {
			return calculateEFields(boardAfterMove, Board.WHITE);
		}

		if (factorName.equals("myFFields")) {
			return calculateFFields(boardAfterMove, Board.BLACK);
		}

		if (factorName.equals("oponentFFields")) {
			return calculateFFields(boardAfterMove, Board.WHITE);
		}

		return 0;
	}

	private int calculateAFields(OthelloBoard board, int player) {
		int result = 0;
		if (board.getValue(0, 0) == player) {
			result++;
		}
		if (board.getValue(0, 7) == player) {
			result++;
		}
		if (board.getValue(7, 0) == player) {
			result++;
		}
		if (board.getValue(7, 7) == player) {
			result++;
		}
		return result;
	}

	private int calculateBFields(OthelloBoard board, int player) {
		int result = 0;
		if (board.getValue(1, 0) == player) {
			result++;
		}
		if (board.getValue(0, 1) == player) {
			result++;
		}
		if (board.getValue(7, 8) == player) {
			result++;
		}
		if (board.getValue(8, 7) == player) {
			result++;
		}
		if (board.getValue(0, 7) == player) {
			result++;
		}
		if (board.getValue(1, 8) == player) {
			result++;
		}
		if (board.getValue(7, 0) == player) {
			result++;
		}
		if (board.getValue(8, 1) == player) {
			result++;
		}
		return result;
	}

	private int calculateCFields(OthelloBoard board, int player) {
		int result = 0;
		if (board.getValue(1, 1) == player) {
			result++;
		}
		if (board.getValue(1, 7) == player) {
			result++;
		}
		if (board.getValue(7, 1) == player) {
			result++;
		}
		if (board.getValue(7, 7) == player) {
			result++;
		}
		return result;
	}

	private int calculateDFields(OthelloBoard board, int player) {
		int result = 0;
		for (int i = 2; i < 6; i++) {
			if (board.getValue(i, 0) == player) {
				result++;
			}
			if (board.getValue(i, 8) == player) {
				result++;
			}
			if (board.getValue(0, i) == player) {
				result++;
			}
			if (board.getValue(8, i) == player) {
				result++;
			}
		}
		return result;
	}

	private int calculateEFields(OthelloBoard board, int player) {
		int result = 0;
		for (int i = 2; i < 6; i++) {
			if (board.getValue(i, 1) == player) {
				result++;
			}
			if (board.getValue(i, 7) == player) {
				result++;
			}
			if (board.getValue(1, i) == player) {
				result++;
			}
			if (board.getValue(7, i) == player) {
				result++;
			}
		}
		return result;
	}

	private int calculateFFields(OthelloBoard board, int player) {
		int result = 0;
		for (int i = 2; i < 6; i++) {
			for (int j = 2; j < 6; j++) {
				if (board.getValue(i, j) == player) {
					result++;
				}
			}
		}
		return result;
	}

	private int calculateNumberOfPawns(OthelloBoard board, int player) {
		int result = 0;
		for (int i = 0; i < board.getHeight(); i++) {
			for (int j = 0; j < board.getWidth(); j++) {
				if (board.getValue(i, j) == player) {
					result++;
				}
			}
		}
		return result;
	}

}
