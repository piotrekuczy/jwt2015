package pl.edu.piotrekuczy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;

public class SpineActor extends Actor {

	// renderers

	SpriteBatch batch;
	ShapeRenderer shpr;
	SkeletonRenderer sr;

	// spine data

	String spineName;
	TextureAtlas spineAtlas;
	SkeletonJson spineJson;
	SkeletonData spineSkeletonData;
	Skeleton spineSkeleton;
	AnimationState state;
	float animationTime = 0;
	boolean flipThatShit;

	// gameplay variables
	
	private int hp;
	private int hpMax;
	private boolean deleted = false;

	public SpineActor(SpriteBatch batch, SkeletonRenderer sr, ShapeRenderer shpr, String spineName, boolean flipThatShit, int hp) {

		this.batch = batch;
		this.shpr = shpr;
		this.sr = sr;
		this.spineName = spineName;
		this.flipThatShit = flipThatShit;

		this.hp = hp;
		this.hpMax = hp;

		spineAtlas = new TextureAtlas(Gdx.files.internal(spineName + ".atlas"));
		spineJson = new SkeletonJson(spineAtlas);
		spineSkeletonData = spineJson.readSkeletonData(Gdx.files.internal(spineName + ".json"));
		spineSkeleton = new Skeleton(spineSkeletonData);

		if (flipThatShit) {
			spineSkeleton.setFlipX(true);
		}
		AnimationStateData stateData = new AnimationStateData(spineSkeletonData);
		state = new AnimationState(stateData);
		state.setAnimation(0, "idle", true);

		// spineSkeleton.setColor(new Color(0, 1, 1, 1));

		this.setBounds(0, 0, 50, 50);

		this.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("down");
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("up");
			}
		});
	}

	@Override
	public void act(float delta) {
		checkEnemyHp();
		super.act(delta);
		spineSkeleton.setX(getX());
		spineSkeleton.setY(getY());
		state.update(Gdx.graphics.getDeltaTime());
		state.apply(spineSkeleton);

		spineSkeleton.updateWorldTransform();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		spineSkeleton.getColor().a = parentAlpha;
		sr.draw(batch, spineSkeleton);
	}
	public void checkEnemyHp() {
		if (hp <= 0) {
			// kill that character
			this.setDeleted(true);
		}
	}
	public void renderHp(ShapeRenderer sr) {
		if (hp > 0) {
			sr.setColor(0, 0, 0, 1);
			sr.rect(getX() - (getWidth() / 2) * 0.7f, 390, ((getWidth() * 0.7f) * (100)) / 100, 10);
			sr.setColor(1, (float) hp / hpMax, 0, 1);
			sr.rect(getX() - (getWidth() / 2) * 0.7f, 390, (getWidth() * 0.7f) * ((float) hp / hpMax), 10);
		}
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
}
