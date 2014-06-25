package misio;

import java.awt.Point;
import java.math.BigDecimal;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.google.common.base.Preconditions;

public class Zjawin98826Wasikowski101560MoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {

	public static final int HEURISTIC_NUM = 3;
	public static final boolean IS_TRAINING = false;
	
	private double[] inputs;
	private double[] weights;

	public Zjawin98826Wasikowski101560MoveEvaluator(RandomDataGenerator random) {
		inputs = new double[HEURISTIC_NUM];
		
		if (!IS_TRAINING) {			
			weights = new double[] {
					-0.036741670540182409798735108097389456816017627716064453125,
					0.44541258180749476736082215211354196071624755859375,
					0.38067968772479765515726057856227271258831024169921875
			};
		} else {
			weights = new double[HEURISTIC_NUM];
			for (int i=0; i<weights.length; i++) {
				weights[i] = random.nextUniform(-0.5, 0.5);
			}
		}
	}

	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE
				|| player == Board.BLACK);

		OthelloBoard currMoveBoard = board.clone();
		currMoveBoard.makeMove(move, player);
		
		double[] mobilityResult = mobilityHeuristic(currMoveBoard, player);
		double[] stabilityResult = stabilityHeuristic(currMoveBoard, player);
		double[] wpcResult = wpcHeuristic(currMoveBoard, player);		
		
		inputs[0] = mobilityResult[0];
		inputs[1]= stabilityResult[0];
		inputs[2] = wpcResult[0];
		
		return getEval(inputs);
	}
	
	public double getEval(double [] inputs) {
		double eval = 0.0;
		for (int i=0; i<inputs.length; i++) {
			eval += inputs[i] * weights[i];
		}
		
		return eval;
	}
	
	public void printWeights() {
		for (int i=0; i<weights.length - 1; i++) {
			BigDecimal bd = new BigDecimal(weights[i]);
			System.out.print(bd.toString());
			System.out.print(",\n");
		}
		BigDecimal bd = new BigDecimal(weights[weights.length-1]);
		System.out.println(bd.toString());
	}

	public double[] coinParityHeuristic(OthelloBoard board, int player) {
		int oppPlayer = OthelloBoard.opponent(player);
		int playerCoinsNum = 0, oppCoinsNum = 0;
		for (int i = 0; i < board.getHeight(); i++) {
			for (int j = 0; j < board.getWidth(); j++) {
				int field = board.getValue(i, j);
				if (field == player)
					playerCoinsNum += 1;
				else if (field == oppPlayer)
					oppCoinsNum += 1;
			}
		}

		return new double [] {
				(double) playerCoinsNum / 64,
				(double) oppCoinsNum / 64
		};
	}

	public double[] cornerHeuristic(OthelloBoard board, int player) {
		int playerCorners = 0;
		int opponentCorners = 0;

		if (board.getValue(11) == player)
			playerCorners++;
		else if (board.getValue(11) == OthelloBoard.opponent(player))
			opponentCorners++;
		if (board.getValue(18) == player)
			playerCorners++;
		else if (board.getValue(18) == OthelloBoard.opponent(player))
			opponentCorners++;
		if (board.getValue(81) == player)
			playerCorners++;
		else if (board.getValue(81) == OthelloBoard.opponent(player))
			opponentCorners++;
		if (board.getValue(88) == player)
			playerCorners++;
		else if (board.getValue(88) == OthelloBoard.opponent(player))
			opponentCorners++;
		
		return new double [] {
				(double) playerCorners / 4,
				(double) opponentCorners / 4
		};		
	}

	public double[] mobilityHeuristic(OthelloBoard board, int player) {
		int[] playerMoves = getValidMoves(board, player,
				getPossibleMoves(board));
		int[] oponentMoves = getValidMoves(board,
				OthelloBoard.opponent(player), getPossibleMoves(board));

		int playerMobility = playerMoves.length;
		int oponentMobility = oponentMoves.length;
		
		return new double [] {
				Math.min((double) playerMobility / 30, 1.0),
				Math.min((double) oponentMobility / 30, 1.0)
		};
	}

	public double[] possibleMobilityHeuristic(OthelloBoard board, int player) {
		int[] playerMoves = getValidMoves(board, player,
				getPossibleOpponentMoves(board, player));
		int[] oponentMoves = getValidMoves(board,
				OthelloBoard.opponent(player),
				getPossibleOpponentMoves(board, OthelloBoard.opponent(player)));

		int playerMobility = playerMoves.length;
		int oponentMobility = oponentMoves.length;

		return new double [] {
				Math.min((double) playerMobility / 5, 1.0),
				Math.min((double) oponentMobility / 5, 1.0)
		};
	}

	public double[] stabilityHeuristic(OthelloBoard board, int player) {
		int oppPlayer = OthelloBoard.opponent(player);

		int[][] stabTab = new int[OthelloBoard.NUM_FIELDS][OthelloBoard.NUM_FIELDS];
		for (int i = 0; i < stabTab.length; i++) {
			for (int j = 0; j < stabTab[i].length; j++) {
				stabTab[i][j] = Board.EMPTY;
			}
		}
		int stableCurrPlayerCoinsNums = 0, stableOppPlayerCoinsNums = 0;

		// search for stable number
		Point corners[] = { new Point(0, 0),
				new Point(board.getWidth() - 1, 0),
				new Point(0, board.getHeight() - 1),
				new Point(board.getWidth() - 1, board.getHeight() - 1) };
		for (int cornerIdx = 0; cornerIdx < 4; cornerIdx++) {
			Point currCorner = corners[cornerIdx];
			int currColor = board.getValue(currCorner.y, currCorner.x);
			if (currColor == Board.EMPTY)
				continue;

			for (int cornerSearchIdx = 0; cornerSearchIdx < 2; cornerSearchIdx++) {
				int offsetX, offsetY;
				if (cornerIdx == 0) {
					offsetX = 1;
					offsetY = 1;
				} else if (cornerIdx == 1) {
					offsetX = -1;
					offsetY = 1;
				} else if (cornerIdx == 2) {
					offsetX = 1;
					offsetY = -1;
				} else {
					offsetX = -1;
					offsetY = -1;
				}
				if (cornerSearchIdx == 0) {
					int searchLength = board.getWidth();
					int y = currCorner.y;
					while (searchLength > 0 && y < board.getHeight() && y >= 0) {
						for (int i = 0; i < searchLength; i++) {
							int x = currCorner.x + offsetX * i;
							if (stabTab[y][x] == currColor)
								continue;
							if (board.getValue(y, x) != currColor) {
								searchLength = i - 1;
								break;
							}
							stabTab[y][x] = currColor;
							if (currColor == player)
								stableCurrPlayerCoinsNums++;
							else if (currColor == oppPlayer)
								stableOppPlayerCoinsNums++;
						}
						y += offsetY;
					}
				} else {
					int searchLength = board.getHeight();
					int x = currCorner.x;
					while (searchLength > 0 && x < board.getWidth() && x >= 0) {
						for (int i = 0; i < searchLength; i++) {
							int y = currCorner.y + offsetY * i;
							if (stabTab[y][x] == currColor)
								continue;
							if (board.getValue(y, x) != currColor) {
								searchLength = i - 1;
								break;
							}
							stabTab[y][x] = currColor;
							if (currColor == player)
								stableCurrPlayerCoinsNums++;
							else if (currColor == oppPlayer)
								stableOppPlayerCoinsNums++;
						}
						x += offsetX;
					}
				}
			}
		}
		
		return new double [] {
				(double)stableCurrPlayerCoinsNums / 64,
				(double)stableOppPlayerCoinsNums / 64
		};
	}
	
	public double[] wpcHeuristic(OthelloBoard board, int player) {
		double [][] wpcEvals = new double [][] {
			{100, -25, 10, 5, 5, 10, -25, 100},
			{-25, -25, 2, 2, 2, 2, -25, -25},
			{10, 2, 5, 1, 1, 5, 2, 10},
			{5, 2, 1, 2, 2, 1, 2, 5},
			{5, 2, 1, 2, 2, 1, 2, 5},
			{10, 2, 5, 1, 1, 5, 2, 10},
			{-25, -25, 2, 2, 2, 2, -25, -25},
			{100, -25, 10, 5, 5, 10, -25, 100}
		};
		
		int oppPlayer = OthelloBoard.opponent(player);
		
		double playerEval = 0;
		double oppPlayerEval = 0;
		for (int i=0; i<board.getHeight(); i++) {
			for (int j=0; j<board.getWidth(); j++) {
				int fieldValue = board.getValue(i, j);
				if (fieldValue == player) {
					playerEval += wpcEvals[i][j];
				} else if (fieldValue == oppPlayer) {
					oppPlayerEval += wpcEvals[i][j];
				}
			}
		}
		
		return new double [] {
				(double)playerEval / 588,
				(double)oppPlayerEval / 588
		};
	}

	private static boolean isInBoard(OthelloBoard board, int pos) {
		int wall = -2;
		return board.buffer[pos] != wall;
	}

	private static IntDoubleLinkedSet getPossibleMoves(OthelloBoard board) {
		IntDoubleLinkedSet moves = new IntDoubleLinkedSet(
				OthelloBoard.BUFFER_SIZE, OthelloBoard.BUFFER_SIZE);
		for (int pos = 0; pos < board.buffer.length; pos++) {
			if (!board.isEmpty(pos)) {
				continue;
			}
			for (int dir : OthelloBoard.DIRS) {
				int neighbour = pos + dir;
				if (!board.isEmpty(neighbour) && isInBoard(board, neighbour)) {
					moves.add(pos);
					break;
				}
			}
		}
		return moves;
	}

	private static int[] getValidMoves(OthelloBoard board, int color,
			IntDoubleLinkedSet possibleMoves) {
		IntArrayList validMoves = new IntArrayList(possibleMoves.size());
		for (int i = 0; i < possibleMoves.size(); ++i) {
			if (board.isValidMove(possibleMoves.dense[i], color)) {
				validMoves.add(possibleMoves.dense[i]);
			}
		}
		return validMoves.toArray();
	}

	private static IntDoubleLinkedSet getPossibleOpponentMoves(
			OthelloBoard board, int player) {
		IntDoubleLinkedSet moves = new IntDoubleLinkedSet(
				OthelloBoard.BUFFER_SIZE, OthelloBoard.BUFFER_SIZE);
		for (int pos = 0; pos < board.buffer.length; pos++) {
			if (!board.isEmpty(pos)) {
				continue;
			}
			for (int dir : OthelloBoard.DIRS) {
				int neighbour = pos + dir;
				if (!board.isEmpty(neighbour)
						&& neighbour == OthelloBoard.opponent(player)) {
					moves.add(pos);
					break;
				}
			}
		}
		return moves;
	}

	public double[] getInputs() {
		return inputs;
	}

	public double[] getWeights() {
		return weights;
	}
	
	public void setWeight(int index, double value) {
		weights[index] = value;
	}
	
}
