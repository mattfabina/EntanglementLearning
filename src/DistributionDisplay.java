import java.awt.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class DistributionDisplay extends JPanel {
	
	private JFrame frame;
	private Controller cont;
	
	private int width;
	private int height;
	private int[] xPoints;
	private int[] yPoints;
	
	public DistributionDisplay(Controller cont, int[] xPoints, int[] yPoints) {
		super();
		this.frame = new JFrame("Graph");
		this.cont = cont;
		//constructs a container, and puts the content pane of the java frame within
		Container container = frame.getContentPane();
		//adds the GUI (within the JPanel superclass to the container)
		container.add(this);
		frame.addMouseListener(this.cont);
		this.width = 730;
		this.height = 730;
		
		this.xPoints = xPoints;
		this.yPoints = yPoints;
	}
	
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		this.update(graphics);
	}
	
	public void update(Graphics gS) {
		Graphics2D g = (Graphics2D)gS;
		
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, 730, 730);
		
		g.setColor(Color.RED);
		g.drawPolyline(xPoints, yPoints, xPoints.length);	//curve
		
		g.setColor(Color.BLACK);
		g.drawLine(79, 601, 601, 601);						//x axis
		g.drawLine(99, 99, 99, 621);						//y axis
		
		
		//dashed lines
		for (int i = 100; i < 600; i += 20) {
			g.drawLine(i, 100, i + 10, 100);
			g.drawLine(600, i, 600, i + 10);
		}
		
		
		
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
