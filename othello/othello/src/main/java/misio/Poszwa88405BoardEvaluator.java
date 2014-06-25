package misio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.OthelloBoard;

public class Poszwa88405BoardEvaluator {
	final private String MEMFILE = "Poszwa88405w";
	final private int NFEATS = 24;
	private double[] weights;
	private double alpha;

	public Poszwa88405BoardEvaluator(double alpha) {
		this.alpha = alpha;
		recall();
	}
	
	public void recall() {
//		weights = new double[NFEATS];
		weights = new double[]{
			0.47107131063447033,
			-0.2798142193717061,
			-0.07546953216202228,
			0.13888869678674556,
			-0.004560922680954286,
			0.0010658420895165,
			0.01703669909319576,
			-0.02189392424841518,
			0.008955538432442806,
			-0.012579401359442407,
			0.006551049241305127,
			-0.022618031319674766,
			0.019045717576855213,
			-0.026115537482077945,
			0.16563633372327288,
			-0.007258709760126563,
			0.010128376734340668,
			-0.2631125482293857,
			-0.09622925132913959,
			0.45050777611049014,
			0.41687615436447584,
			-0.3767031787577954,
			-0.007524287976812154,
			-0.00936874845902465,
		};
/*		File t = new File(MEMFILE);
		FileReader fr = null;
		try {
			fr = new FileReader(t);
			BufferedReader br = new BufferedReader(fr);
			for (int i = 0; i < NFEATS; ++i) {
				weights[i] = Double.parseDouble(br.readLine());
			}
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 */
	}
	
	public void memorise() {
		File t = new File(MEMFILE);
		FileWriter fw = null;
		try {
			fw = new FileWriter(t);
			for (double w: weights)
				fw.write(Double.toString(w)+'\n');
			fw.close();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int xy2move(int x, int y) { return x+11+10*y; }
	
	public double[] gatherSignals(OthelloBoard board, int player) {
		double[] r = new double[NFEATS];
		int c, i = 0, mvm = 0, ovm = 0,
				mpc = BoardUtils.countPieces(board, player),
				opc = BoardUtils.countPieces(board, OthelloBoard.opponent(player));

		for (int m: new int[]{11,21,22,31,32,33,41,42,43,44,51,52,53,54,61,62,63,71,72,81}) {
			c = board.getValue(m/10 - 1, m%10 - 1);
			r[i] += c == player ? 1 : c == OthelloBoard.EMPTY ? 0 : -1;
			m = 99-m;
			c = board.getValue(m/10 - 1, m%10 - 1);
			r[i] += c == player ? 1 : c == OthelloBoard.EMPTY ? 0 : -1;
			m = 10*(m%10) + m/10;
			c = board.getValue(m/10 - 1, m%10 - 1);
			r[i] += c == player ? 1 : c == OthelloBoard.EMPTY ? 0 : -1;
			m = 99-m;
			c = board.getValue(m/10 - 1, m%10 - 1);
			r[i] += c == player ? 1 : c == OthelloBoard.EMPTY ? 0 : -1;
			++i;
		}
		
		for (int y = 0; y < board.getHeight(); ++y) {
			for (int x = 0; x < board.getWidth(); ++x) {
//				System.out.print(board.getValue(y, x));
	//			mp += board.getValue(y, x) == player?1:0;
	//			op += board.getValue(y, x) == OthelloBoard.opponent(player)?1:0;
				mvm += board.isValidMove(xy2move(x, y), player)?1:0;
				ovm += board.isValidMove(xy2move(x, y), OthelloBoard.opponent(player))?1:0;
			}
//			System.out.println(y + 1 == move/10 ? "—" : "");
		}
//		for (int x = 0; x < board.getWidth(); ++x) {
//			System.out.print(x + 1 == move%10 ? '|' : ' ');
//		}
//		System.out.println(s-64);
		int empty = 64 - opc - mpc;
		r[20]=empty>0?(double)mvm/empty:0;
		r[21]=empty>0?(double)ovm/empty:0;
		r[22]=mpc/64.;
		r[23]=opc/64.;
		return r;
	}
	
	public double evaluateBoard(OthelloBoard board, int player) {
		double[] s = gatherSignals(board, player);
		double mpc = s[22], opc = s[23];
		if (mpc + opc > .99) {
			if (mpc > .51)
				return 100;
			if (mpc < .51)
				return -100;
			return 0;
		}
		double r = 0;
		for (int i = 0; i < NFEATS; ++i)
			r += s[i]*weights[i];
		return r;
	}

	public void learn(OthelloBoard board, int player, double next) {
		if (alpha <= 0)
			return;
//		System.out.println("next "+next+" player "+player);
		double[] s = gatherSignals(board, player);
		double cur = Math.tanh(evaluateBoard(board, player));
		next = Math.tanh(next);
//		System.out.println("next "+next+" cur "+cur);
		for (int i = 0; i < NFEATS; ++i) {
//			System.out.println("i "+i+" s "+ s[i]+" weight "+weights[i]);
			weights[i] += alpha * (next - cur) * (1 - cur * cur) * s[i];
//			System.out.println(weights[i]);
		}
	}
}
