package misio;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.google.common.base.Preconditions;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.Othello;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class Grzyb100998MoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {
	
	public Grzyb100998MoveEvaluator() 
	{
	}
	
	// Rewards in terminating state
	public static double winReward = 1;
	public static double lossReward = -1;
	public static double drawReward = 0.5;
	
	// Q value structure
	public static boolean weightsInitialized = false;
	public static double learningRate = 0.2;
	public static double[][] weights = new double[OthelloBoard.SIZE][OthelloBoard.SIZE];
	public static ArrayList<Double> previousQValues = new ArrayList<Double>();
	public static ArrayList<Integer> previousMoves = new ArrayList<Integer>();
	public static double previousQValue = 0.0;
	public static int previousMove = 0;
	
	double numberOfPointsWeight = 1.0;
	double cornerStrategyWeight = 30.0;
	public static double[][] cornerStrategy = {{1.00, -0.25, 0.10, 0.05, 0.05, 0.10, -0.25, 1.00}, 
											{-0.25, -0.25, 0.02, 0.02, 0.02, 0.02, -0.25, -0.25},
											{0.10, 0.02, 0.05, 0.01, 0.01, 0.05, 0.02, 0.10},
											{0.05, 0.02, 0.01, 0.02, 0.02, 0.01, 0.02, 0.05},
											{0.05, 0.02, 0.01, 0.02, 0.02, 0.01, 0.02, 0.05},
											{0.10, 0.02, 0.05, 0.01, 0.01, 0.05, 0.02, 0.10},
											{-0.25, -0.25, 0.02, 0.02, 0.02, 0.02, -0.25, -0.25},
											{1.00, -0.25, 0.10, 0.05, 0.05, 0.10, -0.25, 1.00}};
	
	public double calculateQValue(OthelloBoard board, int move, int player) 
	{
		double qValue = 0.0;
		if (!weightsInitialized) {
//			for (int row = 0; row < OthelloBoard.SIZE; row++) {
//				for (int col = 0; col < OthelloBoard.SIZE; col++) {
//					weights[row][col] = Math.random() - 0.5;
//					weights[row][col] = 0.0;
//				}
//			}
			weightsInitialized = true;
		}
		OthelloBoard boardCopy = board.clone();
		boardCopy.makeMove(move, player);
		double cornerStrategyValue = 0.0;
		for (int row = 0; row < OthelloBoard.SIZE; row++) {
			for (int col = 0; col < OthelloBoard.SIZE; col++) {
				cornerStrategyValue += cornerStrategy[row][col] * (boardCopy.getValue(row, col) - 1);
			}
		}
		
		double numberOfPoints = BoardUtils.countPieces(boardCopy, player);
		qValue = numberOfPoints * numberOfPointsWeight + cornerStrategyValue * cornerStrategyWeight;
		
		return qValue;
	}
	
	public void updateWeights(double maxQValue, double reward, int player) 
	{
//		for (int row = 0; row < OthelloBoard.SIZE; row++) {
//			for (int col = 0; col < OthelloBoard.SIZE; col++) {
//				weights[row][col] = weights[row][col] + learningRate * (reward + maxQValue - previousQ) * previousState[row][col];
//			}
//		}
		OthelloBoard boardCopy = twoTurnBehindBoard.clone();
		boardCopy.makeMove(previousMove, player);
		int pieces = BoardUtils.countPieces(boardCopy, player);
		
		int cornerStrategyValue = 0;
		for (int row = 0; row < OthelloBoard.SIZE; row++) {
			for (int col = 0; col < OthelloBoard.SIZE; col++) {
				cornerStrategyValue += cornerStrategy[row][col] * (boardCopy.getValue(row, col) - 1);
			}
		}
		

		numberOfPointsWeight = numberOfPointsWeight + learningRate * (reward + maxQValue - previousQValue) * pieces;
		cornerStrategyWeight = cornerStrategyWeight + learningRate * (reward + maxQValue - previousQValue) * cornerStrategyValue;
	}
	
	// State management
	public int[][] currentState = new int[OthelloBoard.SIZE][OthelloBoard.SIZE];
	public int[][] previousState = new int[OthelloBoard.SIZE][OthelloBoard.SIZE];
	public OthelloBoard twoTurnBehindBoard;
	public OthelloBoard oneTurnBehindBoard;
	
	public static boolean isFirstMove = true;
	
	public void stateFromBoard(OthelloBoard board, int[][] state) 
	{
		for (int row = 0; row < OthelloBoard.SIZE; row++) {
			for (int col = 0; col < OthelloBoard.SIZE; col++) {
				state[row][col] = board.getValue(row, col);
			}
		}
	}
	
	public void createPreviousState() 
	{
		for (int row = 0; row < OthelloBoard.SIZE; row++) {
			for (int col = 0; col < OthelloBoard.SIZE; col++) {
				previousState[row][col] = currentState[row][col];
			}
		}
	}
	
	public boolean stateChanged() 
	{
		boolean stateChanged = false;
		for (int row = 0; row < OthelloBoard.SIZE; row++) {
			for (int col = 0; col < OthelloBoard.SIZE; col++) {
				if (previousState[row][col] != currentState[row][col]) {
					stateChanged = true;
				}
			}
		}
		return stateChanged;
	}
	
	// 0 -> no termination (no reward), 0.5 -> draw, 1 -> win, -1 -> loose
	public double checkForTermination(OthelloBoard board, int move, int player) {
		double result = 0.0;
		OthelloBoard boardCopy = board.clone();
		boardCopy.makeMove(move, player);
		boolean thereIsValidMove = false;
		for (int row = 0; row < OthelloBoard.SIZE; row++) {
			for (int col = 0; col < OthelloBoard.SIZE; col++) {
				int position = (row + 1) * 10 + col + 1;
				if (boardCopy.isValidMove(position, player)) {
					thereIsValidMove = true;
				}
			}
		}
		if (!thereIsValidMove) {
			int blackPieces = BoardUtils.countPieces(boardCopy, Board.BLACK);
			int whitePieces = BoardUtils.countPieces(boardCopy, Board.WHITE);
			if (whitePieces == blackPieces) {
				return drawReward;
			}
			if (player == Board.BLACK) {
				if (blackPieces > whitePieces) {
					return winReward;
				}
				else {
					return lossReward;
				}
			}
			if (player == Board.WHITE) {
				if (blackPieces > whitePieces) {
					return lossReward;
				}
				else {
					return winReward;
				}
			}
		}
		return result;
	}
	

	public int turn = 0;
	
	public int times = 0;
	public boolean isFirstTurn = true;

	/**
	 * @param move is a value rc, where (1 <= r,c <= 8), e.g. 23 means row=2, col=3 
	 * @player is a player I'm playing with
	 * @board is an Othello board with Board.WHITE, Board.BLACK or Board.EMPTY pieces
	 */
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) 
	{
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE || player == Board.BLACK);
		
		double qValue = 0.0;
		
		if (isFirstTurn) {
			stateFromBoard(board, currentState);
			createPreviousState();
			isFirstTurn = false;
		}
		
		if (stateChanged() && times < 4) {
			times++;
		}
		
		stateFromBoard(board, currentState);
		qValue = calculateQValue(board, move, player);
		if (times == 0) {
			previousMoves.add(move);
			previousQValues.add(qValue);
			twoTurnBehindBoard = board.clone();
		}
		else if (times == 1) {
			if (stateChanged()) {
				previousQValue = Collections.max(previousQValues);
				int indexOfMaxValue = previousQValues.indexOf(previousQValue);
				previousMove = previousMoves.get(indexOfMaxValue);
				oneTurnBehindBoard = board.clone();
				previousQValues.clear();
				previousMoves.clear();
			}
			previousMoves.add(move);
			previousQValues.add(qValue);
		}
		else {
			if (stateChanged()) {
				double nextMaxQValue = Collections.max(previousQValues);
				int indexOfMaxValue = previousQValues.indexOf(nextMaxQValue);
				int nextPreviousMove = previousMoves.get(indexOfMaxValue);
				double reward = checkForTermination(board, move, player);
				updateWeights(nextMaxQValue, reward, player);
				twoTurnBehindBoard = oneTurnBehindBoard;
				oneTurnBehindBoard = board.clone();
				previousQValue = nextMaxQValue;
				previousMove = nextPreviousMove;
				previousQValues.clear();
				previousMoves.clear();
			}
			previousMoves.add(move);
			previousQValues.add(qValue);
		}
		createPreviousState();	
		
		
		
		
//		if (turn < 100) {
//			System.out.println("Q Value: " + qValue + " for move: " + move);
//			System.out.println("----------------");
//			for (int col = 0; col < board.getWidth(); col++) {
//				for (int row = 0; row < board.getWidth(); row++) {
//					System.out.print(weights[row][col] + " ");
//				}
//				System.out.print("\n");
//			}
//			System.out.println("----------------");
//			for (int col = 0; col < board.getWidth(); col++) {
//				for (int row = 0; row < board.getWidth(); row++) {
//					System.out.print(board.getValue(row, col) + " ");
//				}
//				System.out.print("\n");
//			}
//			turn++;
//		}
		
		
		
		// I should return the evaluation (the utility) of the move. The higher the better.
		return qValue;
	}
}
