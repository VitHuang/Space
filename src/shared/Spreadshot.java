package shared;

import java.util.ArrayList;

public class Spreadshot extends Powerup {

	private static final long serialVersionUID = 42L;

	public Spreadshot() {
		super();
		powerupDuration = 30.0;
		type = 6;
	}
	
	@Override
	public void apply() {
		// only affects shots
	}

	@Override
	public void unapply() {
		// only affects shots
	}
	
	public void upgradeBullets(ArrayList<Bullet> bs) {
		ArrayList<Bullet> newBullets = new ArrayList<Bullet>();
		for (Bullet b : bs) {
			double dx = b.x - owner.x;
			double dy = b.y - owner.y;
			Bullet b2 = new Bullet(owner.id);
			Bullet b3 = new Bullet(owner.id);
			b2.copyStatusFrom(b);
			b3.copyStatusFrom(b);
			double cos = Math.cos(Math.PI / 8);
			double sin = Math.sin(Math.PI / 8);
			b2.x = owner.x + dx * cos - dy * sin;
			b2.y = owner.y + dx * sin + dy * cos;
			b3.x = owner.x + dx * cos + dy * sin;
			b3.y = owner.y - dx * sin + dy * cos;
			b2.xVelocity = b.xVelocity * cos - b.yVelocity * sin;
			b2.yVelocity = b.xVelocity * sin + b.yVelocity * cos;
			b3.xVelocity = b.xVelocity * cos + b.yVelocity * sin;
			b3.yVelocity = -b.xVelocity * sin + b.yVelocity * cos;
			newBullets.add(b2);
			newBullets.add(b3);
		}
		bs.addAll(newBullets);
	}

}