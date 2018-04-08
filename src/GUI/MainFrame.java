package GUI;

import javax.swing.JFrame;

public class MainFrame extends JFrame{
	private static final double VERSION = 2.1;
	
	Thread t;
	
	public MainFrame ()
	{
		this.setTitle("pianist");
		this.setSize(900, 1050); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		PaintPanel paintPanel = new PaintPanel();
		this.add(paintPanel);
		this.setResizable(false);
		paintPanel.setFocusable(true);
		paintPanel.setRequestFocusEnabled(true);
		paintPanel.addMouseListener(paintPanel);
		paintPanel.addMouseMotionListener(paintPanel);
		t = new Thread(paintPanel);
		t.start();
	}
}
