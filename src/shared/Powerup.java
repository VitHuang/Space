package shared;

import java.util.ArrayList;

public abstract class Powerup extends TemporaryObject {
	
	private static final long serialVersionUID = 42L;
	Spaceship owner;
	public double powerupDuration;
	
	public Powerup() {
		super();
		baseRadius = 8;
		scale = 2.0;
		lifeSpan = 30.0;
		powerupDuration = 10.0;
	}
	public void pickUp(Spaceship s) {
		owner = s;
		owner.addPowerup(this);
		apply();
	}
	public void update(double dt) {
		powerupDuration -= dt;
	}
	public void upgradeBullets(ArrayList<Bullet> bs) {
	}
	public abstract void apply();
	public abstract void unapply();
	@Override
	public void copyStatusFrom(PhysicalObject o) {
		super.copyStatusFrom(o);
		if (o instanceof Powerup) {
			Powerup p = (Powerup) o;
			owner = p.owner;
			powerupDuration = p.powerupDuration;
		}
	}
}
