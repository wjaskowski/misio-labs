package put.ci.cevo.games.board;

public enum BoardEvaluationType {
	/** The first player prefers maximum utility and the second one minimum utility (like in OthelloLeague) */
	OUTPUT_NEGATION,
	/** The board is always seen from the first player's perspective */
	BOARD_INVERSION;
}
