package shared;

public class Invincibility extends Powerup {

	private static final long serialVersionUID = 42L;
	
	public Invincibility() {
		super();
		powerupDuration = 0.0;
		type = 2;
	}

	@Override
	public void apply() {
		owner.indestructibleTime = 10.0;
	}

	@Override
	public void unapply() {
		// instant use
	}

}
