package GUI;

import javax.swing.JFrame;

public class MainFrame extends JFrame{
	
	Thread t;
	
	public MainFrame ()
	{
		this.setTitle("pianist");
		this.setSize(1200, 1400);
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
