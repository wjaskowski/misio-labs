package misio;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

import com.google.common.base.Preconditions;

public class MT99835 implements BoardMoveEvaluator<OthelloBoard>
{
	@SuppressWarnings("unused")
	private RandomDataGenerator random;
	
	static double  wP[] = {1.29932,-0.23125,0.14067,0.04025,0.03775,0.15388,-0.28102,1.05439,-0.20352,-0.12593,-0.03702,-0.01516,0.00749,-0.01438,-0.10369,-0.19870,0.10260,-0.01030,-0.06223,-0.02839,0.00267,-0.02039,-0.01339,0.10981,0.08872,0.00364,-0.03796,-0.03518,0.01129,0.03966,0.04536,0.03291,0.08258,0.02541,-0.00435,-0.01834,-0.00365,0.00823,0.04155,0.11798,0.16256,-0.03567,-0.03232,-0.03660,0.01940,-0.00316,-0.01581,0.12026,-0.27826,-0.15925,-0.02156,-0.03350,0.00345,0.00079,-0.07750,-0.29788,1.25394,-0.16662,0.05844,0.03057,0.01413,0.09054,-0.27652,1.23344,0.10507};
	static double wM[] = {0.66315,0.08124,0.07054,0.01700,0.02963,0.06299,0.01228,0.65783,0.02399,-0.02528,0.01583,0.02422,0.01033,-0.01062,-0.01358,0.05921,0.07093,-0.00388,0.03149,0.01149,-0.00667,0.01215,0.01137,0.04640,0.04307,0.01308,0.02915,0.01191,0.12891,0.01533,0.02487,0.02674,0.05178,0.01816,0.04053,-0.00453,0.05228,0.03246,-0.00745,0.02484,0.05817,0.03677,0.03152,0.02070,0.03318,-0.02979,0.01774,0.01411,-0.02462,-0.01249,0.01574,0.00159,-0.01178,0.03104,-0.01689,0.01128,0.63740,0.03636,0.00784,0.02660,0.03191,0.01851,0.00716,0.55135,-0.24455};
	static double wS[] = {-0.04720,-0.19345,-0.02018,-0.08303,-0.07203,0.08835,-0.18543,-0.09132,-0.07606,-0.36356,-0.06612,-0.04152,0.09245,-0.01383,-0.38841,-0.13966,0.00887,-0.12129,-0.05740,0.01124,0.00896,-0.02384,-0.02028,0.06059,-0.07954,-0.07882,-0.03665,0.01149,-0.23217,-0.04718,-0.14165,-0.14389,-0.19310,-0.03206,-0.01700,-0.00474,-0.10387,-0.04599,-0.07024,-0.10960,-0.01657,-0.08464,-0.13605,0.02432,0.00614,-0.10891,-0.13027,0.07522,-0.20113,-0.44690,-0.07503,-0.02008,-0.02212,-0.10270,-0.46075,-0.06230,-0.09473,-0.22692,0.04684,-0.21854,-0.10218,-0.00586,-0.25094,-0.11420,0.19909};
	static double w1[] = {0.32666,0.58846,-0.86095,0.29044};
	static double w2[] = {-0.24105,-0.07194,0.85236,0.34605};
	static double wC[] = {0.64178,-0.53141,-0.13948};
	
	public MT99835(RandomDataGenerator random)
	{
		this.random = random;
	}

