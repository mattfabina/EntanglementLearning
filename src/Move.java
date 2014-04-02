
public class Move {
	
	private int[] nonTerminatingPathLengths;
	private int[] terminatingPathLengths;
	private int score;
	private int orientation;
	private boolean useSwapTile;
	private boolean terminating;
	
	public Move(Board b, boolean useSwapTile, int orientation) {
		//initialize variables
		terminating = false;
		score = 0;
		this.orientation = orientation;
		this.useSwapTile = useSwapTile;
		
		//rotate
		b.getSwapTile().rotate(orientation);
		b.getCurrentTile().rotate(orientation);
		
		//place piece
		score = b.placeTile(useSwapTile);
		if (b.isFull()) {
			terminating = true;
		}
		
		//do calculations
		calculatePathLengths(b);
		
		//remove piece
		b.removeTile();
		
		//un-rotate
		b.getSwapTile().rotate(6 - orientation);
		b.getCurrentTile().rotate(6 - orientation);
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	public boolean usesSwapTile() {
		return useSwapTile;
	}

	public double getDecisionScore(int i) {
		switch (i) {
		case (0):	//Terminating Score
			return (terminating)? 0.0 : 1.0;
		
		case (1):	//Game Score
			return score;
		
		case (2):	//Terminating Paths Number
			return terminatingPathLengths.length;
		
		case (3):	//Terminating Paths Length Total
			return sumArray(terminatingPathLengths);
		
		case (4):	//Terminating Paths Length Mean
			return ((double)sumArray(terminatingPathLengths) /
					((double)terminatingPathLengths.length));
		
		case (5):	//Terminating Paths Length Average Mode
			int[] modes = maxIndeces(getIntegerOccurences(terminatingPathLengths));
			return ((double) sumArray(modes) / ((double) modes.length));
		
		case (6):	//Terminating Paths Length Median
			return medianInArray(terminatingPathLengths);
		
		case (7):	//Longest Terminating Path
			return terminatingPathLengths[maxIndeces(terminatingPathLengths)[0]];
		
		case (8):	//Non-Terminating Paths Number
			return nonTerminatingPathLengths.length;
		
		case (9):	//Non-Terminating Paths Length Total
			return sumArray(nonTerminatingPathLengths);
		
		case (10):	//Non-Terminating Paths Length Mean
			return ((double)sumArray(nonTerminatingPathLengths) /
					((double)nonTerminatingPathLengths.length));
		
		case (11):	//Non-Terminating Paths Length Average Mode
			modes = maxIndeces(getIntegerOccurences(nonTerminatingPathLengths));
			return ((double) sumArray(modes) / ((double) modes.length));
			
		case (12):	//Non-terminating Paths Length Median
			return medianInArray(nonTerminatingPathLengths);
			
		case (13):	//Longest Non-Terminating Path
			return nonTerminatingPathLengths[maxIndeces(nonTerminatingPathLengths)[0]];
		
		case (14):	//Distribution of Empty Slots Score (boolean[][])
			return 0;
		default:
			throw new IllegalArgumentException();
		}

	}
	
	private void calculatePathLengths(Board b) {
		terminatingPathLengths = new int[1];
		int[] tPLShadow = new int[2];
		nonTerminatingPathLengths = new int[1];
		int[] nTPLShadow = new int[2];
		
		PathHeadList pathHeads = b.getPathHeads();
		
		int i = 0;
		int j = 0;
		for (Link pH: pathHeads) {
			int l = pH.getLengthToTail();
			if (pH.isTerminatingHead()) {
				if (i > terminatingPathLengths.length-1) {
					terminatingPathLengths = tPLShadow;
					tPLShadow = new int[2*tPLShadow.length];
				}
				terminatingPathLengths[i] = l;
				tPLShadow[i] = l;
				tPLShadow[i - terminatingPathLengths.length/2] = 
					terminatingPathLengths[i - terminatingPathLengths.length/2];
				i++;
			}
			
			else {
				if (j > nonTerminatingPathLengths.length-1) {
					nonTerminatingPathLengths = nTPLShadow;
					nTPLShadow = new int[2*nTPLShadow.length];
				}
				nonTerminatingPathLengths[j] = l;
				nTPLShadow[j] = l;
				nTPLShadow[j - nonTerminatingPathLengths.length/2] = 
					nonTerminatingPathLengths[j - nonTerminatingPathLengths.length/2];
				j++;
			}
		}
	}
	
	private static int sumArray(int[] array) {
		int sum = 0;
		for (int i: array)
			sum += i;
		return sum;
	}
	
	private static int[] getIntegerOccurences(int[] array) {
		int[] integerOccurences = new int[1];
		
		for (int i: array) {
			while (i > integerOccurences.length - 1) {
				int[] newOccs = new int[2*integerOccurences.length];
				for (int j = 0; j < integerOccurences.length; j++) {
					newOccs[j] = integerOccurences[j];
				}
				integerOccurences = newOccs;
			}
			integerOccurences[i]++;
		}
		
		return integerOccurences;
	}
	
	private static double medianInArray(int[] array) {
		int[] intOcc = getIntegerOccurences(array);
		boolean even = (array.length % 2 == 0);
		int entriesToMedian = array.length / 2;
		
		int i = 0;
		int lastI = 0;
		for (; entriesToMedian >= 0; i++) {
			entriesToMedian -= intOcc[i];
			if (i > 0 && intOcc[i-1] != 0)
				lastI = i-1;
		}
		i--; //loop correction
		
		return (even)? (((double) lastI) + ((double) i)) / 2: i;
	}
	
	private static int[] maxIndeces(int[] array) {
		int[] maxIndeces = new int[1];
		maxIndeces[0] = 0;
		int j = 1;
		
		for (int i = 1; i < array.length; i++) {
			if (array[i] == array[maxIndeces[0]]) {
				if (j > maxIndeces.length - 1) {
					int[] newMI = new int[maxIndeces.length+1];
					for (int k = 0; k < maxIndeces.length; k++) {
						newMI[k] = maxIndeces[k];
					}
					maxIndeces = newMI;
				}
				maxIndeces[j] = i;
				j++;
			} else if (array[i] > array[maxIndeces[0]]) {
				maxIndeces = new int[1];
				maxIndeces[0] = i;
				j = 1;
			}
		}
		
		return maxIndeces;
	}

}
