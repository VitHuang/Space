package client;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import shared.*;

public class DrawableObject extends Drawable {

	AffineTransform transform;
	PhysicalObject object;

	public DrawableObject(PhysicalObject obj) {
		transform = new AffineTransform();
		object = obj;
	}

	public PhysicalObject getObject() {
		return object;
	}

	@Override
	public void draw(Graphics2D g) {
		Image img = null;
		if (object instanceof Spaceship) {
			img = ImageManager.getImage("resources/ship" + object.type + ".png");
		} else if (object instanceof Asteroid) {
			img = ImageManager.getImage("resources/asteroid" + object.type + ".png");
		} else if (object instanceof Bullet) {
			img = ImageManager.getImage("resources/bullet" + object.type + ".png");
		} else if (object instanceof Powerup) {
			img = ImageManager.getImage("resources/powerup" + object.type + ".png");
		} else {
			img = ImageManager.getImage("default.png");
		}
		g.drawImage(img, transform, null);
	}

	@Override
	public void update(double cx, double cy) {
		transform.setToIdentity();
		transform.translate(object.x, object.y);
		double dx = object.x - cx;
		double dy = object.y - cy;
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
		transform.rotate(object.rotation);
		transform.translate(-object.radius(), -object.radius());
		transform.scale(object.scale, object.scale);
	}

}
