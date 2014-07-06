package shared;


public abstract class HealthyObject extends PhysicalObject{
	private static final long serialVersionUID = 42L;
	public abstract double maxHealth();
	public double health;
	public PhysicalObject lastHit;
	public void damage(double val, PhysicalObject obj) {
		health -= val;
		lastHit = obj;
	}
	public void recover(double val) {
		health += val;
		if (health > maxHealth()) {
			health = maxHealth();
		}
	}
	@Override
	public void copyStatusFrom(PhysicalObject o) {
		super.copyStatusFrom(o);
		if (o instanceof HealthyObject) {
			HealthyObject h = (HealthyObject) o;
			health = h.health;
			lastHit = h.lastHit;
		}
	}
}
