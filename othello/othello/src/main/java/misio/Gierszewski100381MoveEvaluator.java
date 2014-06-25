package misio;

import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class Gierszewski100381MoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {
	

	public Gierszewski100381MoveEvaluator() {
		
		
	}
	
	double map[][] =
		{	
        
      {155, -9, 125, 41, 0, 142, -35, 116, },
{-18, -76, 152, 108, 11, 2, -42, -36, },
{176, 70, 145, 95, 197, 189, 1, 76, },
{72, 81, 144, -41, 25, 235, -92, 12, },
{85, 67, 111, -48, 90, 79, 84, 32, },
{156, 78, 212, 118, 117, 158, 73, 120, },
{-52, -75, 69, 94, 82, 46, -32, -28, },
{169, -3, 156, 62, 57, 126, -24, 210, },
     };
	/**
	 * @param move is a value rc, where (1 <= r,c <= 8), e.g. 23 means row=2, col=3 
	 * @player is a player I'm playing with
	 * @board is an Othello board with Board.WHITE, Board.BLACK or Board.EMPTY pieces
	 */
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE || player == Board.BLACK);

		
		return map[move/10-1][move%10-1];
		
		
	}
}
