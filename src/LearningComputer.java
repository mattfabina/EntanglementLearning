import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Decision Weights:
 * 		[00] = Terminating 			
 * 		[01] = Game Score			
 * 		[02] = Terminating Paths Number
 * 		[03] = Terminating Paths Length Total
 * 		[04] = Terminating Paths Length Mean
 * 		[05] = Terminating Paths Length Mode
 *		[06] = Terminating Paths Length Median
 * 		[07] = Longest Terminating Path
 * 		[08] = Non-Terminating Path Number
 * 		[09] = Non-Terminating Paths Length Total
 * 		[10] = Non-Terminating Paths Length Mean
 * 		[11] = Non-Terminating Paths Length Mode
 *		[12] = Non-Terminating Paths Length Median
 * 		[13] = Longest Non-Terminating Path
 * 		[14] = Distribution of Empty Slots Score
 * 
 * score = (decisionWeights[0] * m.getDecisionScore(0));
 * 
 * @author mattfabina
 *
 */
public class LearningComputer {

	private HashMemory memory;
	private ArrayList<Move> potentialMoves;
	private File externalMemory;
	
	public LearningComputer(File file) throws FileNotFoundException {
		//load the memory
		memory = new HashMemory(file);
		
		//save for later use
		externalMemory = file;
	}

	public boolean getMove(Board b, double decisionWeights[]) {
		//initialize variables
		potentialMoves = new ArrayList<Move>();
		
		//create potential moves
		for (int orientation = 0; orientation < 6; orientation++) {
			potentialMoves.add(new Move(b, false, orientation));
			potentialMoves.add(new Move(b, true, orientation));
		}
		
		//find the top "score", or the best terminating path if applicable
		Move topScoring = null;
		double topScore = Double.NEGATIVE_INFINITY;
		
		for (Move m: potentialMoves) {
			double score = 0.0;
			for (int i = 0; i < decisionWeights.length; i++)
				score += (decisionWeights[i] * m.getDecisionScore(i));
			if (score > topScore) {
				topScoring = m;
				topScore = score;
			}
		}
		
		//rotate the tile again
		if (topScoring.usesSwapTile())
			b.getSwapTile().rotate(topScoring.getOrientation());
		else
			b.getCurrentTile().rotate(topScoring.getOrientation());
	
		return topScoring.usesSwapTile();
	}
	
	public double[][] simulateGames(int numSimulations) 
			throws FileNotFoundException {
		double[] scoreRecord = new double[500];
		int halfSims = 0;
		if (numSimulations > 50000)
			halfSims = numSimulations/2;
		
		int lowScore = 9080;
		int highScore = 0;
		int timesScoredHigh = 0;
		double totalScore = 0;
		int timesScoredLow = 0;
		double gamesPlayed = numSimulations;
		double startTime = System.currentTimeMillis();
		
		while (numSimulations > 0) {
			if (numSimulations == halfSims)
				System.out.println("Halfway.\nTime Passed: " + (System.currentTimeMillis() - startTime)/1000.0);
			//create the game
			Game g = new Game();
			
			//simulate the game
			while(!g.isOver()) {
				g.makeMove(getMove(g.getBoard(), memory.getDecisionWeights()));
			}
			
			//save the results
			totalScore += g.getScore();
			if (g.getScore() < 500)
				scoreRecord[g.getScore()]++;
			if (g.getScore() < lowScore) {
				lowScore = g.getScore();
				timesScoredLow = 1;
			} else if (g.getScore() == lowScore) {
				timesScoredLow++;
			}
			
			if (g.getScore() > highScore) {
				highScore = g.getScore();
				timesScoredHigh = 1;
			} else if (g.getScore() == highScore) {
				timesScoredHigh++;
			}
			
			//decrement repetition variable
			numSimulations--;
		}
		
		double timePassed = System.currentTimeMillis() - startTime;
		timePassed /= 1000.0;
		
		double[] results = new double[6];
		results[0] = timePassed;
		results[1] = highScore;
		results[2] = timesScoredHigh;
		results[3] = lowScore;
		results[4] = timesScoredLow;
		results[5] = (totalScore / gamesPlayed);

		double[][] rets = new double[3][];
		rets[0] = memory.getDecisionWeights();
		rets[1] = results;
		rets[2] = scoreRecord;
		
//		saveKnowledge(rets);
		return rets;
	}

	public void learn(int[] weightsToTrain, int sims) throws FileNotFoundException {
		System.out.println("Beginning decision weights:");
		for (double d: memory.getDecisionWeights())
			System.out.print(d + ";");
		System.out.println();
		
		for (int i = 0; i < weightsToTrain.length; i++) {
			double temp = memory.getDecisionWeights()[i];
			trainWeight(weightsToTrain[i], sims);
			System.out.println("Weight " + weightsToTrain[i] + ": " 
					+ temp + " -> " + memory.getDecisionWeights()[i]);
		}
		
		System.out.println("Final decision weights:");
		for (double d: memory.getDecisionWeights())
			System.out.print(d + ";");
		System.out.println();
	}
	
	private void trainWeight(int weightToTrain, int sims) throws FileNotFoundException {
		
		boolean done = false;
		
		double[] decisionWeights = memory.getDecisionWeights();
		
		//the number of simulations corresponds to the precision that the weights
		// can be adjusted by
		double increment = 100.0 / ((double) sims);
		for (int j = 0; j < 3; j++) {
			done = false;
			while (!done) {
				//test the original weight
				double originalWeight = decisionWeights[weightToTrain];
				double mSCurr = simulateGames(sims)[1][5];
				
				//test one increment higher
				if (originalWeight + increment > 1.0)
					decisionWeights[weightToTrain] = 1.0;
				else
					decisionWeights[weightToTrain] = originalWeight + increment;
				double mSHigh = simulateGames(sims)[1][5];
				
				//test one increment lower
				if (originalWeight - increment < -1.0)
					decisionWeights[weightToTrain] = -1.0;
				else
					decisionWeights[weightToTrain] = originalWeight - increment;
				double mSLow = simulateGames(sims)[1][5];

				//establish range that indicates a change in weight is necessary
				double range = 0;
				switch (sims) {
				case 100:
					range = 8.0;
					break;
				case 1000:
					range = 4.0;
					break;
				case 10000:
					range = 2.0;
					break;
				case 100000:
					range = 1.0;
					break;
				case 1000000:
					range = 0.5;
					break;
				}
				
				if (mSCurr + range >= mSHigh && mSCurr + range >= mSLow) {
					decisionWeights[weightToTrain] = originalWeight;
					done = true;
				} else if (mSHigh > mSLow) {
					if (originalWeight + increment > 1.0)
						decisionWeights[weightToTrain] = 1.0;
					else
						decisionWeights[weightToTrain] = originalWeight + increment;
				} else if (mSHigh < mSLow) {
					if (originalWeight - increment < -1.0)
						decisionWeights[weightToTrain] = -1.0;
					else
						decisionWeights[weightToTrain] = originalWeight - increment;
				}
			}
		}
		
		
	}
	
}
