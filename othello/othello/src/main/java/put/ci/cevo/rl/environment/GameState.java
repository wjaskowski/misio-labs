package put.ci.cevo.rl.environment;

import java.io.Serializable;

public abstract class GameState implements State, Serializable {

	private static final long serialVersionUID = 485200549082656933L;

	protected final int currentPlayer;

	public GameState(int player) {
		this.currentPlayer = player;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}
}
