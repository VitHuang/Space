package client;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;

import shared.Spaceship;

public class DrawableShip extends DrawableObject {
	
	private static final double BLINK_RATE = 8;
	public int blinkCount;

	public DrawableShip(Spaceship ship) {
		super(ship);
	}

	@Override
	public void draw(Graphics2D g) {
		Image img = null;
		Spaceship ship = (Spaceship) object;
		img = ImageManager.getImage("resources/ship" + ship.type + ".png");
		g.setPaint(Color.WHITE);
		FontMetrics fm = g.getFontMetrics();
		int w = fm.stringWidth(ship.name);
		g.drawString(ship.name, (int)Math.round(ship.x - w / 2), (int)Math.round(ship.y - 32));
		g.setPaint(new Color(255, 0, 0, 64));
		g.fillRect((int)(ship.x - 17), (int)(ship.y - 25), 34, 6);
		g.setPaint(new Color(0, 0, 0, 128));
		double p = ship.health / ship.maxHealth();
		g.fillRect((int)(ship.x - 16), (int)(ship.y - 24), (int)(32 * (1.0 - p)), 4);
		g.setPaint(new Color(255, 0, 0, 64));
		g.fillRect((int)(ship.x - 16) + (int)(32 * (1.0 - p)), (int)(ship.y - 24), (int)Math.ceil(32 * p), 4);
		if (!ship.isIndestructible() || blinkCount < BLINK_RATE / 2) {
			g.drawImage(img, transform, null);
		}
		blinkCount++;
		blinkCount %= BLINK_RATE;
	}
	
}
