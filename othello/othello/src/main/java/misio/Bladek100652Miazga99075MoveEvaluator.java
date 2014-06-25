package misio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class Bladek100652Miazga99075MoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {
	
	String PATH = "BEST.txt";
	
	public static class StrategyManager {

		public static int PHASE_BEGIN = 0;
		public static int PHASE_MIDDLE = 1;
		public static int PHASE_END = 2;
		
		public static double LEARNING_RATE = 0.5;
		
		public ArrayList<ArrayList<Double>> weightLists = new ArrayList<ArrayList<Double>>();
		public ArrayList<Double> treshLists = new ArrayList<Double>();
		
		public StrategyManager(int numOfPhases, int numOfInputs, double lr){
			LEARNING_RATE = lr;
			for (int i=0; i<numOfPhases; ++i){
				weightLists.add(new ArrayList<Double>());
				treshLists.add(0.0);
			}
			
			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
			for (int i=0; i<numOfPhases; ++i){
				ArrayList<Double> inputs = new ArrayList<Double>();
				for (int j=0; j<numOfInputs; j++) {
					inputs.add(random.nextInt(0, 1000) / 1000.0);
				}
				set(i, inputs);
			}
		}
		
		
		public ArrayList<Double> getWeights(int key){
			return weightLists.get(key);
		}
		
		public void loadWeightsFromFile(String filename){
			
		    try {
		    	BufferedReader br = new BufferedReader(new FileReader(filename));
		    	
		    	int numOfLists = Integer.parseInt(br.readLine());
		    	
		    	//inicjalizacja
		    	weightLists.clear();
		    	treshLists.clear();
		    	for (int i=0; i<numOfLists; ++i){	    		
		    		weightLists.add(new ArrayList<Double>());
					treshLists.add(0.0);
		    	}
		    	
		    	
		    	
		    	for (int i=0; i<numOfLists; ++i){
					ArrayList<Double> list = new ArrayList<Double>();
					//wczytywanie liczby wag
					int numOfWeights = Integer.parseInt(br.readLine());
					//wczytywanie progu
					double tresh = Double.parseDouble(br.readLine());
					//wczytywanie wag
					for (int j=0; j<numOfWeights; ++j){
						double tmp = Double.parseDouble(br.readLine());
						list.add(tmp);
					}
					
					setTreshold(i, tresh);
					set(i, list);
				}	
				
				br.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}	
		
		public void saveWeightsToFile(String filename){
			
			try {
				File file = new File(filename);
				if (!file.exists())
					file.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
				
				int numOfLists = this.weightLists.size();
				bw.write(numOfLists+"\r\n");
				
				for (int i=0; i<numOfLists; ++i){
					ArrayList<Double> list = weightLists.get(i);
					//zapis liczby wag (pami���ta���, by wczyta��� jeszcze pr���g)
					bw.write(list.size()+"\r\n");
					//zapis progu
					bw.write(this.treshLists.get(i)+"\r\n");
					//zapis wag
					for (Double w : list){
						bw.write(w+"\r\n");
					}				
				}			
				
				bw.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }		
		}
		
		/**
		 * Wype���nia odpowiedni��� list��� zgodnie z wagami kt���re metoda dosta���a jako parametr.
		 * @param key
		 * @param weights
		 */
		public void set(int key, ArrayList<Double> weights){
			ArrayList<Double> list = weightLists.get(key);
			list.clear();
			
			for (int i=0; i<weights.size(); ++i){
				list.add(weights.get(i));
			}
		}
		
		/**
		 * Ustawia warto������ progu dla odpowiedniej fazy rozgrywki.
		 * @param key
		 * @param tresh
		 */
		public void setTreshold(int key, double tresh){
			treshLists.set(key, tresh);
		}
		
		/**
		 * Zwraca pr���g dla danej fazy rozgrywki.
		 */
		public double getTreshold(int key){
			return treshLists.get(key);
		}
		
		/**
		 * Dokonuje uczenia poprzez zmian��� wag.
		 */
		public void train(int key, double desiredOut, ArrayList<Double> inputs){
			ArrayList<Double> list = weightLists.get(key);		
			if (inputs.size() > list.size()){
				return; //je���eli wej������ jest wi���cej ni��� wag
			}		
			double out = classify(key, inputs);
			
			double constant = LEARNING_RATE * (desiredOut - out);
			double newValue;
			for (int i=0; i<inputs.size(); ++i){
				newValue = list.get(i) + constant * inputs.get(i); 
				list.set(i, newValue);
			}
			//poprawa progu
			setTreshold(key, getTreshold(key) + constant);
		}
		
		
		/**
		 * Zwraca ���redni��� wa���on��� dla podanej listy wej������.
		 * @param key
		 * @param inputs
		 */
		public double classify(int key, ArrayList<Double> inputs){
			ArrayList<Double> list = weightLists.get(key);		
			if (inputs.size() > list.size()){
				return -1; //je���eli wej������ jest wi���cej ni��� wag
			}		
			
			double mean = treshLists.get(key); //pr���g
			double weightsNum = 1;
			for (int i=0; i<inputs.size(); ++i){
				weightsNum++;
				mean += list.get(i) * inputs.get(i);
			}
			
			return mean / weightsNum;
		}
		
	}
	
	
	
	
	
	//public  MultiLayerPerceptron mlp = new MultiLayerPerceptron(384, 1, 1, 1, 0.001);
	public static StrategyManager sm = new StrategyManager(64, 64*8*3, 0.00001);//64*4*9, 0.01); //352
	
	public Bladek100652Miazga99075MoveEvaluator() {
		sm.loadWeightsFromFile(PATH);
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

		OthelloBoard newBoard = board.clone().createAfterState(move/10 - 1, move%10 - 1, player);
		ArrayList<Double> features = generateFeatures(newBoard, player);
		double moveQuality = evaluateWithANN(chooseSubEvaluator(newBoard), features);
		
		// I should return the evaluation (the utility) of the move. The higher the better.
		return moveQuality;
	}
	
	public static ArrayList<Double> generateFeatures(OthelloBoard board, int player) {
		ArrayList<Double> features = new ArrayList<Double>();
		
		int multi = player-1;
		
		// 64 features based on the state of each field
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				
				for(int k=0; k<8; k++) {
					int val = OthelloBoard.BLACK;
					if(k==0) val = board.getValue(i+1,j);
					if(k==1) val = board.getValue(i+1,j+1);
					if(k==2) val = board.getValue(i,j+1);
					if(k==3) val = board.getValue(i-1,j+1);
					if(k==4) val = board.getValue(i-1,j);
					if(k==5) val = board.getValue(i-1,j-1);
					if(k==6) val = board.getValue(i,j-1);
					if(k==7) val = board.getValue(i+1,j-1);
					
					if(val == OthelloBoard.EMPTY) {
						features.add( ((double) (board.getValue(i,j) - 1) * multi) );// / 2.0);
						features.add( 0.0);// / 2.0);
						features.add( 0.0 );// / 2.0);
					} else if(val == OthelloBoard.WHITE) {
						features.add( 0.0 );// / 2.0);
						features.add(  ((double) (board.getValue(i,j) - 1) * multi));// / 2.0);
						features.add( 0.0 );// / 2.0);
					} else {
						features.add( 0.0 );// / 2.0);
						features.add( 0.0 );// / 2.0);
						features.add(   ((double) (board.getValue(i,j) - 1) * multi) );// / 2.0);
					}
				}
			
			}
		}

		
		return features;
	}
	
	public static int chooseSubEvaluator(OthelloBoard b) {
		int sum = 0;
		int mine = 0;
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(b.getValue(i,j) != OthelloBoard.EMPTY) {
					sum++;
					if(b.getValue(i,j) == OthelloBoard.WHITE) {
						mine++;
					}
				}
			}
		}
		return (int) Math.floor((sum-1)/1.0);
	}
	
	public static OthelloBoard flipBoard(OthelloBoard b, int type) {
		OthelloBoard newBoard = new OthelloBoard();
		if(type == 0) {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					newBoard.setValue(i, j, b.getValue(7-i, j));
				}
			}
		} else if(type == 1) {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					newBoard.setValue(i, j, b.getValue(i, 7-j));
				}
			}
		} else if(type == 2) {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					newBoard.setValue(i, j, b.getValue(7-i, 7-j));
				}
			}
		} else if(type == 3) {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					newBoard.setValue(i, j, b.getValue(j, i));
				}
			}
		} else if(type == 4) {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					newBoard.setValue(i, j, b.getValue(7-j, i));
				}
			}
		} else if(type == 5) {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					newBoard.setValue(i, j, b.getValue(j, 7-i));
				}
			}
		} else if(type == 6) {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					newBoard.setValue(i, j, b.getValue(7-j, 7-i));
				}
			}
		}
		
		return newBoard;
	}
	
	public static double evaluateWithANN(int stage, ArrayList<Double> f) {	
		double[] features = new double[f.size()];
		for(int i=0; i<f.size(); i++) {
			features[i] = f.get(i);
		}

		return sm.classify(stage, f);
	}
	
	
}