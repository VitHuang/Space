package messages;

import java.io.Serializable;

import shared.PhysicalObject;

public class ObjectStatusMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;
	
	public PhysicalObject object;
	
	public ObjectStatusMessage(PhysicalObject o) {
		object = o;
	}

}
