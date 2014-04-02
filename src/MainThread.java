import java.io.*;
import java.util.*;

/**
 * 
 * @author mattfabina
 *
 */
public class MainThread {
	
	public static void main(String[] args) {
//		debug();
		
		realMain();
		
	}
	
	private static void realMain() {
		//get a file from the user and load it for the learning computer
		Scanner in = new Scanner(System.in);
		System.out.print("Indicate file to be read to initialize knowledge " +
				"database for the learning computer." +
				"\nFile: ");
		LearningComputer lc;
		try {
			lc = new LearningComputer(new File(in.next()));
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Bad file format.");
			return;
		} catch (NumberFormatException e) {
			System.out.println("Bad file format.");
			return;
		} catch (IllegalArgumentException e) {
			System.out.println("Bad file format (WC).");
			return;
		}
		System.out.println("Load success.");
		
		//menu display
		boolean quitting = false;
		while (!quitting) {
			in = new Scanner(System.in);
			try {
				System.out.println("What would you like to do?\n" +
						"1. Run Simulator\n" +
						"2. Watch Game\n" +
						"3. Learn\n" +
						"4. Quit");	
				//user input
				int input = in.nextInt();
				//menu switch
				switch (input) {
				case 1:
					System.out.println("How many simulations? (Max = 1000000)");
					int sims = in.nextInt();
					System.out.println("Milleseconds to display distribution:");
					int milles = in.nextInt();
					if (sims >= 1 && sims <= 1000000) {
						
						double[][] rets = lc.simulateGames(sims);
						
						double[] results = rets[1];
						double[] scoreRecord = rets[2];

						System.out.println("Results: " +
								"\n\tSeconds Taken: " + results[0] +
								"\n\tHigh Score: " + results[1] +
								"\n\t\tTimes Scored High: " + results[2] +
								"\n\tLow Score: " + results[3] +
								"\n\t\tTimes Scored Low: " + results[4] +
								"\n\tMean: " + results[5]);
						
						displayDistribution(scoreRecord, milles);
					}

					else
						throw new InputMismatchException();
					break;
				case 2:
					System.out.println("How many milliseconds between moves?");
					displayGame(lc, in.nextInt());
					break;
				case 3:
					System.out.println("List the weights to be trained: (i.e.: 1 2 5 OR all)");
					in.nextLine();
					String weightsString = in.nextLine();
					int[] weights = null;
					if (weightsString.toLowerCase().trim().equals("all")) {
						int[] temp = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14};
						weights = temp;
					} else {
						String[] temp = weightsString.split(" ");
						weights = new int[temp.length];
						for (int i = 0; i < temp.length; i++) {
							try {
								weights[i] = Integer.parseInt(temp[i]);
								if (weights[i] < 0 || weights[i] > 14)
									throw new InputMismatchException();
							} catch (NumberFormatException e) {
								throw new InputMismatchException();
							}
						}
					}
					System.out.println("Simulations per increment: (must be power of 10, [100,1000000])");
					sims = in.nextInt();
					if (sims % 10 != 0 || sims < 100 || sims > 1000000)
						throw new InputMismatchException();
					
					lc.learn(weights, sims);
					
					break;
				case 4:
					quitting = true;
					break;
				default:
					throw new InputMismatchException();
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid input, try again.");
			} catch (FileNotFoundException e) {
				System.out.println("Learning computer memory file not found.");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Bad learning computer memory file format.");
			} catch (NumberFormatException e) {
				System.out.println("Bad learning computer memory file format.");
				return;
			}
		}
		
	}

	private static void displayGame(LearningComputer lc, int waitTime) {
		//create the game
		Game g = new Game();
		Controller cont = new Controller(lc, g, waitTime);
		GameDisplay gui = new GameDisplay(cont, g);
		cont.setDisplay(gui);
		cont.start();
		gui.showMe();
		gui.setLocation(0, 0);
		try {
			cont.join();
		} catch (InterruptedException e) {
			//do nothing
		}
		gui.close();
		
		System.out.println("Final Score: " + g.getScore());
	}	

	private static void displayDistribution(double[] scoreRecord, int waitTime) {
		int[] xPoints = new int[500];
		int[] yPoints = new int[500];
		double[] weights = scoreRecord;
		
		//initialize the points
		for (int i = 0; i < xPoints.length; i++) {
			xPoints[i] = 100 + i;
			yPoints[i] = 600;
		}
		
		double max = scoreRecord[0];
		int maxI = 0;
		for (int i = 0; i < scoreRecord.length; i++) {
			if (scoreRecord[i] > max) {
				max = scoreRecord[i];
				maxI = i;
			}
		}
		
		//stretch
		double mult = 500 / max;
		
		System.out.println("\tMedian: " + maxI);
		System.out.println("\t\tTimes Median Hit: " + max);
		
		for (int i = 0; i < yPoints.length; i++)
			yPoints[i] -= (int) Math.floor(mult * weights[i]);
		
				
		Controller cont = new Controller(waitTime);
		DistributionDisplay dd = new DistributionDisplay(cont, xPoints, yPoints);
		cont.setDisplay(dd);
		cont.start();
		dd.showMe();
		dd.setLocation(0, 0);
		try {
			cont.join();
		} catch (InterruptedException e) {
			//do nothing
		}
		dd.close();
	}

//	private static void debug() {
//		LearningComputer lc = null;
//		try {
//			lc = new LearningComputer(new File("memory1.txt"));
//			
//			double lowest = 9080;
//			double highest = 0;
//			for (int i = 0; i < 100; i++) {
//				double score = lc.simulateGames(10000)[1][5];
//				System.out.println(score);
//				
//				if (score < lowest)
//					lowest = score;
//				if (score > highest)
//					highest = score;
//			}
//			
//			System.out.println("\n" + highest);
//			System.out.println(lowest);
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
}
