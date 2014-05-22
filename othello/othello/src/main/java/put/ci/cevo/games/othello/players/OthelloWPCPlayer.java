package put.ci.cevo.games.othello.players;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.evaluators.OthelloWPCMoveDeltaEvalutor;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;
import put.ci.cevo.rl.agent.functions.wpc.MarginWPC;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

/**
 * A shortcut to <code>MoveEvaluatorPlayer(new OthelloWPCMoveEvalutor)</code>
 * 
 * @author Wojciech Jaśkowski
 * 
 */
public class OthelloWPCPlayer implements OthelloPlayer {

	private final MoveEvaluatorPlayer<OthelloBoard> evaluatorPlayer;

	public OthelloWPCPlayer(WPC wpc) {
		this(wpc, 0.0);
	}

	public OthelloWPCPlayer(WPC wpc, double randomMoveProbability) {
		evaluatorPlayer = new MoveEvaluatorPlayer<>(new OthelloWPCMoveDeltaEvalutor(wpc), randomMoveProbability);
	}

	@Override
	public int getMove(OthelloBoard board, int player, int[] possibleMoves, RandomDataGenerator random) {
		return evaluatorPlayer.getMove(board, player, possibleMoves, random);
	}

	public static MarginWPC createMarginWPC(WPC wpc) {
		return new MarginWPC(wpc, OthelloBoard.SIZE, OthelloBoard.SIZE, OthelloBoard.MARGIN_WIDTH);
	}
}
