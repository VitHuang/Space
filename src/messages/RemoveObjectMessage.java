package messages;

import java.io.Serializable;

public class RemoveObjectMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;
	
	public int id;
	
	public RemoveObjectMessage(int i) {
		id = i;
	}

}