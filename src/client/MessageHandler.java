package client;

import messages.*;
import shared.PhysicalObject;

public class MessageHandler {
	
	private SpaceGame game;
	
	public MessageHandler(SpaceGame g) {
		game = g;
	}
	
	public void handleMessage(Message msg) {
		//System.out.println("Received message " + msg);
		if (msg instanceof ObjectStatusMessage) {
			PhysicalObject obj = ((ObjectStatusMessage) msg).object;
			//System.out.println("Object position: (" + obj.x + ", " + obj.y + ")");
			game.refreshObject(obj);
		} else if (msg instanceof SetPlayerShipMessage) {
			game.setPlayerShip(((SetPlayerShipMessage) msg).id);
		} else if (msg instanceof RemoveObjectMessage) {
			game.removeObject(((RemoveObjectMessage) msg).id);
		} else if (msg instanceof CollisionMessage) {
			game.collision(((CollisionMessage) msg).object1, ((CollisionMessage) msg).object2);
		} else if (msg instanceof ExplosionMessage) {
			game.explosion(((ExplosionMessage) msg).object);
		} else if (msg instanceof PowerupMessage) {
			// not implemented yet
		} else if (msg instanceof ScoreMessage) {
			game.setScore(((ScoreMessage) msg).score);
		} else if (msg instanceof LivenessMessage) {
			game.setLiveIds(((LivenessMessage) msg).liveIds);
		}
	}

}