	public static int[][] getMap(OthelloBoard board)
	{
		int map[][] = new int[8][8];
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (board.getValue(i, j) == Board.EMPTY)
					map[i][j] = 0;
				else if (board.getValue(i, j) == Board.BLACK)
					map[i][j] = 1;
				else
					map[i][j] = -1;
			}
		}
		return map;
	}

	@Override
	public double evaluateMove(OthelloBoard board, int move, int player)
	{
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE || player == Board.BLACK);

		double vec[] = getVec(board, move);
		return vec[0];
	}

	public static double[] getVec(OthelloBoard board, int move)
	{
		int map[][] = getMap(board);
		applyMove(map, move);
		return getVec(map);
	}

	public static double[] getVec(int map[][])
	{
		int mobMap[][] = getMobilityMap(map);
		int stabMap[][] = getStableMap(map);
		
		double sumP = 0.0d;
		for (int j = 0; j < 8; j++)
			for (int k = 0; k < 8; k++)
				sumP += wP[j * 8 + k] * map[j][k];
		sumP -= wP[64];
		
		double sumM = 0.0d;
		for (int j = 0; j < 8; j++)
			for (int k = 0; k < 8; k++)
				sumM += wM[j * 8 + k] *mobMap[j][k];
		sumM -= wM[64];
		
		double sumS = 0.0d;
		for (int j = 0; j < 8; j++)
			for (int k = 0; k < 8; k++)
				sumS += wS[j * 8 + k] *stabMap[j][k];
		sumS -= wS[64];
		
		double stP = atan(sumP);
		double stM = atan(sumM);
		double stS = atan(sumS);
		
		double sumA = stP * w1[0] + stM * w1[1] + stS * w1[2] - w1[3];
		double sumB = stP * w2[0] + stM * w2[1] + stS * w2[2] - w2[3];	
		
		double sum = atan(sumA) * wC[0] + atan(sumB) * wC[1] - wC[2];
		double S1 = atan(sum);

		
		double res[] = { S1 };
		
		return res;
	}

	public static double atan(double arg)
	{
		double e = Math.exp(-2 * arg);
		return 2.0d / (1.0d + e) - 1.0d;
	}

	public static int[][] getStableMap(int map[][])
	{
		int res[][] = new int[8][8];

		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if ((map[i][j] == 1) && (isStable(i, j, 1, map)))
					res[i][j] = 1;
				else if ((map[i][j] == -1) && (isStable(i, j, -1, map)))
					res[i][j] = -1;
				else res[i][j] = 0;
			}
		}

		return res;
	}

	public static boolean isStable(int y, int x, int player, int map[][])
	{
		// CHECK H
		boolean h1 = true;
		for (int i = 0; i <= x; i++)
			if (map[y][i] != player)
			{
				h1 = false;
				break;
			}
		boolean h2 = true;
		for (int i = x; i < 8; i++)
			if (map[y][i] != player)
			{
				h2 = false;
				break;
			}
		if ((!h1) && (!h2))
			return false;
		// CHECK V
		boolean v1 = true;
		for (int i = 0; i <= y; i++)
			if (map[i][x] != player)
			{
				v1 = false;
				break;
			}
		boolean v2 = true;
		for (int i = y; i < 8; i++)
			if (map[i][x] != player)
			{
				v2 = false;
				break;
			}
		if ((!v1) && (!v2))
			return false;
		// CHECK 45
		boolean a45_1 = true;
		for (int i = 0; i < 8; i++)
		{
			if ((y - i < 0) || (x - i < 0))
				break;
			if (map[y - i][x - i] != player)
			{
				a45_1 = false;
				break;
			}
		}
		boolean a45_2 = true;
		for (int i = 0; i < 8; i++)
		{
			if ((y + i > 7) || (x + i > 7))
				break;
			if (map[y + i][x + i] != player)
			{
				a45_2 = false;
				break;
			}
		}
		if ((!a45_1) && (!a45_2))
			return false;
		// CHECK 135
		boolean a135_1 = true;
		for (int i = 0; i < 8; i++)
		{
			if ((y - i < 0) || (x + i > 7))
				break;
			if (map[y - i][x + i] != player)
			{
				a135_1 = false;
				break;
			}
		}
		boolean a135_2 = true;
		for (int i = 0; i < 8; i++)
		{
			if ((y + i > 7) || (x - i < 0))
				break;
			if (map[y + i][x - i] != player)
			{
				a135_2 = false;
				break;
			}
		}
		if ((!a135_1) && (!a135_2))
			return false;
		return true;
	}
	
	public static int[][] getMobilityMap(int map[][])
	{
		int res[][] = new int[8][8];

		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
			{
				if ((map[i][j] == 0) && (isMovePossible(i, j, 1, map)))
					res[i][j] += 1;

				if ((map[i][j] == 0) && (isMovePossible(i, j, -1, map)))
					res[i][j] -= 1;
				//else
				//	res[i][j] = 0;
			}
		return res;
	}

	public static boolean isMovePossible(int y, int x, int player, int map[][])
	{
		int dy = -1;
		int dx = -2;

		for (int m = 0; m < 9; m++)
		{
			dx++;
			if (dx == 2)
			{
				dy++;
				dx = -1;
			}
			if ((dy == 0) && (dx == 0))
				continue;

			int cnt = 0;
			for (int i = 1; i < 8; i++)
			{
				if (y + i * dy < 0)
					break;
				if (x + i * dx < 0)
					break;
				if (y + i * dy > 7)
					break;
				if (x + i * dx > 7)
					break;
				if (map[y + i * dy][x + i * dx] == 0)
					break;

				if (map[y + i * dy][x + i * dx] != player)
				{
					cnt++;
				}
				else if (map[y + i * dy][x + i * dx] == player)
				{
					if (cnt > 0)
						return true;
					else break;
				}
			}

		}
		return false;
	}

	public static void applyMove(int map[][], int move)
	{
		int y = move / 10 - 1;
		int x = move % 10 - 1;

		// UPDATE MAP
		int dy = -1;
		int dx = -2;

		for (int m = 0; m < 9; m++)
		{
			dx++;
			if (dx == 2)
			{
				dy++;
				dx = -1;
			}
			if ((dy == 0) && (dx == 0))
				continue;

			for (int i = 1; i < 8; i++)
			{
				if (y + i * dy < 0)
					break;
				if (x + i * dx < 0)
					break;
				if (y + i * dy > 7)
					break;
				if (x + i * dx > 7)
					break;

				if (map[y + i * dy][x + i * dx] == 0)
					break;
				if (map[y + i * dy][x + i * dx] == 1)
				{
					for (int j = 0; j < i; j++)
						map[y + dy * j][x + dx * j] = 1;
					break;
				}
			}

		}
	}
}
