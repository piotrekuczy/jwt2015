package pl.edu.piotrekuczy;

import com.badlogic.gdx.Game;

public class Jamwteatrze extends Game {

	Introscreen introscreen;
	Gamescreen gamescreen;

	@Override
	public void create() {

		Assets.load();
		Assets.manager.finishLoading();

		if (Assets.manager.update()) {

			introscreen = new Introscreen(this);
			gamescreen = new Gamescreen(this);

			setScreen(gamescreen);
//			setScreen(introscreen);
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

}
