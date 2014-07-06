package shared;

public class Asteroid extends HealthyObject {
	private static final long serialVersionUID = 42L;
	public int id;
		//Clusteriungey stuff
	public Asteroid() {
		super();
		density = 4;
		health = 10;
	}
	
	public double maxHealth() {
		return 10.0 * scale;
	}
	
	@Override
	public void copyStatusFrom(PhysicalObject o) {
		super.copyStatusFrom(o);
	}
}
