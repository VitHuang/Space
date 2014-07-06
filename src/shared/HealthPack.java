package shared;

public class HealthPack extends Powerup {

	private static final long serialVersionUID = 42L;

	public HealthPack() {
		super();
		powerupDuration = 0.0; // instant one-time effect
		type = 1;
	}
	
	@Override
	public void apply() {
		owner.recover(owner.maxHealth() / 3.0);

	}

	@Override
	public void unapply() {
		//do nothing

	}

}
