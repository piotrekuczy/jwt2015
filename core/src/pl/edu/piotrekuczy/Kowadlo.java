package pl.edu.piotrekuczy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Kowadlo extends Actor {

	Gamescreen gamescreen;
	
	boolean dropped = false;

	public Kowadlo(Gamescreen gamescreen)  {
		
		this.gamescreen = gamescreen;
		
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}
	@Override
	public void draw(Batch batch, float alpha) {

		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * alpha);
		batch.draw(gamescreen.kowadloTex, getX(), getY());
	}

	public boolean isDropped() {
		return dropped;
	}

	public void setDropped(boolean dropped) {
		this.dropped = dropped;
	}
	
	
}
