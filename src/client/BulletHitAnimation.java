package client;

import java.awt.Color;

public class BulletHitAnimation extends CollisionAnimation {


	public BulletHitAnimation() {
		super();
		numParticles = 8;
		radius = 16;
	}
	
	public Color getColour(int id) {
		int alpha = (int)(255 - (t * 255));
		if (alpha < 0) {
			alpha = 0;
		}
		return new Color(255, 128 + particleColourMod[id], 0, alpha);
	}
}
