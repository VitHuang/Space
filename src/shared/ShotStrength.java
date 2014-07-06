package shared;

import java.util.ArrayList;

public class ShotStrength extends Powerup {

	private static final long serialVersionUID = 42L;
	
	public ShotStrength() {
		super();
		powerupDuration = 30.0;
		type = 3;
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
		for (Bullet b : bs) {
			b.scale *= 2;
			b.damage *= 2;
			b.type = 1;
		}
	}

}
