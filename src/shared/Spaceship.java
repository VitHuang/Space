package shared;

import java.io.Serializable;
import java.util.ArrayList;


public class Spaceship extends HealthyObject implements Serializable {
	
	private static final long serialVersionUID = 42L;

	public enum Action { START_ACCELERATING, STOP_ACCELERATING, START_ROTATING_CLOCKWISE, START_ROTATING_ANTICLOCKWISE, STOP_ROTATING, START_FIRING, STOP_FIRING };
	
	public double rotationSpeed = Math.PI*1.5;
	public String name;
	public double thrustPower = 100;
	public boolean accelerating;
	public double lastFired = 0.0;
	public double firingRate = 0.2;
	public boolean firing;
	public double indestructibleTime = 3.0;
	public ArrayList<Powerup> powerups;

	public Spaceship() {
		super();
		powerups = new ArrayList<Powerup>();
		density = 2;
		baseRadius = 16;
		indestructibleTime = 2.0;
	}
	
	@Override
	public double maxHealth() {
		switch (type) {
		case 0:
			return 20.0;
		case 1:
			return 10.0;
		}
		return 10.0;
	}
	
	@Override
	public void tick(double dt) {
		super.tick(dt);
		if (accelerating) {
			xAcceleration = thrustPower*Math.cos(rotation);
			yAcceleration = thrustPower*Math.sin(rotation);
		} else {
			xAcceleration = 0.0;
			yAcceleration = 0.0;
		}
		if (indestructibleTime > 0.0) {
			indestructibleTime -= dt;
		}
		for (Powerup p : powerups) {
			p.update(dt);
		}
		int i = 0;
		while (i < powerups.size()) {
			if (powerups.get(i).powerupDuration <= 0.0) {
				powerups.get(i).unapply();
				powerups.remove(i);
			} else {
				i++;
			}
		}
	}
	
	public void addPowerup(Powerup p) {
		powerups.add(p);
	}
	
	public void removePowerup(Powerup p) {
		powerups.remove(p);
	}
	
	public boolean canFire() {
		if (firing) {
			return System.currentTimeMillis() / 1000.0 >= (lastFired + firingRate);
		} else {
			return false;
		}
	}
	
	public ArrayList<Bullet> fire() {
		lastFired = System.currentTimeMillis() / 1000.0;
		Bullet bullet = new Bullet(id);
		bullet.x = x + (radius() + bullet.radius()) * Math.cos(rotation);
		bullet.y = y + (radius() + bullet.radius()) * Math.sin(rotation);
		bullet.xVelocity = (bullet.x - x) / radius() * 500 + xVelocity;
		bullet.yVelocity = (bullet.y - y) / radius() * 500 + yVelocity;
		switch (type) {
		case 0:
			bullet.damage = 1.0;
			break;
		case 1:
			bullet.damage = 2.0;
			break;
		}
		ArrayList<Bullet> list = new ArrayList<Bullet>();
		list.add(bullet);
		for (Powerup p : powerups) {
			p.upgradeBullets(list);
		}
		return list;
	}
	
	public void startAccelerating() {
		accelerating = true;
	}
	
	public void stopAccelerating() {
		accelerating = false;
	}
	
	public void startFiring() {
		firing = true;
	}
	
	public void stopFiring() {
		firing = false;
	}

	public void startRotatingClockwise() {
		angularVelocity = rotationSpeed;
	}
	
	public void startRotatingAnticlockwise() {
		angularVelocity = -rotationSpeed;
	}

	public void stopRotating() {
		angularVelocity = 0.0;
	}
	
	@Override
	public void damage(double val, PhysicalObject obj) {
		if (isIndestructible()) {
			val = 0.0;
		}
		if (val > 0.0) {
			indestructibleTime = 2.0;
		}
		super.damage(val, obj);
	}
	
	public boolean isIndestructible() {
		return indestructibleTime > 0.0;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void copyStatusFrom(PhysicalObject o) {
		super.copyStatusFrom(o);
		if (o instanceof Spaceship) {
			Spaceship s = (Spaceship) o;
			rotationSpeed = s.rotationSpeed;
			name = s.name;
			thrustPower = s.thrustPower;
			accelerating = s.accelerating;
			lastFired = s.lastFired;
			firingRate = s.firingRate;
			firing = s.firing;
			indestructibleTime = s.indestructibleTime;
			powerups = (ArrayList<Powerup>) s.powerups.clone();
		}
	}
}
