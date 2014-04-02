import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;

public class Slot implements Drawable2D {

	private Tile occupant;
	private int column;
	private int row;
	private boolean borderSlot;
	
	private int xOffset;
	private int yOffset;
	
	public Slot(int row, int column, int xOffset, int yOffset) {
		this.row = row;
		this.column = column;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
		//initiate the tile if it's a border slot
		int[] tPP = terminatingPathPositions(row, column);
		if (tPP[0] != -1) {
			occupant = new Tile(tPP);
			borderSlot = true;
		}
	}
	
	public void setOccupant(Tile t) {
		occupant = t;
	}
	
	public Tile removeOccupant() {
		Tile t = occupant;
		occupant = null;
		return t;
	}
	public Tile getOccupant() {
		return occupant;
	}
	public int getColumn() {
		return column;
	}
	
	public int getRow() {
		return row;
	}
	public boolean isBorderSlot() {
		return borderSlot;
	}
	public boolean isOccupied() {
		return (occupant != null);
	}
	
	public ArrayList<Link> getTerminatingLinks() {
		ArrayList<Link> tLinks = new ArrayList<Link>();
		int[] tPP = terminatingPathPositions(row, column);
		for (int i: tPP)
			tLinks.add(occupant.getLink(i));
		return tLinks;
	}
	
	
	/**
	 * Returns an int array of the positions that are terminating paths for
	 *  the given slot. Values are only returned for border slots.
	 *  
	 * @param i The column of the slot.
	 * @param j The row of the slot.
	 * @return the positions of the terminating paths.
	 */
	private int[] terminatingPathPositions(int i, int j) {
		//i's are rows
		//j's are columns
		switch (i) {
		//first row
		case 0:
			switch (j) {
			case 3:
				return new int[] {3, 4, 5, 6};
			case 4:
				return new int[] {5, 6};
			case 5:
				return new int[] {5, 6, 7, 8};
			}
			return new int[] {-1};
		//second row
		case 1:
			switch (j) {
			case 1:
			case 2:
				return new int[] {3, 4, 5, 6};
			case 6:
			case 7:
				return new int[] {5, 6, 7, 8};
			}
			return new int[] {-1};
		//third row
		case 2:
			switch (j) {
			case 0:
				return new int[] {3, 4};
			case 8:
				return new int[] {7, 8};
			}
			return new int[] {-1};
		//fourth row, sixth row (identical cases)
		case 3:
		case 5:
			switch (j) {
			case 0:
				return new int[] {1, 2, 3, 4};
			case 8:
				return new int[] {7, 8, 9, 10};
			}
			return new int[] {-1};
		//fifth, middle row
		case 4:
			switch (j) {
			case 0:
				return new int[] {1, 2, 3, 4};
			case 4:
				return new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
			case 8:
				return new int[] {7, 8, 9, 10};
			}
			return new int[] {-1};
		//seventh row
		case 6:
			switch (j) {
			case 0:
				return new int[] {1, 2};
			case 1:
				return new int[] {11, 0, 1, 2};
			case 7:
				return new int[] {9, 10, 11, 0};
			case 8:
				return new int[] {9, 10};
			}
			return new int[] {-1};
		//eighth row
		case 7:
			switch (j) {
			case 2:
			case 3:
				return new int[] {11, 0, 1, 2};
			case 5:
			case 6:
				return new int[] {9, 10, 11, 0};
			}
			return new int[] {-1};
		//ninth row
		case 8:
			switch (j) {
			case 4:
				return new int[] {11, 0};
			}
			return new int[] {-1};
		}
		
		return new int[] {-1};
	}

	@Override
	public void drawMe(Graphics2D g, int xOffset, int yOffset) {
		//draw the slot
		Polygon p = new Polygon();
	
		p.addPoint(xOffset + this.xOffset + 0,	 	yOffset + this.yOffset + 30);
		p.addPoint(xOffset + this.xOffset + 20,  	yOffset + this.yOffset + 0);
		p.addPoint(xOffset + this.xOffset + 60, 	yOffset + this.yOffset + 0);
		p.addPoint(xOffset + this.xOffset + 80, 	yOffset + this.yOffset + 30);
		p.addPoint(xOffset + this.xOffset + 60, 	yOffset + this.yOffset + 60);
		p.addPoint(xOffset + this.xOffset + 20,  	yOffset + this.yOffset + 60);
		p.addPoint(xOffset + this.xOffset + 0,  	yOffset + this.yOffset + 30);
		
		g.setColor(Color.GRAY);
		g.fillPolygon(p);
		g.setColor(Color.BLACK);
		g.drawPolygon(p);
		
		//draw the tile
		if (occupant != null)
			occupant.drawMe(g, xOffset + this.xOffset, yOffset + this.yOffset);
	}
	
}
