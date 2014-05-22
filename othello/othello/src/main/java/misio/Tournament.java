package misio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.Pair;

import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.othello.Othello;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.games.othello.players.wpc.LucasRunnarson2006;
import put.ci.cevo.games.othello.players.wpc.OthelloStandardWPCHeuristic;
import put.ci.cevo.games.othello.players.wpc.SzubertJaskowskiKrawiec2009;
import put.ci.cevo.games.othello.players.wpc.SzubertJaskowskiKrawiec2011;

public class Tournament {

	private static double EPS = 0.1;
	private static int REPEATS = 1000;

	private static Othello othello = new Othello(new MorePointsGameResultEvaluator(1, 0, 0.5));

	private static double playDoubleGames(OthelloPlayer firstPlayer, OthelloPlayer secondPlayer, int repeats,
			RandomDataGenerator random) {
		double sumPoints = 0.0;
		for (int i = 0; i < repeats; ++i) {
			GameOutcome first = othello.play(firstPlayer, secondPlayer, random);
			GameOutcome second = othello.play(secondPlayer, firstPlayer, random);
			sumPoints += (first.blackPlayerPoints + second.whitePlayerPoints) / 2.0;
		}
		return sumPoints / repeats;
	}

	public static void main(String[] args) {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

		ArrayList<Pair<String, OthelloPlayer>> players = new ArrayList<>();
		players.add(new Pair<String, OthelloPlayer>("SWH", new OthelloWPCPlayer(new OthelloStandardWPCHeuristic().create(), EPS)));
		players.add(new Pair<String, OthelloPlayer>("LR06", new OthelloWPCPlayer(new LucasRunnarson2006().create(), EPS)));
		players.add(new Pair<String, OthelloPlayer>("SJK09", new OthelloWPCPlayer(new SzubertJaskowskiKrawiec2009().create(), EPS)));
		players.add(new Pair<String, OthelloPlayer>("SJK11", new OthelloWPCPlayer(new SzubertJaskowskiKrawiec2011().create(), EPS)));
		players.add(new Pair<String, OthelloPlayer>("Example", new MISiOOthelloPlayer(new ExampleMoveEvaluator(random))));

		roundRobinTournament(random, players, REPEATS);
	}

	private static void roundRobinTournament(RandomDataGenerator random,
			ArrayList<Pair<String, OthelloPlayer>> players, int repeats) {
		final int n = players.size();
		final double[][] rr = new double[n][n];
		final double[] total = new double[n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; ++j) {
				double res = playDoubleGames(players.get(i).getSecond(), players.get(j).getSecond(), REPEATS, random);
				rr[i][j] = res;
				rr[j][i] = 1.0 - res;
				total[i] += res / (n - 1);
				total[j] += (1.0 - res) / (n - 1);
			}
		}

		Integer[] keys = new Integer[n];
		for (int i = 0; i < n; i++) {
			keys[i] = i;
		}

		Arrays.sort(keys, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				double diff = total[o2] - total[o1];
				if (diff > 0) {
					return 1;
				}
				if (diff < 0) {
					return -1;
				}
				return 0;
			}
		});

		StringBuilder str = new StringBuilder();
		str.append("Team");
		for (int key : keys) {
			str.append("\t" + players.get(key).getFirst());
		}
		str.append("\tTotal\n");

		for (int key1 : keys) {
			str.append(players.get(key1).getFirst());
			for (int key2 : keys) {
				str.append(rr[key1][key2] > 0 ? String.format("\t%.1f%%", 100 * rr[key1][key2]) : "\t-");
			}
			str.append(String.format("\t%.1f%%\n", 100 * total[key1]));
		}
		System.out.println(str.toString());
	}
}
