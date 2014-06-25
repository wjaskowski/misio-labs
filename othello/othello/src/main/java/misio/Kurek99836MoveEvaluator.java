package misio;

import static put.ci.cevo.games.othello.OthelloBoard.opponent;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.base.Preconditions;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.evaluators.OthelloWPCMoveDeltaEvalutor;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.games.player.BoardMoveEvaluator;
import put.ci.cevo.rl.agent.functions.wpc.MarginWPC;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.ArrayUtils;

public class Kurek99836MoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {
	public static double ALFA = 0.01;
	
	static double[][] learnedWPC = new double[][]{
		{0.490, -0.494, -0.070, -0.082, -0.056, 0.059, -0.480, 0.393},
		{-0.510, -0.351, -0.108, -0.123, -0.218, -0.092, -0.236, -0.500},
		{-0.063, -0.116, -0.204, -0.137, -0.155, -0.147, -0.139, -0.016},
		{-0.213, -0.159, -0.118, -0.131, -0.143, -0.147, -0.187, -0.137},
		{-0.152, -0.151, -0.180, -0.168, -0.164, -0.158, -0.143, -0.173},
		{-0.095, -0.102, -0.229, -0.129, -0.168, -0.158, -0.148, -0.095},
		{-0.305, -0.223, -0.066, -0.067, -0.145, -0.115, -0.198, -0.291},
		{0.528, -0.345, -0.073, -0.217, -0.147, -0.080, -0.376, 0.286}
	};
	
	MarginWPC wpc;
	
	public Kurek99836MoveEvaluator(){
		wpc = OthelloWPCPlayer.createMarginWPC(new WPC(ArrayUtils.flatten(learnedWPC)));
	}
	
	public double evaluateMove(OthelloBoard board, int move, int player) {
		IntArrayList moves = board.simulateMove(move, player);
		return evaluateMoves(board, moves, move);
	}
	
	protected double evaluateMoves(OthelloBoard board, IntArrayList moves, int move){
		double r = 0;
		for (int i = 0; i < moves.size(); i++) {
			int pos = moves.buffer[i];
			r += wpc.buffer[pos];
		}
		return r;
	}
}
