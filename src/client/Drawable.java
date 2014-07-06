package client;

import java.awt.Graphics2D;

public abstract class Drawable {
	
	public Drawable() {
	}
	
	public abstract void draw(Graphics2D g);
	
	public abstract void update(double cx, double cy);
}
