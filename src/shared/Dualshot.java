package shared;

import java.util.ArrayList;

public class Dualshot extends Powerup {

	private static final long serialVersionUID = 42L;

	public Dualshot() {
		super();
		powerupDuration = 30.0;
		type = 0;
	}
	
	@Override
	public void apply() {
		owner.firingRate /= 1.5;
	}

	@Override
	public void unapply() {
		owner.firingRate *= 1.5;
	}
	
	public void upgradeBullets(ArrayList<Bullet> bs) {
		ArrayList<Bullet> newBullets = new ArrayList<Bullet>();
		for (Bullet b : bs) {
			double dx = b.x - owner.x;
			double dy = b.y - owner.y;
			Bullet b2 = new Bullet(owner.id);
			b2.copyStatusFrom(b);
			double cos = Math.cos(Math.PI / 8);
			double sin = Math.sin(Math.PI / 8);
			b.x = owner.x + dx * cos - dy * sin;
			b.y = owner.y + dx * sin + dy * cos;
			b2.x = owner.x + dx * cos + dy * sin;
			b2.y = owner.y - dx * sin + dy * cos;
			newBullets.add(b2);
		}
		bs.addAll(newBullets);
	}

}
