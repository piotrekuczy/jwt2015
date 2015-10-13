package pl.edu.piotrekuczy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Dice extends Actor {

	Gamescreen gamescreeen;

	TextureRegion frame;

	boolean stopklatka = false;
	int numerKlatki;

	boolean heroDice;

	protected boolean started;

	public Dice(Gamescreen gamescreen, boolean heroDice) {

		this.gamescreeen = gamescreen;
		// czy ta kosc nalezy do gracza czy nie
		this.heroDice = heroDice;

		numerKlatki = MathUtils.random(1, 6);
		frame = Assets.manager.get(Assets.diceStandardAtlas, TextureAtlas.class).findRegion("dice0" + numerKlatki);

		this.setBounds(getX(), getY(), frame.getRegionWidth(), frame.getRegionHeight());

		if (heroDice) {
			this.addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					((Dice) event.getTarget()).started = true;
					// System.out.println("taczed");
					if (!stopklatka) {
						heroDiceLogic();
						stopklatka = true;
					}
					return true;
				}
			});
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (!stopklatka) {
			numerKlatki = MathUtils.random(1, 6);
		}
		frame = Assets.manager.get(Assets.diceStandardAtlas, TextureAtlas.class).findRegion("dice0" + numerKlatki);

	}

	@Override
	public void draw(Batch batch, float alpha) {

		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * alpha);
		batch.draw(frame, getX(), getY());
	}

	public void heroDiceLogic() {
		if (numerKlatki == 1) {
			// rolled
			// gamescreeen.setDelayTimer(0);
			gamescreeen.setRolled(true);
			gamescreeen.setPulaGracza(0);
		} else {
			// niezrollowane dodaj do puli gracza
			gamescreeen.setDelayTimer(0);
			gamescreeen.setRolled(false);
			gamescreeen.setPulaGracza(gamescreeen.getPulaGracza() + numerKlatki);
			gamescreeen.setNumberHeroDieces(gamescreeen.getNumberHeroDieces() - 1);
		}
	}

	public void enemyDiceLogic() {
		if (numerKlatki == 1) {
			// rolled
			gamescreeen.setRolled(true);
			gamescreeen.setPulaPrzeciwnika(0);
			// przeciwnik przerollowal wiec swaptury!
		} else {
			// niezrollowane dodaj do puli przeciwnika
			// gamescreeen.setDelayTimer(0);
			// gamescreeen.setEnemyTurnTimer(0);
//				gamescreeen.setRolled(false);
//				gamescreeen.setPulaPrzeciwnika(gamescreeen.getPulaPrzeciwnika() + numerKlatki);
//				gamescreeen.setNumberEnemyDieces(gamescreeen.getNumberEnemyDieces() - 1);
		}
	}

	public boolean isStopklatka() {
		return stopklatka;
	}

	public void setStopklatka(boolean stopklatka) {
		this.stopklatka = stopklatka;
	}

}
