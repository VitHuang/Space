package shared;

import java.io.Serializable;

public class PhysicalObject implements Serializable {
	private static final long serialVersionUID = 42L;
	
	public double x;
	public double y;
	public double xVelocity = 0;
	public double yVelocity = 0;
	public double xAcceleration = 0;
	public double yAcceleration = 0;
	public double rotation = 0;
	public double angularVelocity = 0;

	public double density = 1;
	public double baseRadius = 16;
	public double scale = 1.0;
	public int type = 0;
	public int id = -1;

	public double radius() {
		return baseRadius * scale;
	}
	
	public double mass() {
		return radius() * radius() * density;
	}

	public double momentum() {
		return speed() * mass();
	}
	
	public double horizontalMomentum() {
		return xVelocity * mass();
	}
	
	public double verticalMomentum() {
		return yVelocity * mass();
	}
	
	public double relativeMomentum(PhysicalObject other) {
		double relXVel = xVelocity - other.xVelocity;
		double relYVel = yVelocity - other.yVelocity;
		return Math.sqrt(relXVel * relXVel + relYVel * relYVel) * other.mass();
	}
	
	public double speed() {
		return Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
	}

	public void tick(double dt) {
		xVelocity += xAcceleration * dt;
		yVelocity += yAcceleration * dt;
		x += xVelocity * dt;
		y += yVelocity * dt;
		xVelocity -= xVelocity * World.DRAG_COEFF * dt;
		yVelocity -= yVelocity * World.DRAG_COEFF * dt;
		rotation += angularVelocity * dt;
		while (rotation > Math.PI) {
			rotation -= 2 * Math.PI;
		}
		while (rotation < -Math.PI) {
			rotation += 2 * Math.PI;
		}

		while (x < 0) {
			x += World.WIDTH;
		}
		while (y < 0) {
			y += World.HEIGHT;
		}
		while (x >= World.WIDTH) {
			x -= World.WIDTH;
		}
		while (y >= World.HEIGHT) {
			y -= World.HEIGHT;
		}

	}

	public boolean collidingWith(PhysicalObject o) {
		double dx = x - o.x;
		double dy = y - o.y;
		while (dx >= World.WIDTH / 2) {
			dx -= World.WIDTH;
		}
		while (dx < -World.WIDTH / 2) {
			dx += World.WIDTH;
		}
		while (dy >= World.HEIGHT / 2) {
			dy -= World.HEIGHT;
		}
		while (dy < -World.HEIGHT / 2) {
			dy += World.HEIGHT;
		}
		if ((dx * dx + dy * dy) <= (radius() + o.radius()) * (radius() + o.radius())) {
			return true;
		} else {
			return false;
		}
	}
	
	public void handleCollision(PhysicalObject o, double dt) {
		double dx = x - o.x;
		double dy = y - o.y;
		while (dx >= World.WIDTH / 2) {
			dx -= World.WIDTH;
		}
		while (dx < -World.WIDTH / 2) {
			dx += World.WIDTH;
		}
		while (dy >= World.HEIGHT / 2) {
			dy -= World.HEIGHT;
		}
		while (dy < -World.HEIGHT / 2) {
			dy += World.HEIGHT;
		}
		double distance = Math.sqrt(dx * dx + dy * dy);
		double ndx = dx / distance; // normalised
		double ndy = dy / distance;
		double nc1 = xVelocity * ndx + yVelocity * ndy; // component of velocity along normal vector
		double nc2 = o.xVelocity * ndx + o.yVelocity * ndy;
		double p = (2.0 * (nc1 - nc2)) / (mass() + o.mass()); // momentum / (mass * o.mass)
		x -= xVelocity * dt * 1.1;
		y -= yVelocity * dt * 1.1;
		o.x -= o.xVelocity * dt * 1.1;
		o.y -= o.yVelocity * dt * 1.1;
		double speedMod1 = p * o.mass();
		double speedMod2 = p * mass();
		xVelocity = xVelocity - speedMod1 * ndx;
		yVelocity = yVelocity - speedMod1 * ndy;
		o.xVelocity = o.xVelocity + speedMod2 * ndx;
		o.yVelocity = o.yVelocity + speedMod2 * ndy;
		/*double overlap = Math.sqrt(radius() * radius() + o.radius() * o.radius()) - distance;
		double proportion = radius() / (radius() + o.radius());
		x -= ndx * overlap * proportion;
		y -= ndy * overlap * proportion;
		o.x += ndx * overlap * (1.0 - proportion);
		o.y += ndx * overlap * (1.0 - proportion);*/
	}

	public void copyStatusFrom(PhysicalObject o) {
		x = o.x;
		y = o.y;
		xVelocity = o.xVelocity;
		yVelocity = o.yVelocity;
		xAcceleration = o.xAcceleration;
		yAcceleration = o.yAcceleration;
		rotation = o.rotation;
		angularVelocity = o.angularVelocity;
		density = o.density;
		baseRadius = o.baseRadius;
		scale = o.scale;
		type = o.type;
		id = o.id;
	}

}
