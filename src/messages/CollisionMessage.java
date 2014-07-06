package messages;

import java.io.Serializable;

import shared.PhysicalObject;

public class CollisionMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;

	public PhysicalObject object1;
	public PhysicalObject object2;
	
	public CollisionMessage(PhysicalObject a, PhysicalObject b) {
		object1 = a;
		object2 = b;
		
	}
}