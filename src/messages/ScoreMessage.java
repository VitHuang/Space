package messages;

import java.io.Serializable;

public class ScoreMessage extends Message implements Serializable {

	private static final long serialVersionUID = 42L;
	
	public int userId;
	public int score;
	
	public ScoreMessage(int s) {
		score = s;
	}

}
