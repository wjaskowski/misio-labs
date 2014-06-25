package misio;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class Jankowski99410MoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {
	
	private static final double [] weights = {0.996, 0.686, -0.268};

	public Jankowski99410MoveEvaluator() {
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
		return getStrategiesValue(move/10, move%10, board);
	}
	
	public double getStrategiesValue(int row, int col, OthelloBoard board){
		double result = 0.0;
		result += Jankowski99410MoveEvaluator.weights[0] * Corner(row, col);
		result += Jankowski99410MoveEvaluator.weights[1] * frontierNumber(row, col, board);
		result += Jankowski99410MoveEvaluator.weights[2] * DecreaseOpponentMobility(row, col, board);
		return result;
	}
	
	public double Corner(int row, int col){
		boolean temp = (col == 1 && row == 1)
				|| (col == 1 && row == 8)
				|| (col == 8 && row == 1)
				|| (col == 8 && row == 8);
		if (temp) {
			return 1.0;
		}
		else {
			return 0.0;
		}
	}
	
	public double HighestTurnover(int row, int col, OthelloBoard board){
		double pointsPrev = 0.0;
		double pointsAfter = 0.0;
		// Count opponent points
		for(int i=1; i<9; i++){
			for(int k=1; k<9; k++){
				if(board.getValue(i*10+k) == Board.WHITE){
					pointsPrev += 1.0;
				}
			}
		}
		if (board.isValidMove(row*10+col, Board.BLACK)){
			OthelloBoard boardCopy = board.clone();
			boardCopy.makeMove(row*10+col, Board.BLACK);
			for(int i=1; i<9; i++){
				for(int k=1; k<9; k++){
					if(board.getValue(i*10+k) == Board.WHITE){
						pointsAfter += 1.0;
					}
				}
			}
			return (pointsPrev - pointsAfter)/32.0;
		}
		else {
			return 0.0;
		}
	}
	
	public double DecreaseOpponentMobility(int row, int col, OthelloBoard board){
		double minMob = 0.0;
		for(int i=1; i<9; i++){
			for(int k=1; k<9; k++){
				if(board.isValidMove(row*10+col, Board.BLACK)){
					OthelloBoard boardCopy = board.clone();
					boardCopy.makeMove(row*10+col, Board.BLACK);
					for(int a=1; a<9; a++){
						for(int b=1; b<9; b++){
							if(boardCopy.isValidMove(a*10+b, Board.WHITE)){
								minMob += 1.0;
							}
						}
					}
				}
			}
		}
		return minMob/10.0;
	}
	
	public double Edge(int row, int col){
		if( ((row == 1 || row == 8 ) && (col >= 3 && col <= 6))
			|| ((col == 1 || col == 8 ) && (row >= 3 && row <= 6)) ){
			return 1.0;
		}
		else{
			return 0.0;
		}
	}
	
	public double StopOpponentTurn(int col, int row, OthelloBoard board) {
		boolean result = true;
		OthelloBoard boardCopy = board.clone();
		boardCopy.makeMove(col*10+row, Board.BLACK);
		for (int i = 1; i < 9; ++i)
			for (int j = 1; j < 9; ++j)
				if (boardCopy.isValidMove(i+10+j, Board.WHITE)) {
					result = false;
					break;
				}
		if(result){
			return 1.0;
		}
		else{
			return 0.0;
		}
	}
	
	public double frontierNumber(int row, int col, OthelloBoard board){
		double result = 0.0;
		Board boardCopy = board.clone();
		for (int i = 0; i < 8; ++i){
			for (int k = 0; k < 8; ++k){
				if(boardCopy.getValue(i,  k) == Board.BLACK){
					if(boardCopy.getValue(i-1, k) == Board.EMPTY){
						continue;
					}
					else if(boardCopy.getValue(i+1, k) == Board.EMPTY){
						continue;
					}
					if(boardCopy.getValue(i, k-1) == Board.EMPTY){
						continue;
					}
					else if(boardCopy.getValue(i, k+1) == Board.EMPTY){
						continue;
					}
					if(boardCopy.getValue(i-1, k+1) == Board.EMPTY){
						continue;
					}
					else if(boardCopy.getValue(i+1, k+1) == Board.EMPTY){
						continue;
					}
					if(boardCopy.getValue(i+1, k-1) == Board.EMPTY){
						continue;
					}
					else if(boardCopy.getValue(i-1, k-1) == Board.EMPTY){
						continue;
					}
					else{
						result += 1.0;
					}
				}
			}
		}
		return result / 10.0;
	}
}