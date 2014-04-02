
public class WeightCombination {

	private double[] weights;
	private int gamesPlayed;
	private int totalScore;
	private int highScore;
	
	public WeightCombination(double[] weights, int gamesPlayed, int totalScore, int highScore) {		
		this.weights = weights;
	}
	
	public WeightCombination(String[] entries) {
		
		weights = new double[15];
				
		if (entries.length != 18)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < weights.length; i++) {
			weights[i] = Double.parseDouble(entries[i]);
		}
		gamesPlayed = Integer.parseInt(entries[15]);
		totalScore = Integer.parseInt(entries[16]);
		highScore = Integer.parseInt(entries[17]);
	}
	
	public void combine(WeightCombination wc) {
		totalScore += wc.getTotalScore();
		gamesPlayed += wc.getGamesPlayed();
		if (wc.getHighScore() > highScore)
			highScore = wc.getHighScore();
	}
	
	public int getTotalScore() {
		return totalScore;
	}
	
	public int getGamesPlayed() {
		return gamesPlayed;
	}
	
	public int getHighScore() {
		return highScore;
	}
	
	public double[] getWeights() {
		return weights;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass() != this.getClass())
			return false;
		
		WeightCombination wc = (WeightCombination) o;
		for (int i = 0; i < weights.length; i++) {
			if (wc.getWeights()[i] != weights[i])
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int ret = 0;
		for (int i = 0; i < weights.length; i++) {
			ret += (i * 1000 * weights[i]);
		}
		return ret;
	}
	
	@Override
	public String toString() {
		String s = new String();
		
		for (int i = 0; i < weights.length; i++)
			s += (weights[i] + ",");
		s += gamesPlayed + ",";
		s += totalScore + ",";
		s += highScore + ",";
		
		return s;
	}

}
