package messages;

import java.io.Serializable;

import shared.PhysicalObject;

public class ExplosionMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;

	public PhysicalObject object;
	
	public ExplosionMessage(PhysicalObject o) {
		object = o;
	}
}