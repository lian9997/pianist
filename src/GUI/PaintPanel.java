package GUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

import model.*;

public class PaintPanel extends JPanel implements MouseMotionListener, MouseInputListener, Runnable {
	private static final double VERSION = 2.1;

	private static final int SCREEN_MAX_Y = 760;
	private static final int SCREEN_MIN_Y = 200;
	private static final int SCREEN_MAX_X = 910;
	private LinkedList<Point> points;
	private LinkedList<Color> colors;
	private Color currentColor;
	private final Color goodBlue = new Color(87, 173, 213);
	private final Color goodPink = new Color(241, 157, 183);
	private final Color goodGreen = new Color(192, 224, 213);
	private final Color goodYellow = new Color(231, 207, 145);
	private final Color goodCyan = new Color(111, 201, 191);
	private final Color goodword = new Color(133, 190, 215);
	private final int DIST_LIMIT = 50;
	private final Font instFont = new Font("chalkduster", Font.ITALIC, 24);
	private final Font saveFont = new Font("chalkduster", Font.ITALIC, 48);

	private Point lastHit = null;
	private Image img;
	private String save;
	private String instname;
	private int instnum;
	private Model model;
	private Thread playallThread;
	private Thread endThread;

	public PaintPanel() {
		points = new LinkedList<Point>();
		colors = new LinkedList<Color>();
		model = new Model();
		save = "save";
		currentColor = goodGreen;
		instnum = 0;
		instname = "piano";
		try {
			img = ImageIO.read(new File("background.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paint(Graphics g) {
		int i = 0;

		// g.clearRect(0, 0, 1400, 1200);

		g.drawImage(img, 0, 0, 900, 1060, 0, 0, 800, 960, this);

		Point prev = null;
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		// instruments
		// g2.setFont(instFont);
		// g2.setColor(goodword);
		// g2.drawString(instname, 340, 100);

		if (!colors.isEmpty())
			g.setColor(colors.get(0));

		for (Point p : points) {
			if (p != null) {
				if (prev != null)
					g.drawLine(prev.x, prev.y, p.x, p.y);
				else
				{
					g.setColor(colors.get(i++));
					g.drawLine(p.x, p.y, p.x, p.y);
				}
				prev = p;
			} else {
				prev = null;
			}
		}
		// points.clear();
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		Point now = event.getPoint();
		if (isInRange(now)) {
			if(lastHit == null)
			{
				lastHit = event.getPoint();
				model.setOriginNote((int) (((double) SCREEN_MAX_Y - lastHit.y) / SCREEN_MAX_Y * 20) + 60);
				colors.add(currentColor);
				points.add(now);
			}
			points.add(event.getPoint());
			if (now.distance(lastHit) > DIST_LIMIT) {
				double angle;

				if (now.x - lastHit.x == 0)
					angle = now.y > lastHit.y ? 270 : 90;
				else
					angle = Math.atan(((double) now.y - lastHit.y) / ((double) now.x - lastHit.x)) * 57.2957795131;

				if (now.x > lastHit.x && now.y > lastHit.y) {
					angle = 360.0 - angle;
				} else if (now.x < lastHit.x && now.y > lastHit.y) {
					angle = 180.0 - angle;
				} else if (now.x > lastHit.x && now.y < lastHit.y) {
					angle = -angle;
				} else {
					angle = 180.0 - angle;
				}
				model.nextStep((int) angle);
				lastHit = now;
			}
		}
	}

	private boolean isInRange(Point p) {
		if (p.x > 0 && p.x < SCREEN_MAX_X && p.y > SCREEN_MIN_Y && p.y < SCREEN_MAX_Y)
			return true;
		return false;
	}

	@Override
	public void mouseMoved(MouseEvent event) {
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		Point p = event.getPoint();
		if (p.x > 0 && p.x < SCREEN_MAX_X && p.y > 0 && p.y < SCREEN_MIN_Y) {
			// up
			if (p.x < SCREEN_MAX_X / 4) {
				points = new LinkedList<Point>();
				colors = new LinkedList<Color>();
				model = new Model();
			} else if (p.x < SCREEN_MAX_X / 2) {
				/*
				 * instnum = (instnum + 1) % 128; model.setInstrucment(instnum); instname =
				 * model.getInstrumentName(instnum);
				 */
			} else if (p.x < SCREEN_MAX_X / 4*3) {
				model.saveAudio();
				save();
				model.setInstrucment(instnum);
			} else if (p.x < 720) {
//do nothing
			} else {
				playallThread = new Thread(() -> model.playAll());
				playallThread.start();
			}

		}
		if (p.y > SCREEN_MAX_Y && p.y < SCREEN_MAX_Y + 200 && p.x > 0 && p.x < SCREEN_MAX_X) {
			// bottom
			if (p.x < SCREEN_MAX_X/5) {
				currentColor = goodGreen;
				instnum = 0;
				instname = "piano";
			} else if (p.x < SCREEN_MAX_X/5*2) {
				currentColor = goodYellow;
				instnum = 34;
			} else if (p.x < SCREEN_MAX_X/5*3) {
				currentColor = goodPink;
				instnum = 32;
			} else if (p.x < SCREEN_MAX_X/5*4) {
				currentColor = goodCyan;
				instnum = 39;
			} else {
				currentColor = goodBlue;
				instnum = 38;
			}
			model.setInstrucment(instnum);
			instname = model.getInstrumentName(instnum);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (endThread != null) {
			endThread.interrupt();
			Thread.interrupted();
			endThread = null;
		}
		if (isInRange(event.getPoint())) {
			if (!save.equals("save"))
				save = "save";
			lastHit = event.getPoint();
			model.setOriginNote((int) (((double) SCREEN_MAX_Y - lastHit.y) / SCREEN_MAX_Y * 20) + 60);
			colors.add(currentColor);
			Point now;
			points.add(now = event.getPoint());
			
		}
		else
		{
			lastHit = null;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		points.add(null);
		if (isInRange(arg0.getPoint()))
			;// model.release();

	}

	private void save() {
		BufferedImage bImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D cg = bImg.createGraphics();
		save = "saving";
		this.paintAll(cg);
		try {
			int i = 0;

			String name = "saved_imace.png";
			File f = new File(name);
			while (f.exists()) {
				name = "saved_image" + i++ + ".png";
				f = new File(name);
			}

			if (ImageIO.write(bImg, "png", new File(name))) {
				save = "saved";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			save = "save faild";
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(50);
				this.repaint();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
