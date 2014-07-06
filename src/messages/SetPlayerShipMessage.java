package messages;

import java.io.Serializable;

public class SetPlayerShipMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;

	public int id;
	
	public SetPlayerShipMessage(int i) {
		id = i;
	}

}
