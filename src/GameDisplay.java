import java.awt.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class GameDisplay extends JPanel {
	
	private JFrame frame;
	private Controller cont;
	private Game g;
	
	private int width;
	private int height;
	
	public GameDisplay(Controller cont, Game g) {
		super();
		this.frame = new JFrame("Game Simulation");
		this.cont = cont;
		this.g = g;
		//constructs a container, and puts the content pane of the java frame within
		Container container = frame.getContentPane();
		//adds the GUI (within the JPanel superclass to the container)
		container.add(this);
		frame.addMouseListener(this.cont);
		this.width = 730;
		this.height = 730;
	}
	
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		this.update(graphics);
	}
	
	public void update(Graphics gS) {
		Graphics2D gr = (Graphics2D)gS;
		
		gr.setColor(Color.GRAY);
		gr.fillRect(0, 0, 730, 730);
		
		g.drawMe(gr, 0, 0);
	}
	
	public void close() {
		frame.dispose();
	}
	
	public void showMe() {
		Dimension dimension = new Dimension(width, height);
		// Set the size of the contents of the window so that we get the right size
		Container container = frame.getContentPane();
		container.setMaximumSize(dimension);
		container.setPreferredSize(dimension);
		container.setMinimumSize(dimension);
		// And now we can show the window
		frame.pack();
		frame.setVisible(true);
		this.repaint();
	}
	
	public void setLocation(int x, int y) {
	      frame.setLocation(x, y);
	}
	
}
