package put.ci.cevo.games.othello;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardGame;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.google.common.base.Preconditions;

public final class Othello implements BoardGame<OthelloPlayer, OthelloPlayer, OthelloState> {

	static final int NULL_MOVE = Integer.MIN_VALUE;

	private final GameResultEvaluator boardEvaluator;

	public Othello(GameResultEvaluator boardEvaluator) {
		this.boardEvaluator = boardEvaluator;
	}

	public GameResultEvaluator getBoardEvaluator() {
		return boardEvaluator;
	}

	@Override
	public GameOutcome play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, RandomDataGenerator random) {
		return play(blackPlayer, whitePlayer, new OthelloState(new OthelloBoard(), Board.BLACK), random);
	}

	/**
	 * Play a game from initialState
	 */
	@Override
	public GameOutcome play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, OthelloState initialState,
			RandomDataGenerator random) {
		OthelloBoard finalBoard = playImpl(blackPlayer, whitePlayer, initialState.getBoard(),
			initialState.getCurrentPlayer(), random);
		final int blackPieces = BoardUtils.countPieces(finalBoard, Board.BLACK);
		final int whitePieces = BoardUtils.countPieces(finalBoard, Board.WHITE);
		return getBoardEvaluator().evaluate(blackPieces, whitePieces);
	}

	public static OthelloBoard playImpl(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, RandomDataGenerator random) {
		return playImpl(blackPlayer, whitePlayer, new OthelloBoard(), OthelloBoard.BLACK, random);
	}

	/**
	 * Play a game and return OthelloBoard
	 */
	static OthelloBoard playImpl(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, OthelloBoard initialBoard,
			int playerToMove, RandomDataGenerator random) {
		Preconditions.checkArgument(playerToMove == Board.BLACK || playerToMove == Board.WHITE);

		OthelloBoard board = initialBoard.clone();

		IntDoubleLinkedSet possibleMoves = getPossibleMoves(board);

		final OthelloPlayer[] players = playerToMove == Board.BLACK ? new OthelloPlayer[] { blackPlayer, whitePlayer }
			: new OthelloPlayer[] { whitePlayer, blackPlayer };
		final int[] playerColors = new int[] { playerToMove, OthelloBoard.opponent(playerToMove) };

		boolean aMoveWasPossible;
		do {
			aMoveWasPossible = false;
			for (int p = 0; p < players.length; p++) {
				OthelloPlayer player = players[p];
				int playerColor = playerColors[p];

				int[] validMoves = getValidMoves(board, playerColor, possibleMoves);
				if (validMoves.length == 0) {
					continue;
				}
				// clone() is not necessary if I trust the player...
				int move = player.getMove(board.clone(), playerColor, validMoves, random);
				board.makeMove(move, playerColor);
				aMoveWasPossible = true;
				updatePossibleMoves(possibleMoves, board, move);
			}
		} while (aMoveWasPossible);

		return board;
	}

	/**
	 * Get a superset of valid moves for a given board
	 */
	private static IntDoubleLinkedSet getPossibleMoves(OthelloBoard board) {
		IntDoubleLinkedSet moves = new IntDoubleLinkedSet(OthelloBoard.BUFFER_SIZE, OthelloBoard.BUFFER_SIZE);
		for (int pos = 0; pos < board.buffer.length; pos++) {
			if (!board.isEmpty(pos)) {
				continue;
			}
			for (int dir : OthelloBoard.DIRS) {
				int neighbour = pos + dir;
				if (!board.isEmpty(neighbour) && board.isInBoard(neighbour)) {
					moves.add(pos);
					break;
				}
			}
		}
		return moves;
	}

	/*
	 * Update the list of possible moves by taking into account the lastMove made
	 */
	private static void updatePossibleMoves(IntDoubleLinkedSet moves, OthelloBoard board, int lastMove) {
		if (lastMove == NULL_MOVE) {
			return;
		}
		moves.remove(lastMove);

		for (int dir : OthelloBoard.DIRS) {
			int neighbour = lastMove + dir;
			if (board.isEmpty(neighbour)) {
				moves.add(neighbour);
			}
		}
	}

	private static int[] getValidMoves(OthelloBoard board, int color, IntDoubleLinkedSet possibleMoves) {
		IntArrayList validMoves = new IntArrayList(possibleMoves.size());
		for (int i = 0; i < possibleMoves.size(); ++i) {
			if (board.isValidMove(possibleMoves.dense[i], color)) {
				validMoves.add(possibleMoves.dense[i]);
			}
		}
		return validMoves.toArray();
	}
}
