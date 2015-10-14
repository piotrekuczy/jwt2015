package pl.edu.piotrekuczy;

import com.badlogic.gdx.utils.Array;

public class Swiat {

	// QUESTY

	private Array<Quest> questy;

	public Swiat() {
		
		questy = new Array<Quest>();
	}

	public Array<Quest> getQuesty() {
		return questy;
	}

	public void setQuesty(Array<Quest> questy) {
		this.questy = questy;
	}

}
