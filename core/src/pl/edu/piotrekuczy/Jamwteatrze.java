package pl.edu.piotrekuczy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Jamwteatrze extends Game {

	Introscreen introscreen;
	Gamescreen gamescreen;

	// audio
	Sound intromusic, bee, fight, alien;
	long idIntro, idBee, idFight, idAlien;

	public static Player player;

	public static int initialHp = 35;

	@Override
	public void create() {

		// sound
		intromusic = Gdx.audio.newSound(Gdx.files.internal("audio/intro.mp3"));
		bee = Gdx.audio.newSound(Gdx.files.internal("audio/bee.wav"));
		fight = Gdx.audio.newSound(Gdx.files.internal("audio/fight.wav"));
		alien = Gdx.audio.newSound(Gdx.files.internal("audio/alien.wav"));
		idIntro = intromusic.loop();
		
		player = new Player(initialHp);

		Assets.load();
		Assets.manager.finishLoading();

		if (Assets.manager.update()) {
			introscreen = new Introscreen(this);
			gamescreen = new Gamescreen(this);
			// setScreen(gamescreen);
			setScreen(introscreen);
		}
	}

	public Introscreen getIntroscreen() {
		return introscreen;
	}

	public void setIntroscreen(Introscreen introscreen) {
		this.introscreen = introscreen;
	}

	public Gamescreen getGamescreen() {
		return gamescreen;
	}

	public void setGamescreen(Gamescreen gamescreen) {
		this.gamescreen = gamescreen;
	}

	public Sound getIntromusic() {
		return intromusic;
	}

	public void setIntromusic(Sound intromusic) {
		this.intromusic = intromusic;
	}

	public Sound getBee() {
		return bee;
	}

	public void setBee(Sound bee) {
		this.bee = bee;
	}

	public Sound getFight() {
		return fight;
	}

	public void setFight(Sound fight) {
		this.fight = fight;
	}

	public long getIdIntro() {
		return idIntro;
	}

	public void setIdIntro(long idIntro) {
		this.idIntro = idIntro;
	}

	public long getIdBee() {
		return idBee;
	}

	public void setIdBee(long idBee) {
		this.idBee = idBee;
	}

	public long getIdFight() {
		return idFight;
	}

	public void setIdFight(long idFight) {
		this.idFight = idFight;
	}

	public Sound getAlien() {
		return alien;
	}

	public void setAlien(Sound alien) {
		this.alien = alien;
	}

	public long getIdAlien() {
		return idAlien;
	}

	public void setIdAlien(long idAlien) {
		this.idAlien = idAlien;
	}

}
