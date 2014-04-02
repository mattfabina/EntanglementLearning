import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class HashMemory {

	private ArrayList<WeightCombination>[] table;
	private int size;
	
	public HashMemory() {
		table = new ArrayList[61];
	}
	
	public HashMemory(File file) throws FileNotFoundException {
		loadMemory(file);
	}
	
	public void add(double[] weights, int gamesPlayed, int totalScore, int highScore) {
		WeightCombination wc = new WeightCombination(weights, gamesPlayed, totalScore, highScore);
		
		//case where memory does not exist
		if (table[Math.abs(wc.hashCode() % table.length)] == null) {
			if (((double) size + 1.0) / ((double) table.length) > .75) {
				resize();
				add(weights, gamesPlayed, totalScore, highScore);
			}
			else {
				table[Math.abs(wc.hashCode() % table.length)] = new ArrayList<WeightCombination>();
				table[Math.abs(wc.hashCode() % table.length)].add(wc);
				size ++;
			}
		}
		//case where memory exists
		else {
			ArrayList<WeightCombination> chain = table[Math.abs(wc.hashCode() % table.length)];
			
			boolean copied = false;
			for (int i = 0; i < chain.size() && !copied; i++) {
				if (chain.get(i).equals(wc)) {
					chain.get(i).combine(wc);
					copied = true;
				}
			}
			
			if (!copied) {
				if (chain.size() + 1 > 5) {
					resize();
					add(weights, gamesPlayed, totalScore, highScore);
				}
				else
					chain.add(wc);
			}
		}
	}
	
	public void saveMemory(File file) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(file);
		
		out.print(this);
		
		out.close();
	}
	
	private void resize() {
		//create the new ArrayList that will replace the old one
    	ArrayList<WeightCombination>[] newTable = new ArrayList[2*table.length + 1];
    	
    	//then iterate across the old table
    	for (int i = 0; i < table.length; i++) {
    		//if there's a chain at that index...
    		if (table[i] != null) {
    			//iterate through the chain, and hash the item into the new table
	    		for (WeightCombination wc: table[i]) {
	    			if (newTable[Math.abs(wc.hashCode() % newTable.length)] == null)
	    	    		newTable[Math.abs(wc.hashCode() % newTable.length)] = new ArrayList<WeightCombination>();
	    	        newTable[Math.abs(wc.hashCode() % newTable.length)].add(wc);
	    		}
    		}
    		
    		if (table[i] != null)
    			newTable[table[i].hashCode() % newTable.length] = table[i];
    	}

    	//finish the conversion process
    	table = newTable;
	}
	
	private void loadMemory(File file) throws FileNotFoundException {		
		Scanner in = new Scanner(file);
				
		table = new ArrayList[Integer.parseInt(in.nextLine())];
				
		int i = 0;
		while (in.hasNextLine()) {
			table[i] = new ArrayList<WeightCombination>();
						
			String[] entries = in.nextLine().split(";");
			table[i].add(new WeightCombination(entries));
			
			i++;
		}
	}
	
	@Override
	public String toString() {
		String s = new String();
		
		s += table.length + "\n";
		
		for (int i = 0; i < table.length; i++) {
			if (table[i] == null) {
				s += "null";
			}
			else {
				for (int j = 0; j < table[i].size(); j++)
					s += table[i].toString() + ";";
			}
			s += "\n";
		}
		
		return s;
	}

	
	public double[] getDecisionWeights() {
		return table[0].get(0).getWeights();
	}

}
