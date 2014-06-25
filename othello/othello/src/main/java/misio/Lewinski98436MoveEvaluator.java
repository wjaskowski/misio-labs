package misio;

import static put.ci.cevo.games.othello.OthelloBoard.opponent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.Othello;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;
import put.ci.cevo.rl.agent.functions.wpc.MarginWPC;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class Lewinski98436MoveEvaluator implements
		BoardMoveEvaluator<OthelloBoard> {
	public static boolean needInversion = false;
	private RandomDataGenerator random;
	
	WPC wpc=new Lewinski98436WPC().create();
	List<OthelloBoard> boards = new ArrayList<OthelloBoard>();

	public Lewinski98436MoveEvaluator(RandomDataGenerator random) {
		this.random = random;
	}

	@Override
	public double evaluateMove(OthelloBoard b, int move, int player) {
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE
				|| player == Board.BLACK);
		RandomDataGenerator random = new RandomDataGenerator();
		OthelloBoard board = b.clone();
		board.makeMove(move, player);
		double myPoints = 0;

			for (int i=0;i<8;i++)
			{
				for(int j=0;j<8;j++)
				{
					if (board.getValue(i, j) == player) {
				
						myPoints += wpc.get(j*8+i);
					}else if (board.getValue(i, j) == opponent(player)) {
						
						myPoints -= wpc.get(j*8+i);
					}
					
				}
			}
		
		
		return myPoints;
	}


}
