import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Random;

/**
 * A tile, whose links appear in the following order:
 * 		  _ _ _ _ _ _ _ _
 * 		 /   [11]  [0] 	  \
 * 		/				   \
 *     /[10]			 [1]\
 *    /						 \
 *   /[9]				   [2]\
 *  /						   \				   
 * 	 \						  /
 * 	  \[8]				  [3]/
 * 	   \			        /
 * 		\[7]  	  		[4]/
 * 		 \
 * 		  \ _ [6]  [5] _ / 
 * 
 * @author mattfabina
 *
 */

public class Tile implements Drawable2D {
	
	/**
	 * 
	 * @param position
	 * @return 
	 */
	public static int getComplement(int position) {
		switch (position) {
		case 0:
			return 5;
		case 1:
			return 8;
		case 2:
			return 7;
		case 3:
			return 10;
		case 4:
			return 9;
		case 5:
			return 0;
		case 6:
			return 11;
		case 7:
			return 2;
		case 8:
			return 1;
		case 9:
			return 4;
		case 10:
			return 3;
		case 11:
			return 6;
		}
		
		return -1;
	}
	
	private Link[] links;
	private int[] uniqueLinks;
	private boolean borderTile;		//for drawing purposes

	/**
	 * Case for standard tiles.
	 */
	public Tile () {
		borderTile = false;
		links = new Link[12];
		uniqueLinks = new int[6];
		
		for (int i = 0; i < links.length; i++) {
			links[i] = new Link(i);
		}
		
		setUpLinks();
	}
	/**
	 * Case for border tiles.
	 * 
	 * @param column
	 * @param row
	 */
	public Tile (int[] terminatingPathPositions) {
		borderTile = true;
		links = new Link[terminatingPathPositions.length];
		
		for (int i = 0; i < links.length; i++) {
			links[i] = new Link(terminatingPathPositions[i]);
		}
	}
	
	private void setUpLinks() {
		Random r = new Random();
		
		for (int i = 0; i < (links.length / 2); i++) {
			int pos1 = 0;
			int pos2 = 0;
			
			pos1 = r.nextInt(12);
			while (links[pos1].hasIntra()) {
				pos1 = r.nextInt(12);
			}
			
			pos2 = r.nextInt(12);
			while (links[pos2].hasIntra() || pos1 == pos2) {
				pos2 = r.nextInt(12);
			}
			
			uniqueLinks[i] = pos1;
			links[pos1].setIntra(links[pos2]);
			links[pos2].setIntra(links[pos1]);			
		}
	}
	/**
	 * 0 = normal
	 * 1 = [0] -> [2]
	 * 2 = [0] -> [4]
	 * 3 = [0] -> [6]...
	 * @param orientation Valid integer between 0 and 5 inclusive.
	 */
	public void rotate(int o) {
		Link[] copy = new Link[12];
		o *= 2;
		
		for (int i = 0; i < 12; i++) {
			if (i - o >= 0)
				copy[i] = links[i-o];
			else
				copy[i] = links[i - o + 12];
			
			copy[i].setPosition(i);
		}
		
		for (int i = 0; i < 6; i++) {
			if (uniqueLinks[i] + o > 11)
				uniqueLinks[i] = uniqueLinks[i] + o - 12;
			else
				uniqueLinks[i] = uniqueLinks[i] + o;
		}
		
		links = copy;
	}
	
	public Link getLink(int position) {
		if (links.length == 12)
			return links[position];
		else {
			for (Link l: links) {
				if (l.getPosition() == position)
					return l;
			}
		}

		return null;
	}
	
	public Link getUniqueLink(int i) {
		return links[uniqueLinks[i]];
	}
	
	@Override
	public void drawMe(Graphics2D g, int xOffset, int yOffset) {
		//draw the tile
		Polygon p = new Polygon();
		
		p.addPoint(xOffset + 0, 	yOffset + 30);
		p.addPoint(xOffset + 20, 	yOffset + 0);
		p.addPoint(xOffset + 60, 	yOffset + 0);
		p.addPoint(xOffset + 80, 	yOffset + 30);
		p.addPoint(xOffset + 60, 	yOffset + 60);
		p.addPoint(xOffset + 20, 	yOffset + 60);
		p.addPoint(xOffset + 0,  	yOffset + 30);
		
		if (borderTile)
			g.setColor(new Color(0, 100, 0)); //dark green
		else
			g.setColor(new Color(75,139,75)); //lighter green
		g.fillPolygon(p);
		g.setColor(Color.BLACK);
		g.drawPolygon(p);
		
		//draw the links
		for (Link l: links) {
			l.drawMe(g, xOffset, yOffset);
		}
	}
	
}
