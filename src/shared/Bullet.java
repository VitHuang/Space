package shared;

public class Bullet extends TemporaryObject {

	private static final long serialVersionUID = 42L;
	public double damage;
	public int owner;
	
	public Bullet(int o) {
		owner = o;
		damage = 1.0;
		lifeSpan = 1.0;
		density = 0.0000001;
		baseRadius = 4.0;
	}

	
	@Override
	public void copyStatusFrom(PhysicalObject o) {
		super.copyStatusFrom(o);
		if (o instanceof Bullet) {
			Bullet b = (Bullet) o;
			damage = b.damage;
		}
	}
	
	
	
}
