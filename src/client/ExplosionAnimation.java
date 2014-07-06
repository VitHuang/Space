package client;

import java.awt.Color;

public class ExplosionAnimation extends CollisionAnimation {


	public ExplosionAnimation() {
		super();
		numParticles = 64;
		radius = 64;
		particleSize = 4;
		lifeSpan = 2.0;
	}
	
	public Color getColour(int id) {
		int alpha = (int)(255 - (t * 127));
		if (alpha < 0) {
			alpha = 0;
		}
		return new Color(255, Math.max(particleColourMod[id], 255 - alpha), ((255 - alpha) * 3) / 4, alpha);
	}
}
