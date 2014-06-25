package misio;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class DembskiKedzierskiMoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {

	public static double earlyWeights[][] = new double[][] {
		{17320.043292946798, -1487.9396309631697, 12036.14253704537, -5248.206517332731, -4304.3729184657, 12483.033883802438, -1480.7460510331564, 19131.521970189475, },
		{-1196.7412737372126, -4168.363754181275, -315.1939134091149, 228.04238233139833, -167.74717609103683, -395.5004454427506, -3806.4290767496523, -1436.4757447031616, },
		{12837.497673368875, -402.0522439232845, 2601.526368971284, 1395.8874284831065, 1893.4728008481648, 3029.070062171985, -708.5335942694762, 12594.2411218928, },
		{-301.8975588120634, 711.268133037518, -222.24505613311703, -953.1764250381036, -602.1431534497767, -64.48396197423946, -2311.076281136626, 920.4443918762125, },
		{-326.19874483521664, 745.2124440318107, -205.3068684609135, -925.288947233547, -704.3685275345412, -119.42954502348519, -241.8608429912673, 916.508586985585, },
		{12887.971894877737, -452.3289036630279, 2569.248118046365, 1444.6435215789854, 1899.739993502805, 2978.4252897315714, -746.3249690426715, 12625.629877048163, },
		{-1161.1078722664445, -3769.1744679442227, -365.834979305517, 227.60953211120903, -164.17808980377507, -509.2516936555548, -4107.5531290587383, -1505.6592630025273, },
		{16880.703643081379, -1291.2236512205357, 12015.833475454141, -5228.491502601976, -4297.050596120443, 12458.365340753033, -1495.1450609695087, 19111.152955832182, },
	};
	
	private static boolean learnable = false;
	
	public void shouldLearn(boolean learn) {
		learnable = learn;
	}
	
	public void learn(OthelloBoard board, int move, int player, double terminal) {
		if (!learnable) return;
		OthelloBoard afterState = board.clone();
		if (afterState.isValidMove(move, player)) {
			afterState.makeMove(move, player);
		}
		double evaluation = Math.tanh(evaluateState(afterState, player));
		double prevEvaluation = Math.tanh(evaluateState(board, player));
		
		if (terminal > 0.001 || terminal < -0.001) {
			evaluation = terminal;
		}
		
		double d = 0.005 * (evaluation - prevEvaluation) * (1 - prevEvaluation * prevEvaluation);
		for(int i=0; i<board.getHeight(); i++)
			for(int j=0; j<board.getWidth(); j++)
				earlyWeights[i][j] += d * ((board.getValue(i, j) == player ? 1 : board.getValue(i, j) != Board.EMPTY ? -1: 0));
	}
	
	public void print() {
		System.out.println("---");
		System.out.println("{");
		for(int i=0; i<8; i++) {
			System.out.print("{");
			for(int j=0; j<8; j++) {
				System.out.print(earlyWeights[i][j]);
				System.out.print(", ");
			}
			System.out.println("},");
		}
		System.out.println("};");
	}
	
	public double evaluateState(OthelloBoard board, int player) {
		double result = 0.0;
		for (int i = 0; i < board.getHeight(); i++)
			for (int j = 0; j < board.getWidth(); j++)
				result += (board.getValue(i, j) == player ? 1 : -1) * earlyWeights[i][j];
		return result;
	}
	
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		OthelloBoard clone = board.clone();
		clone.makeMove(move, player);
		return evaluateState(clone, player);
	}

}
