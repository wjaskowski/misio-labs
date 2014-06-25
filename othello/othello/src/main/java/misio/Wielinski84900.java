package misio;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.google.common.base.Preconditions;
import com.sun.xml.internal.bind.api.impl.NameConverter.Standard;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class Wielinski84900 implements BoardMoveEvaluator<OthelloBoard> {
	
	private RandomDataGenerator random;
	/**
	 * weighted sum of owned pieces + bonus for get pieces - penalty from uncovered options - penalty for worst option for oponent
	 * + number of possible moves -1;
	 * @param random
	 */
	static double early[] = {0.785741 ,-0.266148 ,0.249657 ,0.000422 ,-0.042578 ,0.286399 ,-0.174502 ,0.751291 ,
		-0.223083 ,-0.425207 ,-0.148500 ,-0.087195 ,-0.056732 ,-0.079475 ,-0.416679 ,-0.058953 ,
		0.452200 ,-0.146547 ,0.088240 ,-0.027385 ,-0.069190 ,0.094184 ,-0.158883 ,0.483618 ,
		-0.102682 ,-0.047619 ,-0.021696 ,-0.047325 ,-0.012190 ,-0.023300 ,-0.109480 ,-0.014107 ,
		-0.056577 ,-0.049154 ,-0.030926 ,-0.059351 ,-0.067808 ,-0.037521 ,-0.069110 ,-0.064746 ,
		0.469758 ,-0.117659 ,0.142242 ,-0.001866 ,-0.036044 ,0.085520 ,-0.201167 ,0.383104 ,
		-0.226853 ,-0.510893 ,-0.092250 ,-0.014180 ,-0.103558 ,-0.148561 ,-0.587601 ,-0.202411 ,
		0.837926 ,-0.263491 ,0.309002 ,-0.007139 ,0.032643 ,0.400808 ,-0.091792 ,0.947358};
		    
		   static double late[] ={0.805895 ,-0.266800 ,0.240169 ,-0.001740 ,-0.044740 ,0.276911 ,-0.175154 ,0.771444 ,
				   -0.223735 ,-0.412956 ,-0.178197 ,-0.102404 ,-0.071940 ,-0.109171 ,-0.404429 ,-0.059605 ,
				   0.442712 ,-0.176243 ,0.050535 ,0.011319 ,-0.030486 ,0.056480 ,-0.188579 ,0.474130 ,
				   -0.104844 ,-0.062827 ,0.017008 ,-0.005017 ,0.030118 ,0.015404 ,-0.124688 ,-0.016269 ,
				   -0.058739 ,-0.064363 ,0.007778 ,-0.017043 ,-0.025501 ,0.001184 ,-0.084319 ,-0.066908 ,
				   0.460270 ,-0.147356 ,0.104538 ,0.036838 ,0.002660 ,0.047816 ,-0.230863 ,0.373615 ,
				   -0.227506 ,-0.498643 ,-0.121947 ,-0.029389 ,-0.118766 ,-0.178257 ,-0.575351 ,-0.203063 ,
				   0.858079 ,-0.264143 ,0.299513 ,-0.009301 ,0.030481 ,0.391319 ,-0.092445 ,0.967511};

		   static double middle[] = {0.817805 ,-0.283014 ,0.251376 ,-0.009739 ,-0.052739 ,0.288117 ,-0.191368 ,0.783355 ,
				   -0.239948 ,-0.424254 ,-0.179784 ,-0.095934 ,-0.065471 ,-0.110759 ,-0.415726 ,-0.075818 ,
				   0.453919 ,-0.177831 ,0.054157 ,0.018405 ,-0.023400 ,0.060101 ,-0.190167 ,0.485337 ,
				   -0.112843 ,-0.056357 ,0.024094 ,-0.007176 ,0.027959 ,0.022490 ,-0.118218 ,-0.024268 ,
				   -0.066738 ,-0.057893 ,0.014863 ,-0.019202 ,-0.027659 ,0.008269 ,-0.077849 ,-0.074907 ,
				   0.471476 ,-0.148943 ,0.108160 ,0.043924 ,0.009745 ,0.051437 ,-0.232451 ,0.384822 ,
				   -0.243719 ,-0.509940 ,-0.123534 ,-0.022919 ,-0.112296 ,-0.179845 ,-0.586648 ,-0.219277 ,
				   0.869990 ,-0.280357 ,0.310720 ,-0.017300 ,0.022482 ,0.402526 ,-0.108658 ,0.979422};
		   
		
			

	public RandomDataGenerator getRandom() {
		return random;
	}

	public void setRandom(RandomDataGenerator random) {
		this.random = random;
	}


	
	public Wielinski84900() {
	}

	/**
	 * @param move is a value rc, where (1 <= r,c <= 8), e.g. 23 means row=2, col=3 
	 * @player is a player I'm playing with
	 * @board is an Othello board with Board.WHITE, Board.BLACK or Board.EMPTY pieces
	 */
	
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		Preconditions.checkArgument(11 <= move && move <= 88);
		Preconditions.checkArgument(player == Board.WHITE || player == Board.BLACK);
		
		OthelloBoard b = board.clone();
		b.makeMove(move, player);
		double[] ip = convertBoard(b, player);
		int stage = getStage(ip);
    	if(stage == 0) {
    		return predictCase(ip, player, early);
    	} else if(stage  == 1) {
    		return predictCase(ip, player, middle);
    	} else {
    		return predictCase(ip, player, late);
    	}
	}
	
	public double predictCase(double[] ip, int player, double[] weights) {
        double tot = 0;//weights[ip.length]; // bias node
        
        for (int i=0; i<ip.length; i++) {
            tot += weights[i] * ip[i];
        }
        return tot;
    }
	private double[] convertBoard(OthelloBoard board, int player) {
		double[] input = new double[64];
		int c = 0;
		for(int i = 0; i <= 7; i++) {
			for(int j = 0; j <= 7; j ++) {
				int a = i * 10 + j;
				if(board.getValue(i,j) == player){
					input[c++] = 2;
				} else if(board.getValue(i,j) == Board.EMPTY) {
					input[c++] = 1;
				} else {
					input[c++] = 0;
				}
			}
		}
		return input;
	}
	
	private int getStage(double[] ip) {
		double a = ip[0];
		double b = ip[7];
		double c = ip[56];
		double d = ip[63];
		int corners = 0;
		if( a > 1.5 || a < 0.5) {
			corners++;
		} 
		if( b > 1.5 || b < 0.5) {
			corners++;
		} 
		if( c > 1.5 || c < 0.5) {
			corners++;
		} 
		if( d > 1.5 || d < 0.5) {
			corners++;
		} 
		if(corners > 1) {
			return 2;
		}
		int sides = 0;
		//upper
		for(int i = 0; i < 8; i++) {
			if(ip[i] > 1.5 || ip[i] < 0.5){
				sides ++;
			}
		}
		// bottom
		for(int i = 1; i < 8; i++) {
			if(ip[i+56] > 1.5 || ip[i+56] < 0.5){
				sides ++;
			}
		}
		//right
		for(int i = 1; i < 7; i++) {
			if(ip[i*8+7] > 1.5 || ip[i*8+7] < 0.5){
				sides ++;
			}
		}
		//left
		for(int i = 1; i < 8; i++) {
			if(ip[i*8] > 1.5 || ip[i*8] < 0.5){
				sides ++;
			}
		}
		if(sides > 3) {
			return 1;
		} else {
			return 0;
		}

	}
	
	
}
