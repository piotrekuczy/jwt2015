package pl.edu.piotrekuczy;

import com.badlogic.gdx.utils.Array;

public class Quest {

	String questName;

	// CHARACTERS

	private Array<SpineActor> heroes;
	private Array<SpineActor> enemys;
	
	public Quest(String questName) {
		this.questName = questName;
		
		// CHARACTERS
		
		heroes = new Array<SpineActor>();
		enemys = new Array<SpineActor>();
	}

	public String getQuestName() {
		return questName;
	}

	public void setQuestName(String questName) {
		this.questName = questName;
	}

	public Array<SpineActor> getHeroes() {
		return heroes;
	}

	public void setHeroes(Array<SpineActor> heroes) {
		this.heroes = heroes;
	}

	public Array<SpineActor> getEnemys() {
		return enemys;
	}

	public void setEnemys(Array<SpineActor> enemys) {
		this.enemys = enemys;
	}

	
}
