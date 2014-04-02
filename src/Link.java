import java.awt.Color;
import java.awt.Graphics2D;

public class Link implements Drawable2D {

	private Link intraLink;
	private Link interLink;
	private int position;
	private boolean scoring;
	private boolean pathHead;
	
	public Link(int position) {
		this.position = position;
		scoring = false;
	}

	public void setIntra(Link l) {
		intraLink = l;
	}
	public void setInter(Link l) {
		interLink = l;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public void setScoring(boolean scoring) {
		this.scoring = scoring;
	}
	public void setPathHead(boolean isPathHead) {
		this.pathHead = isPathHead;
	}
	
	/**
	 * Returns the "tail" link if the link is head, or the link that would be the
	 * tail link if the link was a head.
	 * @return The tail link.
	 */
	public Link getTail() {
		if (intraLink == null)
			return this;
		Link l = intraLink;
		while (l.hasInter()) {
			l = l.getInter();
			if (l.hasIntra() && l != this) {
				l = l.getIntra();
				if (l == this)
					return l;
			}
			else
				return l;
		}
		return l;
	}
	public Link getIntra() {
		return intraLink;
	}
	public Link getInter() {
		return interLink;
	}
	public int getPosition() {
		return position;
	}
	public int getLengthToTail() {
		int length = 1;
		if (intraLink == null)
			return length;
		Link l = intraLink;
		length++;
		while (l.hasInter()) {
			l = l.getInter();
			length++;
			if (l.hasIntra() && l != this) {
				l = l.getIntra();
				length++;
			}
			else
				return length;
		}
		return length;
	}
	public boolean hasIntra() {
		return (intraLink != null);
	}
	public boolean hasInter() {
		return (interLink != null);
	}
	public boolean isPathHead() {
		return pathHead;
	}
	public boolean isTerminatingHead() {
		return (pathHead && intraLink == null);
	}
	public boolean isNonTerminatingHead() {
		return (pathHead && intraLink != null);
	}
	public boolean isScoring() {
		return scoring;
	}
	
	@Override
	public void drawMe(Graphics2D g, int xOffset, int yOffset) {
		g.setColor(Color.WHITE);

		int[] x = getXCoords(position, xOffset);
		int[] y = getYCoords(position, yOffset);
		
		if (scoring)
			g.setColor(Color.RED);
		else
			g.setColor(Color.WHITE);
		
		g.drawLine(x[0], y[0], x[1], y[1]);
		if (intraLink != null)
			g.drawLine(x[1], y[1], getXCoords(intraLink.getPosition(), xOffset)[1], 
					getYCoords(intraLink.getPosition(), yOffset)[1]);
		
		if (pathHead) {
			g.setColor(Color.BLUE);
			g.fillOval(x[1] - 4, y[1] - 4, 8, 8);
		}
	}
	
	private int[] getXCoords(int position, int xOffset) {
		int[] x = new int[2];
		
		switch (position) {
		case (0):
		case (5):
			x[0] = xOffset + 52;
			x[1] = xOffset + 52;
			break;
		case (1):
		case (4):
			x[0] = xOffset + 64;
			x[1] = xOffset + 60;
			break;
		case (2):
		case (3):
			x[0] = xOffset + 76;
			x[1] = xOffset + 72;
			break;
		case (6):
		case (11):
			x[0] = xOffset + 28;
			x[1] = xOffset + 28;
			break;
		case (7):
		case (10):
			x[0] = xOffset + 16;
			x[1] = xOffset + 20;
			break;
		case (8):
		case (9):
			x[0] = xOffset + 4;
			x[1] = xOffset + 8;
			break;
		}
		
		return x;
	}
	private int[] getYCoords(int position, int yOffset) {
		int[] y = new int[2];
		
		switch (position) {
		case (0):
		case (11):
			y[0] = yOffset + 0;
			y[1] = yOffset + 6;
			break;
		case (1):
		case (10):
			y[0] = yOffset + 6;
			y[1] = yOffset + 10;
			break;
		case (2):
		case (9):
			y[0] = yOffset + 24;
			y[1] = yOffset + 28;
			break;
		case (3):
		case (8):
			y[0] = yOffset + 36;
			y[1] = yOffset + 32;
			break;
		case (4):
		case (7):
			y[0] = yOffset + 54;
			y[1] = yOffset + 50;
			break;
		case (5):
		case (6):
			y[0] = yOffset + 60;
			y[1] = yOffset + 54;
			break;
		}
		
		return y;
	}
	
}
