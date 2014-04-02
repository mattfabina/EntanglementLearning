import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class PathHeadList implements Iterable<Link>{

	private ArrayList<Link> pathHeads;
	private ArrayList<Link> addedHeads;
	private ArrayList<Link> removedHeads;
	
	public PathHeadList() {
		pathHeads = new ArrayList<Link>();
		removedHeads = new ArrayList<Link>();
		addedHeads = new ArrayList<Link>();
	}
	
	public void add(Link pathHead) {
		pathHeads.add(pathHead);
		pathHead.setPathHead(true);
		addedHeads.add(pathHead);
	}
	
	public void addAll(Collection<Link> heads) {
		pathHeads.addAll(heads);
		for (Link l: heads) {
			l.setPathHead(true);
		}
	}
	
	public void remove(Link pathHead) {
		if (!pathHeads.remove(pathHead)) {
			//per the updatePathHead method, this only triggers when an improper
			// tail is returned (i.e., when l.getTail() returns l)
			return;
		}
		pathHead.setPathHead(false);
		removedHeads.add(pathHead);
	}
	
	public int size() {
		return pathHeads.size();
	}
	
	public void reset() {
		pathHeads.removeAll(addedHeads);
		for (Link l: addedHeads)
			l.setPathHead(false);
		pathHeads.addAll(removedHeads);
		for (Link l: removedHeads)
			l.setPathHead(true);
		addedHeads = new ArrayList<Link>();
		removedHeads = new ArrayList<Link>();
	}

	/**
	 * Wipes the saved removed and added heads from the previous turn.
	 */
	public void wipe() {
		addedHeads = new ArrayList<Link>();
		removedHeads = new ArrayList<Link>();
	}
	
	@Override
	public Iterator<Link> iterator() {
		return pathHeads.iterator();
	}


}
