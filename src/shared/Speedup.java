package shared;

public class Speedup extends Powerup {
	
	private static final long serialVersionUID = 42L;

	public Speedup() {
		super();
		powerupDuration = 30.0;
		type = 5;
	}

	@Override
	public void apply() {
		owner.thrustPower *= 2;
		owner.rotationSpeed *= 2;
	}

	@Override
	public void unapply() {
		owner.thrustPower /= 2;
		owner.rotationSpeed /= 2;
	}

}
