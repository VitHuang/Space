package messages;

import java.io.Serializable;

import shared.Powerup;

public class PowerupMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;
	
	public Powerup powerup;
	
	public PowerupMessage(Powerup p) {
		powerup = p;
	}

}
