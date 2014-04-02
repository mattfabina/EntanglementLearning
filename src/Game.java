import java.awt.Graphics2D;

/**
 * 
 * 
 * @author mattfabina
 *
 */
public class Game implements Drawable2D {

	private Board b;
	private int score;
	
	public Game() {
		b = new Board();
	}
	
	public void makeMove(boolean useSwapTile) {		
		//make change to board
		score += b.placeTile(useSwapTile);
		
		b.makeNewTile();
		
		//System.out.println("pH size: " + b.getPathHeads().size());
	}

	public Board getBoard() {
		return b;
	}
	public int getScore() {
		return score;
	}
	public boolean isOver() {
		return b.isFull();
	}
	
	/**
	 * Prints pertinent board information
	 */
	public void analyzeBoard() {		
	}

	@Override
	public void drawMe(Graphics2D g, int xOffset, int yOffset) {
		b.drawMe(g, 20, 20);
	}

}
