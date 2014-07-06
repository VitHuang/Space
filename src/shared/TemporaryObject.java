package shared;

public abstract class TemporaryObject extends PhysicalObject {

	private static final long serialVersionUID = 42L;
	public double lifeSpan;
	
	public TemporaryObject() {
		lifeSpan = 10.0;
	}
	
	public boolean shouldDispose() {
		return lifeSpan <= 0.0;
	}
	
	public void tick(double dt) {
		super.tick(dt);
		lifeSpan -= dt;
	}

}
