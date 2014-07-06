package shared;

public class Sneakiness extends Powerup {

	private static final long serialVersionUID = 1L;

	public Sneakiness() {
		super();
		powerupDuration = 30.0;
		type = 4;
	}
	
	@Override
	public void apply() {
		owner.scale *= 0.75;
		owner.thrustPower *= 1.5;
		owner.firingRate *= 0.75;
	}

	@Override
	public void unapply() {
		owner.scale /= 0.75;
		owner.thrustPower /= 1.5;
		owner.firingRate /= 0.75;
	}

}
