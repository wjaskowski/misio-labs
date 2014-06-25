package misio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

import com.google.common.base.Preconditions;

public class Pralat99975Rybicki99961MoveEvaluator implements Serializable,
		BoardMoveEvaluator<OthelloBoard> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7316128356963768298L;

	private RandomDataGenerator random;

	private NTuple[] tuples;
	private int numTuples = 12;
	private int spaceSize = 8;
	private int tupleArity = 6;
	private int numValues = 3;
	private double initRange = 0;
	
	private static final String NTUPLES_FILE_NAME = "NTUPLES";
	
	/**
	 * Directions on a 2D board represented by a "flat" array
	 */

	public NTuple[] getTuples() {
		return tuples;
	}

	public void setTuples(NTuple[] tuples) {
		this.tuples = tuples;
	}

	public Pralat99975Rybicki99961MoveEvaluator(RandomDataGenerator random) {
		this.random = random;

		double[][] weights = new double[numTuples][];
		int[][] positions = new int[numTuples][];
		int maxPosition = spaceSize * spaceSize;

		ObjectSerializator s = new ObjectSerializator();
		
		try {
			tuples = (NTuple[]) s
					.readObject(NTUPLES_FILE_NAME);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 static public void writeObject(Object object, String filename) throws IOException {
	        ObjectOutputStream stream = new ObjectOutputStream(       
	                new GZIPOutputStream(
	                        new FileOutputStream(filename)));
	        
	        stream.writeObject(object);
	        stream.close();
	    }

	public void updateEvaluationFunction(OthelloBoard previousBoard,
			OthelloBoard board, GameOutcome outcome) {
		double evalBefore = Math.tanh(evaluate(previousBoard));

		double evalAfter = Math.tanh(evaluate(board));
		double derivative = (1 - (evalBefore * evalBefore));
		if (outcome != null) {
			evalAfter = outcome.blackPlayerPoints;
		}

		double error = evalAfter - evalBefore;

		TDLUpdate(previousBoard, 0.001 * error * derivative);

	}

	public void TDLUpdate(Board previous, double delta) {
		for (NTuple tuple : tuples) {
			tuple.updateWeights(previous, delta);
		}
	}

	public double evaluate(Board board) {
		double result = 0;
		for (NTuple tuple : tuples) {
			result += tuple.value(board);
		}
		return result;
	}

	public class OthelloSymmetryExpander {

		int N = 10;
		int M = N - 1;

		public int[] getSymmetries(int location) {
			int x = location % N;
			int y = location / N;

			int[] a = new int[8];
			a[0] = (flat(x, y));
			a[1] = (flat(M - x, y));
			a[2] = (flat(x, M - y));
			a[3] = (flat(M - x, M - y));
			a[4] = (flat(y, x));
			a[5] = (flat(M - y, x));
			a[6] = (flat(y, M - x));
			a[7] = (flat(M - y, M - x));

			return a;
		}

		public int flat(int x, int y) {
			return x + N * y;
		}
	}

	class NTuple implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static final int VALUES = 3;

		private int n;

		private double[] lut;

		private double[] traces;

		private int[][] symmetricPositions;

		private NTuple() {
		}

		public NTuple(int[] locations, OthelloSymmetryExpander expander) {
			this(locations,
					new double[(int) Math.pow(VALUES, locations.length)],
					expander);
		}

		public NTuple(int[] locations, double[] weights,
				OthelloSymmetryExpander expander) {
			n = locations.length;
			lut = weights;

			int iterator = 0;
			int[][] symmetries = new int[locations.length][];
			for (int location : locations) {
				symmetries[iterator++] = expander.getSymmetries(location);
			}

			int numSymmetries = symmetries[0].length;
			symmetricPositions = new int[numSymmetries][n];
			for (int i = 0; i < numSymmetries; i++) {
				for (int j = 0; j < n; j++) {
					symmetricPositions[i][j] = symmetries[j][i];
				}
			}
		}

		public double value(Board board) {
			double result = 0;
			for (int[] tuple : symmetricPositions) {
				result += lut[address(tuple, board)];
			}
			return result;
		}

		private int address(int[] tuple, Board board) {
			int result = 0;
			for (int location : tuple) {
				result *= VALUES;
				result += (board.getValue(location));
			}
			return result;
		}

		public void updateWeights(Board previous, double delta) {
			for (int[] tuple : symmetricPositions) {
				lut[address(tuple, previous)] += delta;
			}
		}

	}

	/**
	 * @param move
	 *            is a value rc, where (1 <= r,c <= 8), e.g. 23 means row=2,
	 *            col=3
	 * @player is a player I'm playing with
	 * @board is an Othello board with Board.WHITE, Board.BLACK or Board.EMPTY
	 *        pieces
	 */
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE
				|| player == Board.BLACK);
		OthelloBoard bCopy = board.clone();
		bCopy.makeMove(move, player);

		// I should return the evaluation (the utility) of the move. The higher
		// the better.

		return evaluate(bCopy);

	}

	public class ObjectSerializator {

		public void writeObject(Object object, String filename)
				throws IOException {
			ObjectOutputStream stream = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(filename)));

			stream.writeObject(object);
			stream.close();
		}

		public Object readObject(String filename) throws IOException,
				ClassNotFoundException {
			ObjectInputStream stream = new ObjectInputStream(
					new GZIPInputStream(new FileInputStream(filename)));

			Object object = stream.readObject();
			stream.close();
			return object;
		}

	}
}
