package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;

import shared.Bullet;
import shared.PhysicalObject;
import shared.Spaceship;
import shared.World;

public class SpaceGame implements KeyListener {

	private Spaceship playerShip;
	private GameScreen screen;

	private Client client;
	private JFrame frame;

	private World world;
	
	public static void main(String[] args) {
		try {
			new SpaceGame(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SpaceGame(String username, int shipType, String host, int port) throws UnknownHostException,
	IOException {
		world = new World(null);
		screen = new GameScreen();
		screen.setSize(800, 600);
		screen.setUpdateLock(world.updateLock);
		frame = new JFrame("En-masse-teroids");
		frame.add(screen);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		client = new Client(host, port);
		client.setMessageHandler(new MessageHandler(this));

		client.sendConnectedMessage(username, shipType);
	}

	public void setPlayerShip(int id) {
		playerShip = world.getShip(id);
		screen.centreOn(playerShip);
		Input inp = new Input();
		inp.setListener(this);
		frame.addKeyListener(inp);
	}
	
	public void setLiveIds(ArrayList<Integer> ids) {
		System.out.println("Removing all dead objects");
		for (int i = 0; i < world.maxObjects(); i++) {
			if ((ids.size() <= 0) || (i != ids.get(0))) {
				if (world.getObject(i) != null) {
					removeObject(i);
				}
			} else {
				ids.remove(0);
			}
		}
	}
	
	public void setScore(int s) {
		screen.setScore(s);
	}

	/*public void refreshObject(PhysicalObject object) {
		PhysicalObject oldObject = world.getObject(object.id);
		if (object == null || oldObject == null
				|| object.getClass() != oldObject.getClass()) {
			screen.removeObject(object.id);
			screen.addObject(object);
		}
		world.refreshObject(object);
	}*/

	public void refreshObject(PhysicalObject object) {
		PhysicalObject oldObject = world.getObject(object.id);
		if (oldObject == null) {
			world.addObject(object);
			screen.addObject(object);
		} else if (object.getClass() == oldObject.getClass()) {
			world.refreshObject(object);
		} else {
			//System.out.println("Trying to change object " + oldObject + " to " + object);
		}
	}

	public void removeObject(int id) {
		screen.removeObject(id);
		world.removeObject(id);
	}

	public void bulletHit(Bullet b, PhysicalObject o) {
		if (screen.onScreen(b.x, b.y)) {
			BulletHitAnimation a = new BulletHitAnimation();
			a.initialise();
			a.x = b.x;
			a.y = b.y;
			a.vx = (b.xVelocity + o.xVelocity) / 2;
			a.vy = (b.yVelocity + o.xVelocity) / 2;
			screen.addAnimation(a);
		}
	}

	public void collision(PhysicalObject a, PhysicalObject b) {
		refreshObject(a);
		refreshObject(b);
		if (a instanceof Bullet) {
			bulletHit((Bullet) a, b);
			return;
		} else if (b instanceof Bullet) {
			bulletHit((Bullet) b, a);
			return;
		}
		if (screen.onScreen(a.x, a.y)) {
			CollisionAnimation anim = new CollisionAnimation();
			anim.initialise();
			anim.x = (a.x + b.x) / 2;
			anim.y = (a.y + b.y) / 2;
			anim.vx = (a.xVelocity + b.xVelocity) / 2;
			anim.vy = (a.yVelocity + b.xVelocity) / 2;
			screen.addAnimation(anim);
		}
	}

	public void explosion(PhysicalObject o) {
		ExplosionAnimation a = new ExplosionAnimation();
		a.initialise();
		a.x = o.x;
		a.y = o.y;
		a.vx = o.xVelocity;
		a.vy = o.yVelocity;
		screen.addAnimation(a);
	}

	@Override
	public void upArrowDown() {
		if (playerShip != null) {
			playerShip.startAccelerating();
			client.sendAction(Spaceship.Action.START_ACCELERATING);
		}
	}

	@Override
	public void upArrowUp() {
		if (playerShip != null) {
			playerShip.stopAccelerating();
			client.sendAction(Spaceship.Action.STOP_ACCELERATING);
		}
	}

	@Override
	public void leftArrowDown() {
		if (playerShip != null) {
			playerShip.startRotatingAnticlockwise();
			client.sendAction(Spaceship.Action.START_ROTATING_ANTICLOCKWISE);
		}
	}

	@Override
	public void leftArrowUp() {
		if (playerShip != null) {
			playerShip.stopRotating();
			client.sendAction(Spaceship.Action.STOP_ROTATING);
		}
	}

	@Override
	public void rightArrowDown() {
		if (playerShip != null) {
			playerShip.startRotatingClockwise();
			client.sendAction(Spaceship.Action.START_ROTATING_CLOCKWISE);
		}
	}

	@Override
	public void rightArrowUp() {
		if (playerShip != null) {
			playerShip.stopRotating();
			client.sendAction(Spaceship.Action.STOP_ROTATING);
		}
	}

	@Override
	public void spaceDown() {
		if (playerShip != null) {
			client.sendAction(Spaceship.Action.START_FIRING);
		}
	}

	@Override
	public void spaceUp() {
		if (playerShip != null) {
			client.sendAction(Spaceship.Action.STOP_FIRING);
		}
	}
}
