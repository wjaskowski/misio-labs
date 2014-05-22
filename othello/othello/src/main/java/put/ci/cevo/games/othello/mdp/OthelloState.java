package put.ci.cevo.games.othello.mdp;

import java.io.Serializable;

import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.rl.environment.GameState;

public class OthelloState extends GameState implements Serializable {

	private static final long serialVersionUID = -6286363206021152568L;

	private final OthelloBoard board;

	public OthelloState(OthelloBoard board, int player) {
		super(player);
		this.board = board;
	}

	@Override
	public double[] getFeatures() {
		return board.getFeatures();
	}

	public OthelloBoard getBoard() {
		return board;
	}

	@Override
	public String toString() {
		return "( " + currentPlayer + " ,\n" + board.toString() + " )";
	}

	public int getDepth() {
		return BoardUtils.countDepth(board);
	}
}
