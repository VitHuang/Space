package messages;

import java.io.Serializable;

import shared.Spaceship.Action;

public class ClientActionMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;
	
	public Action action;

	public ClientActionMessage(Action a) {
		action = a;
	}

}
