package client;

import java.awt.Graphics2D;

public class ParallaxBackground {

	private ParallaxLayer[] layers;
	double width;
	double height;

	public ParallaxBackground(double w, double h) {
		width = w;
		height = h;
		layers = new ParallaxLayer[5];
		for (int i = 0; i < layers.length; i++) {
			int l = i + 2;
			layers[i] = new ParallaxLayer((int) (width / l),
					(int) (height / l), (8 - i) * 0.25);
		}
	}

	public void draw(Graphics2D g, double centreX, double centreY, double w, double h) {
		for (int i = 0; i < layers.length; i++) {
			int l = i + 2;
			int lWidth = (int)(width / l);
			int lHeight = (int)(height / l);
			int xOffset = -(int)(centreX / l);
			int yOffset = -(int)(centreY / l);
			while (xOffset >= 0) {
				xOffset -= lWidth;
			}
			while (yOffset >= 0) {
				yOffset -= lHeight;
			}
			int baseXOffset = xOffset;
			while (yOffset < h) {
				xOffset = baseXOffset;
				while (xOffset < w) {
					layers[i].draw(g, xOffset, yOffset);
					xOffset += lWidth;
				}
				yOffset += lHeight;
			}
		}
	}
}
