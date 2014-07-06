package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class CollisionAnimation extends Animation {

	protected int numParticles;
	protected int radius;
	protected int particleSize;
	
	protected double particleX[];
	protected double particleY[];
	protected int particleColourMod[];
	
	public CollisionAnimation() {
		super();
		numParticles = 32;
		radius = 32;
		particleSize = 2;
		lifeSpan = 1.0;
	}
	
	public void initialise() {
		particleX = new double[numParticles];
		particleY = new double[numParticles];
		particleColourMod = new int[numParticles];
		for (int i = 0; i < numParticles; i++) {
			particleX[i] = Math.random() * radius * 2 - radius;
			particleY[i] = Math.random() * radius * 2 - radius;
			particleColourMod[i] = (int)(Math.random() * 128);
		}
	}
	
	public Color getColour(int id) {
		int alpha = (int)(255 - (t * 255));
		if (alpha < 0) {
			alpha = 0;
		}
		return new Color(128 + particleColourMod[id], 255, 0, alpha);
	}
	
	@Override
	public void draw(Graphics2D g) {
		AffineTransform original = g.getTransform();
		g.transform(transform);
		for (int i = 0; i < numParticles; ++i) {
			g.setPaint(getColour(i));
			g.fillRect((int)(particleX[i] * t), (int)(particleY[i] * t), particleSize, particleSize);
		}
		g.setTransform(original);
	}

}
