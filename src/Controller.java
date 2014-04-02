import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class Controller extends Thread implements MouseListener {

	private LearningComputer lc;
	private Game g;
	private JPanel display;
	private int waitTime;
	private boolean quitting;
	private boolean game;
	
	public Controller(int waitTime) {
		this.waitTime = waitTime;
	}
	
	public Controller(LearningComputer lc, Game g, int waitTime) {
		this.g = g;
		this.waitTime = waitTime;
		this.lc = lc;
		game = true;
	}
	
	public void run() {
		if (game)
			runGame();
		else
			runDistribution();
	}
	
	private void runGame() {
		double[] weights = {1.0,0.01,-0.1,-0.1,-0.1,-0.1,-0.1,-0.1,0.1,
				0.1,0.1,0.1,0.1,0.1,0.0};
		
		while (!quitting) {
			long currTime = System.currentTimeMillis();

			if (!g.isOver()) {
				g.makeMove(lc.getMove(g.getBoard(), weights));
			} else {
				quitting = true;
			}

			display.repaint();
			
			while (System.currentTimeMillis() < currTime + waitTime);

		}
	}
	
	private void runDistribution() {
		long startTime = System.currentTimeMillis();
		
		while (!quitting) {
			display.repaint();
			
			if (System.currentTimeMillis() > startTime + ((long) waitTime))
				quitting = true;
		}
	}
	
	public void setDisplay(JPanel display) {
		this.display = display;
	}

	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

}
