package shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;


import server.Server;

public class World implements ActionListener {

	private ArrayList<PhysicalObject> objects;
	private Timer timer;
	private Server server;
	
	private long lastUpdateTime;

	public static final double WIDTH = 2000.0;
	public static final double HEIGHT = 2000.0;
	public static final double DRAG_COEFF = 0.0;
	
	public static final int MAX_ASTEROIDS = 100;
	
	private int numAsteroids;

	public Object updateLock;

	public World(Server s) {
		updateLock = new Object();
		objects = new ArrayList<PhysicalObject>();
		objects.add(null);
		timer = new Timer(33, this);
		server = s;
		lastUpdateTime = System.currentTimeMillis();
		numAsteroids = 0;
		timer.start();
	}
	
	public ArrayList<Integer> getLiveIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < maxObjects(); i++) {
			if (getObject(i) != null) {
				ids.add(i);
			}
		}
		return ids;
	}

	public void dynamicallyResizeObjectList(int size) {
		while (objects.size() <= size) {
			int currentSize = objects.size();
			for (int i = 0; i < currentSize; i++) {
				objects.add(null);
			}
		}
	}

	public int firstFree() {
		int ret = 0;
		while (ret < objects.size() && objects.get(ret) != null) {
			ret++;
		}
		if (ret >= objects.size()) {
			dynamicallyResizeObjectList(ret);
		}
		return ret;
	}

	public int addNewPlayer(String name, int shipType) {
		Spaceship ship = new Spaceship();
		ship.x = Math.random() * WIDTH;
		ship.y = Math.random() * HEIGHT;
		ship.rotation = (Math.random() * 2 * Math.PI) - Math.PI;
		ship.name = name;
		ship.type = shipType;
		ship.health = ship.maxHealth();
		return addObject(ship);
	}

	public void addAsteroids(int quantity) {
		for (int i = 0; i < quantity; i++) {
			Asteroid a = new Asteroid();
			a.x = Math.random() * WIDTH;
			a.y = Math.random() * HEIGHT;
			a.xVelocity = (Math.random() * 64) - 32;
			a.yVelocity = (Math.random() * 64) - 32;
			a.rotation = (Math.random() * 2 * Math.PI) - Math.PI;
			a.angularVelocity = (Math.random() / 2 * Math.PI) - Math.PI / 4;
			a.scale = Math.random() + 0.5;
			a.health = a.maxHealth();
			addObject(a);
		}
	}

	public int addObject(PhysicalObject object) {
		synchronized (updateLock) {
			int index;
			if (object.id < 0) {
				index = firstFree();
				object.id = index;
			} else {
				index = object.id;
			}
			boolean colliding = true;
			while (colliding) {
				colliding = false;
				for (int i = 0; i < maxObjects(); i++) {
					PhysicalObject o = getObject(i);
					if (o != null) {
						if ((i != object.id) && object.collidingWith(o)) {
							colliding = true;
							object.x += Math.random() * 2 * object.radius() - object.radius();
							object.y += Math.random() * 2 * object.radius() - object.radius();
						}
					}
				}
			}
			objects.set(index, object);
			if (object instanceof Asteroid) {
				numAsteroids++;
			}
			if (server != null) {
				server.sendObject(object, null);
			}
			return index;
		}
	}

	public int maxObjects() {
		return objects.size();
	}

	public void removeObject(int id) {
		dynamicallyResizeObjectList(id);
		synchronized (updateLock) {
			PhysicalObject o = objects.get(id);
			if (o == null) {
				return;
			}
			if (id >= 0) {
				objects.set(id, null);
			}
			if (o instanceof Asteroid) {
				numAsteroids--;
			}
			if (server != null) {
				server.sendRemove(id);
			}
		}
	}

	public void removeObject(PhysicalObject object) {
		removeObject(objects.indexOf(object));
	}

	public PhysicalObject getObject(int id) {
		dynamicallyResizeObjectList(id);
		return objects.get(id);
	}

	public void refreshObject(PhysicalObject object) {
		synchronized (updateLock) {
			int id = object.id;
			dynamicallyResizeObjectList(id);
			PhysicalObject oldObject = getObject(object.id);
			if (oldObject == null) {
				addObject(object);
			} else if (oldObject.getClass() == object.getClass()) {
				oldObject.copyStatusFrom(object);
			}
		}
		//System.out.println("Object " + id + " position: (" + object.x + ", " + object.y + ")");
	}

	public Spaceship getShip(int id) {
		PhysicalObject object = objects.get(id);
		if (object instanceof Spaceship) {
			return (Spaceship) object;
		} else {
			return null;
		}
	}

	public Asteroid getAsteroid(int id) {
		PhysicalObject object = objects.get(id);
		if (object instanceof Asteroid) {
			return (Asteroid) object;
		} else {
			return null;
		}
	}

	public void update(double dt) {
		synchronized (updateLock) {
			for (int i = 0; i < maxObjects(); i++) {
				PhysicalObject object = objects.get(i);
				if (object != null) {
					for (int j = i + 1; j < maxObjects(); j++) {
						PhysicalObject object2 = objects.get(j);
						if (object2 != null && object.collidingWith(object2)) {
							boolean mustHandleCollision = true;
							if (object instanceof Bullet && object2 instanceof Bullet) {
								mustHandleCollision = false;
							}
							if (object instanceof Bullet) {
								Bullet b = (Bullet)object;
								if (object2 == getObject(b.owner)) {
									mustHandleCollision = false;
								}
							}
							if (object2 instanceof Bullet) {
								Bullet b = (Bullet)object2;
								if (object == getObject(b.owner)) {
									mustHandleCollision = false;
								}
							}
							if (server != null) {
								if (object instanceof Spaceship && object2 instanceof Powerup) {
									((Powerup) object2).pickUp((Spaceship) object);
									removeObject(object2);
									server.sendPowerup((Powerup) object2);
									server.addScore(500, object.id);
									mustHandleCollision = false;
								}
								if (object instanceof Powerup && object2 instanceof Spaceship) {
									((Powerup) object).pickUp((Spaceship) object2);
									removeObject(object);
									server.sendPowerup((Powerup) object);
									server.addScore(500, object2.id);
									mustHandleCollision = false;
								}
							}
							if (mustHandleCollision) {
								if (server != null) {
									if (object instanceof Bullet) {
										Bullet b = (Bullet)object;
										b.lifeSpan = 0.0;
										if (object2 instanceof HealthyObject) {
											((HealthyObject) object2).damage(b.damage, b);
										}
									}
									if (object2 instanceof Bullet) {
										Bullet b = (Bullet)object2;
										b.lifeSpan = 0.0;
										if (object instanceof HealthyObject) {
											((HealthyObject) object).damage(b.damage, b);
										}
									}
									if (!(object instanceof Asteroid && object2 instanceof Asteroid)) { // asteroids shouldn't damage each other
										if (object instanceof HealthyObject && object2 instanceof HealthyObject) {
											((HealthyObject) object).damage(object.relativeMomentum(object2) / 40000.0, object2);
											((HealthyObject) object2).damage(object2.relativeMomentum(object) / 40000.0, object);
										}
									}
								}
								object.handleCollision(object2, dt);
								if (server != null) {
									server.sendCollision(object, object2);
								}
							}
						}
					}
					object.tick(dt);
				}
			}
		}
		if (server != null) {
			for (int i = 0; i < maxObjects(); i++) {
				PhysicalObject object = objects.get(i);
				if (object instanceof HealthyObject) {
					HealthyObject h = (HealthyObject) object;
					if (h.health <= 0.0) {
						server.sendExplosion(h);
						if (h instanceof Spaceship) {
							server.playerDestroyed((Spaceship) h, h.lastHit);
							removeObject(h);
						} else {
							if (h.lastHit instanceof Spaceship) {
								server.addScore(destroyedScore(h) * 2, h.lastHit.id);
							} else if (h.lastHit instanceof Bullet) {
								server.addScore(destroyedScore(h), ((Bullet) h.lastHit).owner);
							}
							removeObject(h);
						}
						if (h instanceof Asteroid) {
							Asteroid a = (Asteroid) h;
							if (Math.random() < 0.3) {
								spawnPowerup(a.x, a.y, a.xVelocity, a.yVelocity);
							}
							if (a.scale >= 1.0) {
								double massRemaining = a.mass() * 0.75;
								int numFragments = (int) (Math.random() * 2 + 2);
								double randAngle = Math.random() * Math.PI * 2;
								double momentumMultiplier = Math.random() * 10000 + 5000;
								for (int j = 0; j < (numFragments - 1); j++) {
									double m = massRemaining * Math.random();
									double s = Math.sqrt(m / a.density) / a.baseRadius;
									if (s <= 0.5) {
										s = 0.5;
										m = (a.baseRadius * s) * (a.baseRadius * s) * a.density;
									}
									double xComp = Math.cos(j * Math.PI * 2 / numFragments + randAngle);
									double yComp = Math.sin(j * Math.PI * 2/ numFragments + randAngle);
									double xMom = xComp * momentumMultiplier;
									double yMom = yComp * momentumMultiplier;
									massRemaining -= m;
									Asteroid na = new Asteroid();
									na.xVelocity = a.xVelocity + xMom / m;
									na.yVelocity = a.yVelocity + yMom / m;
									na.scale = s;
									na.x = a.x + xComp * na.radius() * 2;
									na.y = a.y + yComp * na.radius() * 2;
									na.health = na.maxHealth();
									na.rotation = (Math.random() * 2 * Math.PI) - Math.PI;
									na.angularVelocity = (Math.random() / 2 * Math.PI) - Math.PI / 4;
									addObject(na);
								}
								double m = massRemaining;
								double s = Math.sqrt(m / a.density) / a.baseRadius;
								if (s <= 0.5) {
									s = 0.5;
									m = (a.baseRadius * s) * (a.baseRadius * s) * a.density;
								}
								double xComp = Math.cos(-Math.PI * 2 / numFragments + randAngle);
								double yComp = Math.sin(-Math.PI * 2 / numFragments + randAngle);
								double xMom = xComp * momentumMultiplier;
								double yMom = yComp * momentumMultiplier;
								Asteroid na = new Asteroid();
								na.xVelocity = a.xVelocity + xMom / m;
								na.yVelocity = a.yVelocity + yMom / m;
								na.scale = s;
								na.x = a.x + xComp * na.radius() * 2;
								na.y = a.y + yComp * na.radius() * 2;
								na.health = na.maxHealth();
								na.rotation = (Math.random() * 2 * Math.PI) - Math.PI;
								na.angularVelocity = (Math.random() / 2 * Math.PI) - Math.PI / 4;
								addObject(na);
							}
						}
					}
				}
				if (object instanceof Spaceship) {
					Spaceship s = (Spaceship) object;
					if (s.canFire()) {
						ArrayList<Bullet> bullets = s.fire();
						for (Bullet b : bullets) {
							addObject(b);
						}
					}
				} if (object instanceof TemporaryObject) {
					TemporaryObject temp = (TemporaryObject) object;
					if (temp.shouldDispose()) {
						removeObject(temp);
					}
				}
			}
		}
	}
	
	public int destroyedScore(HealthyObject o) {
		if (o instanceof Asteroid) {
			return (int)(((Asteroid) o).scale * 1000);
		} else if (o instanceof Spaceship) {
			return 500; // rest of points are assigned by server 
		}
		return 0;
	}
	
	public void spawnPowerup(double x, double y, double xv, double yv) {
		Powerup p;
		switch ((int)(Math.random() * 7)) {
		case 0:
			p = new Dualshot();
			break;
		case 1:
			p = new HealthPack();
			break;
		case 2:
			p = new Invincibility();
			break;
		case 3:
			p = new ShotStrength();
			break;
		case 4:
			p = new Sneakiness();
			break;
		case 5:
			p = new Speedup();
			break;
		default:
			p = new Spreadshot();
		}
		p.x = x;
		p.y = y;
		p.xVelocity = xv;
		p.yVelocity = yv;
		addObject(p);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		long time = System.currentTimeMillis();
		update((time - lastUpdateTime) / 1000.0);
		lastUpdateTime = time;
		if (server != null) {
			if (Math.random() < 0.01) {
				if (numAsteroids < MAX_ASTEROIDS) {
					addAsteroids(1);
				}
			}
		}
	}

}
