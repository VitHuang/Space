package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
import shared.*;

public class GameScreen extends JPanel implements ActionListener {

	private static final long serialVersionUID = 42L;
	private ArrayList<DrawableObject> drawableObjects;
	private ArrayList<Animation> animations;
	private PhysicalObject centreObj;
	private final Timer timer = new Timer(33, this);
	private ParallaxBackground background;
	private Object updateLock;
	private int score;
	private long lastUpdateTime;

	public GameScreen() {
		background = new ParallaxBackground(World.WIDTH, World.HEIGHT);
		drawableObjects = new ArrayList<DrawableObject>();
		drawableObjects.add(null);
		animations = new ArrayList<Animation>();
		lastUpdateTime = System.currentTimeMillis();
		timer.start();
	}

	public void setUpdateLock(Object lock) {
		updateLock = lock;
	}

	public void dynamicallyResizeObjectList(int size) {
		while (drawableObjects.size() <= size) {
			int currentSize = drawableObjects.size();
			for (int i = 0; i < currentSize; i++) {
				drawableObjects.add(null);
			}
		}
	}

	public synchronized void addObject(PhysicalObject o) {
		dynamicallyResizeObjectList(o.id);
		if (o instanceof Spaceship) {
			drawableObjects.set(o.id, new DrawableShip((Spaceship) o));
		} else {
			drawableObjects.set(o.id, new DrawableObject(o));
		}
	}

	public synchronized void removeObject(int i) {
		dynamicallyResizeObjectList(i);
		drawableObjects.set(i, null);
	}

	public synchronized void addAnimation(Animation a) {
		animations.add(a);
	}

	public synchronized void update() {
		long time = System.currentTimeMillis();
		for (Drawable o : drawableObjects) {
			if (o != null) {
				o.update(getCentreX(), getCentreY());
			}
		}
		int i = 0;
		while (i < animations.size()) {
			Animation a = animations.get(i);
			if (a.isDead()) {
				animations.remove(i);
			} else {
				a.update((time - lastUpdateTime) / 1000.0, getCentreX(), getCentreY());
				i++;
			}
		}
		lastUpdateTime = time;
	}

	public void centreOn(PhysicalObject o) {
		centreObj = o;
	}

	public double getCentreX() {
		if (centreObj != null) {
			return centreObj.x;
		} else {
			return 0.0;
		}
	}

	public double getCentreY() {
		if (centreObj != null) {
			return centreObj.y;
		} else {
			return 0.0;
		}
	}

	public boolean onScreen(double x, double y) {
		double xDist = Math.abs(x - getCentreX());
		double yDist = Math.abs(y - getCentreY());
		if (xDist > World.WIDTH / 2) {
			xDist = Math.abs(xDist - World.WIDTH);
		}
		if (yDist > World.HEIGHT / 2) {
			yDist = Math.abs(xDist - World.HEIGHT);
		}
		return (xDist < getWidth() / 2) && (yDist < getHeight() / 2);
	}
	
	public void setScore(int s) {
		score = s;
	}

	@Override
	public synchronized void paintComponent(Graphics g) {
		synchronized (updateLock) {
			super.paintComponent(g);
			update();
			Graphics2D g2d = (Graphics2D) g;
			//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			//		RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setPaint(Color.WHITE);
			g2d.drawString("Score: " + score, 32, 64);
			AffineTransform transform = new AffineTransform();
			double cx = getCentreX();
			double cy = getCentreY();
			background.draw(g2d, cx, cy, getWidth(), getHeight());
			transform.translate(-cx + getWidth() / 2, -cy + getHeight() / 2);
			g2d.transform(transform);
			for (Drawable o : drawableObjects) {
				if (o != null) {
					o.draw(g2d);
				}
			}
			for (Animation a : animations) {
				a.draw(g2d);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}
