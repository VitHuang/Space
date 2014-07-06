package messages;

import java.io.Serializable;

public class ClientConnectedMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;
	
	public String name;
	public int shipType;
	
	public ClientConnectedMessage(String n, int t) {
		name = n;
		shipType = t;
	}

}
