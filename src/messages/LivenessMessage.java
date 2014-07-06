package messages;

import java.util.ArrayList;

public class LivenessMessage extends Message {
	
	private static final long serialVersionUID = 42L;
	public ArrayList<Integer> liveIds;
	
	public LivenessMessage(ArrayList<Integer> ids) {
		liveIds = ids;
	}
}
