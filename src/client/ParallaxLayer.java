package client;

import java.awt.Color;
import java.awt.Graphics2D;

public class ParallaxLayer {
	
	private static final double DENSITY = 0.00005;
	
	public class Star {
		public Color colour;
		public int x;
		public int y;
	}
	
	Star[] stars;
	
	public static Color getColour(double intensity) {
		if (intensity >= 1.0) {
			return new Color(255, 255, 255, 255);
		}
		else if (intensity >= 0.5) {
			return new Color((int)(255 * intensity), (int)(255 * intensity), 255, 255);
		} else {
			return new Color(0, 0, (int)(255 * intensity), 255);
		}
	}
	
	public ParallaxLayer(int width, int height, double maxIntensity) {
		double area = width * height;
		int numStars = (int)(area * DENSITY);
		stars = new Star[numStars];
		for (int i = 0; i < numStars; i++) {
			int x = (int)(Math.random() * width);
			int y = (int)(Math.random() * height);
			stars[i] = new Star();
			stars[i].colour = getColour(Math.random() * maxIntensity);
			stars[i].x = x;
			stars[i].y = y;
		}
	}

	public void draw(Graphics2D g, int x, int y) {
		for (int i = 0; i < stars.length; i++) {
			g.setPaint(stars[i].colour);
			g.fillRect(stars[i].x + x, stars[i].y + y, 1, 1);
		}
	}
}
