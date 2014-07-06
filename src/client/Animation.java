package client;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import shared.World;

public abstract class Animation {
	
	protected double t;
	public double x;
	public double y;
	public double vx;
	public double vy;
	public double lifeSpan;
	public AffineTransform transform;
	
	public Animation() {
		transform = new AffineTransform();
		t = 0;
	}
	
	public void update(double dt, double cx, double cy) {
		t += dt;
		transform.setToIdentity();
		transform.translate(x + t * vx, y + t * vy);
		double dx = (x + t * vx) - cx;
		double dy = (y + t * vy) - cy;
		while (dx >= World.WIDTH / 2) {
			dx -= World.WIDTH;
			transform.translate(-World.WIDTH, 0);
		}
		while (dx < -World.WIDTH / 2) {
			dx += World.WIDTH;
			transform.translate(World.WIDTH, 0);
		}
		while (dy >= World.HEIGHT / 2) {
			dy -= World.HEIGHT;
			transform.translate(0, -World.HEIGHT);
		}
		while (dy < -World.HEIGHT / 2) {
			dy += World.HEIGHT;
			transform.translate(0, World.HEIGHT);
		}
	}
	
	public boolean isDead() {
		return t >= lifeSpan;
	}
	
	public void draw(Graphics2D g) {
		g.transform(transform);
	}

}
