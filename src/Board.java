import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A "board" data structure that deals with Tiles and Slots
 * 
 * The order of the slots appears as the diagram that follows:
 * 
 *   0	 60   120  180  240  300  360  420  480
 * (///)	 (///)	   (0,4)	 (///)	   (///)	//offset 0
 * 		(///)	  (0,3)	 |	(0,5)	  (///)			//offset 30
 * (///)	 (1,2)	|  (1,4)  |	 (1,6)	   (///)	//offset 60
 * 		(1,1)  |  (1,3)	 |	(1,5)  |  (1,7)			//offset 90
 * (2,0)  |	 (2,2)	|  (2,4)  |	 (2,6)	   (2,8)	//offset 120
 * 	 	(2,1)  |  (2,3)		(2,5)	  (2,7)			//offset 150
 * (3,0)  |	 (3,2)	|  (3,4)	 (3,6)	   (3,8)	//offset 180
 * 	 	(3,1)  |  (3,3)		(3,5)	  (3,7)			//offset 210
 * (4,0)  |	 (4,2)	|  (4,4)	 (4,6)	   (4,8)	//offset 240
 * 		(4,1)  |  (4,3)		(4,5)	  (4,7)			//offset 270
 * (5,0)  |	 (5,2)	|  (5,4)	 (5,6)	   (5,8)	//offset 300
 * 		(5,1)  |  (5,3)		(5,5)	  (5,7)			//offset 330
 * (6,0)  |	 (6,2)	   (6,4)	 (6,6)	   (6,8)	//offset 360
 * 		(6,1)  |  (6,3)		(6,5)	  (6,7)			//offset 390
 * (///)	 (7,2)	   (7,4)	 (7,6)	   (///)	//offset 420
 * 		(///)	  (7,3)		(7,5)	  (///)			//offset 450
 * (///)	 (///)	   (8,4)	 (///)	   (///)	//offset 480
 * 		(///)	  (///)		(///)	  (///)			//offset 510
 * 
 * @author mattfabina
 *
 */
public class Board implements Drawable2D {
	
	private PathHeadList pathHeads;
	private Slot[][] slots;
	private Slot prevSlot;
	private Slot currentSlot;
	private Tile currentTile;
	private Tile swapTile;
	private Link prevLink;
	private Link currentLink;
	private boolean full;
	
	public Board() {
		slots = constructSlotArray();
		currentSlot = slots[3][4];
		currentTile = new Tile();
		swapTile = new Tile();
		currentLink = slots[4][4].getOccupant().getLink(11);
		currentLink.setScoring(true);
		full = false;
		pathHeads = new PathHeadList();
		buildPathHeads();
	}
		
	public void makeNewTile() {
		if (currentTile == null) 
			currentTile = new Tile();
		else if (swapTile == null)
			swapTile = new Tile();
	}
	/**
	 * This operation can only be performed once per turn, i.e. a tile can be placed
	 *  and removed, but a tile can not be removed if no tile has been placed,
	 *  or a placed tile has already been removed.
	 */
	public void removeTile() {
		full = false;
		//reset pathHeads to its former state
		pathHeads.reset();
		
		//reset current position
		while (currentLink != prevLink) {
			currentLink.setScoring(false);
			currentLink.getIntra().setScoring(false);
			currentLink = currentLink.getIntra().getInter();
		}
		
		//reset links
		for (int i = 0; i < 12; i ++) {
			//reset the current link, if it it's linked
			if (prevSlot.getOccupant().getLink(i).getInter() != null) {
				//eliminate the inter links, on both sides
				prevSlot.getOccupant().getLink(i).getInter().setInter(null);
				prevSlot.getOccupant().getLink(i).setInter(null);
			}
		}
		
		//remove tile
		if (currentTile == null) 
			currentTile = prevSlot.removeOccupant();
		else if (swapTile == null)
			swapTile = prevSlot.removeOccupant();
		
		//reset slot
		currentSlot = prevSlot;
	}
	
	public PathHeadList getPathHeads() {
		return pathHeads;
	}
	public Tile getCurrentTile() {
		return currentTile;
	}
	public Tile getSwapTile() {
		return swapTile;
	}
	public int placeTile(boolean useSwapTile) {
		int score = 0;
		
		//make new tiles if they don't exist
		makeNewTile();

		//place the tile
		if (!useSwapTile) {
			currentSlot.setOccupant(currentTile);
			currentTile = null;
		} else {
			currentSlot.setOccupant(swapTile);
			swapTile = null;
		}
		
		//update links
		for (int i = 0; i < 12; i ++) {
			//set the current link to its connection, if it has an occupant
			if (getAdjacentSlot(i).isOccupied()) {
				currentSlot.getOccupant().getLink(i).setInter
						( getAdjacentSlot(i).getOccupant().getLink(Tile.getComplement(i)) );
				//set the connection to the current link
				getAdjacentSlot(i).getOccupant().getLink(Tile.getComplement(i)).setInter
						( currentSlot.getOccupant().getLink(i) );
			}
		}
		
		//update path head collection
		updatePathHeads();
		
		//update current position
		prevLink = currentLink;
		prevSlot = currentSlot;
		int scoreInc = 0;
		while (currentLink.hasInter()) {
			if (currentLink.getInter().getIntra() == null) {
				full = true;
				return score;
			}
			currentLink = currentLink.getInter().getIntra();
			currentLink.setScoring(true);
			currentLink.getIntra().setScoring(true);
			
			scoreInc ++;
			score += scoreInc;
			
			//update slot
			currentSlot = getAdjacentSlot(currentLink.getPosition());
		}
		
		return score;
	}
	public boolean isFull() {
		return full;
	}
	
	/**
	 * Returns the next slot based on the position given from the current slot.
	 * 
	 * @return the next slot based on the position given from the current slot.
	 */
	private Slot getAdjacentSlot(int currentSlotPos) {
		switch (currentSlotPos) {
		case 0:
		case 11:
			return slots[currentSlot.getRow()-1][currentSlot.getColumn()];
		case 1:
		case 2:
			if (currentSlot.getColumn() % 2 == 0) { //even
				return slots[currentSlot.getRow()-1][currentSlot.getColumn()+1];
			} else {
				return slots[currentSlot.getRow()][currentSlot.getColumn()+1];
			}
		case 3:
		case 4:
			if (currentSlot.getColumn() % 2 == 0) { //even
				return slots[currentSlot.getRow()][currentSlot.getColumn()+1];
			} else {
				return slots[currentSlot.getRow()+1][currentSlot.getColumn()+1];
			}
		case 5:
		case 6:
			return slots[currentSlot.getRow()+1][currentSlot.getColumn()];
		case 7:
		case 8:
			if (currentSlot.getColumn() % 2 == 0) { //even
				return slots[currentSlot.getRow()][currentSlot.getColumn()-1];
			} else {
				return slots[currentSlot.getRow()+1][currentSlot.getColumn()-1];
			}
		case 9:
		case 10:
			if (currentSlot.getColumn() % 2 == 0) { //even
				return slots[currentSlot.getRow()-1][currentSlot.getColumn()-1];
			} else {
				return slots[currentSlot.getRow()][currentSlot.getColumn()-1];
			}
		default:
			throw new RuntimeException("lastSlotScoringPos out of range.");
		}
		
	}
	private Slot[][] constructSlotArray() {
		//slot[i] are rows i
		//slot[i][j] are columns j
		Slot[][] slots = {
				{ //0
					null,
					null,
					null,
					new Slot(0, 3, 180, 30),
					new Slot(0, 4, 240, 0),
					new Slot(0, 5, 300, 30),
					null,
					null,
					null
				},
				{ //1
					null,
					new Slot(1, 1, 60, 90),
					new Slot(1, 2, 120, 60),
					new Slot(1, 3, 180, 90),
					new Slot(1, 4, 240, 60),
					new Slot(1, 5, 300, 90),
					new Slot(1, 6, 360, 60),
					new Slot(1, 7, 420, 90),
					null
				},
				{ //2
					new Slot(2, 0, 0, 120),
					new Slot(2, 1, 60, 150),
					new Slot(2, 2, 120, 120),
					new Slot(2, 3, 180, 150),
					new Slot(2, 4, 240, 120),
					new Slot(2, 5, 300, 150),
					new Slot(2, 6, 360, 120),
					new Slot(2, 7, 420, 150),
					new Slot(2, 8, 480, 120)
				},
				{ //3
					new Slot(3, 0, 0, 180),
					new Slot(3, 1, 60, 210),
					new Slot(3, 2, 120, 180),
					new Slot(3, 3, 180, 210),
					new Slot(3, 4, 240, 180),
					new Slot(3, 5, 300, 210),
					new Slot(3, 6, 360, 180),
					new Slot(3, 7, 420, 210),
					new Slot(3, 8, 480, 180)
				},
				{ //4
					new Slot(4, 0, 0, 240),
					new Slot(4, 1, 60, 270),
					new Slot(4, 2, 120, 240),
					new Slot(4, 3, 180, 270),
					new Slot(4, 4, 240, 240), //4 (the start)
					new Slot(4, 5, 300, 270),
					new Slot(4, 6, 360, 240),
					new Slot(4, 7, 420, 270),
					new Slot(4, 8, 480, 240)
				},
				{ //5
					new Slot(5, 0, 0, 300),
					new Slot(5, 1, 60, 330),
					new Slot(5, 2, 120, 300),
					new Slot(5, 3, 180, 330),
					new Slot(5, 4, 240, 300),
					new Slot(5, 5, 300, 330),
					new Slot(5, 6, 360, 300),
					new Slot(5, 7, 420, 330),
					new Slot(5, 8, 480, 300)
				},
				{ //6
					new Slot(6, 0, 0, 360),
					new Slot(6, 1, 60, 390),
					new Slot(6, 2, 120, 360),
					new Slot(6, 3, 180, 390),
					new Slot(6, 4, 240, 360),
					new Slot(6, 5, 300, 390),
					new Slot(6, 6, 360, 360),
					new Slot(6, 7, 420, 390),
					new Slot(6, 8, 480, 360)
				},
				{ //7
					null,
					null,
					new Slot(7, 2, 120, 420),
					new Slot(7, 3, 180, 450),
					new Slot(7, 4, 240, 420),
					new Slot(7, 5, 300, 450),
					new Slot(7, 6, 360, 420),
					null,
					null
				},
				{ //8
					null,
					null,
					null,
					null,
					new Slot(8, 4, 240, 480),
					null,
					null,
					null,
					null
				},
		};
		
		return slots;
	}

	@Override
	public void drawMe(Graphics2D g, int xOffset, int yOffset) {
		//draw the board outline
		g.setColor(new Color(205, 192, 176)); //sand white
		g.fillRoundRect(xOffset, yOffset, 690, 690, 20, 20);
		
		//draw the slots
		for (int i = 0; i < slots.length; i++) {
			for (Slot s: slots[i]) {
				if (s != null)
					s.drawMe(g, xOffset + 65, yOffset + 75);
			}
		}
	}	

	private void buildPathHeads() {
		for (int i = 0; i < slots.length; i++) {
			for (Slot s: slots[i]) {
				if (s != null && s.isBorderSlot()) {
					pathHeads.addAll(s.getTerminatingLinks());
				}
			}	
		}
	}

	private void updatePathHeads() {
		//wipe the reset from the previous turn
		pathHeads.wipe();
		
		for (int i = 0; i < 6; i++) {
			Link curr = currentSlot.getOccupant().getUniqueLink(i);
			
			//Asterisk denotes uncertainty of termination
			//Case 1: no existing paths
			//			 	  | N |
			if (!curr.hasInter() && !curr.getIntra().hasInter()) {
				//add curr as a new path head
				pathHeads.add(curr);
			}
			
			//Case 2: existing path on curr's side
			//*[---][---][--o]| N |
			//*[o--][---][---]| N |		
			else if (curr.hasInter() && !curr.getIntra().hasInter()) {
				// [---][---][--o]| N |
				if (curr.getInter().isNonTerminatingHead()) {
					//remove the old path head, add intra
					pathHeads.remove(curr.getInter());
					pathHeads.add(curr.getIntra());
				}

				//			   [T]| N |
				//*[o--][---][---]| N |		
				//no changes necessary
			}
			
			//Case 3: existing path on intra's side
			//				  | N |[o--][---][---]*		
			//  			  | N |[---][---][--o]*
			else if (!curr.hasInter() && curr.getIntra().hasInter()) {
				//				  | N |[o--][---][---]
				if (curr.getIntra().getInter().isNonTerminatingHead()) {
					//remove the old path head, add curr
					pathHeads.remove(curr.getIntra().getInter());
					pathHeads.add(curr);
				}
				
				//				  | N |[T]
				//  			  | N |[---][---][--o]*
				//no changes necessary
			}
			
			//Case 4: existing path on both sides
			//*[---][---][--o]| N |[o--][---][---]*(sub-case 1)
			//*[---][---][--o]| N |[---][---][--o]*(sub-case 2)
			//*[o--][---][---]| N |[o--][---][---]*(sub-case 3)
			//*[o--][---][---]| N |[---][---][--o]*(sub-case 4)
			else if (curr.hasInter() && curr.getIntra().hasInter()) {
				//sub-case 1:
				//*[---][---][--o]| N |[o--][---][---]*
				if (curr.getInter().isPathHead() && curr.getIntra().getInter().isPathHead()) {
					//			   [T]| N |[o--][---][---]*
					if (curr.getInter().isTerminatingHead()) {
						//			   [T]| N |[T]
						if (curr.getIntra().getInter().isTerminatingHead()) {
							pathHeads.remove(curr.getInter());
							pathHeads.remove(curr.getIntra().getInter());
						}
						//			   [T]| N |[o--][---][---]
						else {
							pathHeads.remove(curr.getIntra().getInter());
						}
					}
					// [---][---][--o]| N |[o--][---][---]*
					else {
						// [---][---][--o]| N |[T]
						if (curr.getIntra().getInter().isTerminatingHead()) {
							pathHeads.remove(curr.getInter());
						}
						// [---][---][--o]| N |[o--][---][---]
						else {
							pathHeads.remove(curr.getInter());
							pathHeads.remove(curr.getIntra().getInter());
							pathHeads.add(curr.getInter().getTail());
						}
					}
				}
				
				//sub-case 2:
				//*[---][---][--o]| N |[---][---][--o]*
				else if (curr.getInter().isPathHead() && !curr.getIntra().getInter().isPathHead()) {
					//			   [T]| N |[---][---][--o]*
					if (curr.getInter().isTerminatingHead()) {
						Link l = curr.getTail();
						//			   [T]| N |[---][---][T]
						if (l.isTerminatingHead()) {
							pathHeads.remove(curr.getInter());
							pathHeads.remove(l);
						}
						//			   [T]| N |[---][---][--o]
						else {
							pathHeads.remove(l);
						}
					}
					// [---][---][--o]| N |[---][---][--o]*
					else {
						pathHeads.remove(curr.getInter());
					}
				}
				
				//sub-case 3:
				//*[o--][---][---]| N |[o--][---][---]*
				else if (!curr.getInter().isPathHead() && curr.getIntra().getInter().isPathHead()) {
					//*[o--][---][---]| N |[T]
					if (curr.getIntra().getInter().isTerminatingHead()) {
						Link l = curr.getInter().getTail();
						//   [T][---][---]| N |[T]
						if (l.isTerminatingHead()) {
							pathHeads.remove(l);
							pathHeads.remove(curr.getIntra().getInter());
						}
						// [o--][---][---]| N |[T]
						else {
							pathHeads.remove(l);
						}
					}
					//*[o--][---][---]| N |[o--][---][---]
					else {
						pathHeads.remove(curr.getIntra().getInter());
					}
				}
				
				//sub-case 4:
				//*[o--][---][---]| N |[---][---][--o]*
				else if (!curr.getInter().isPathHead() && !curr.getIntra().getInter().isPathHead()) {					
					Link l = curr.getInter().getTail();
					//   [T][---][---]| N |[---][---][--o]*
					if (l.isTerminatingHead()) {
						Link k = curr.getTail();
						//   [T][---][---]| N |[---][---][T]
						if (k.isTerminatingHead()) {
							pathHeads.remove(l);
							pathHeads.remove(k);
						}
						//   [T][---][---]| N |[---][---][--o]
						else {
							pathHeads.remove(k);
						}
					}
					// [o--][---][---]| N |[---][---][--o]*
					else {
						pathHeads.remove(l);
					}
				}
			}
		}
		
	}
	
}
