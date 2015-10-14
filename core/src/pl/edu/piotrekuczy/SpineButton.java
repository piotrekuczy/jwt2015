package pl.edu.piotrekuczy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;

public class SpineButton extends Actor {

	// renderers

	SpriteBatch batch;
	SkeletonRenderer sr;

	// spine data

	String spineName, animIdle;
	TextureAtlas spineAtlas;
	SkeletonJson spineJson;
	SkeletonData spineSkeletonData;
	Skeleton spineSkeleton;
	Animation spineIdleAnimation;
	AnimationState state;
	float animationTime = 0;

	// active arena offsets

	int bboxX, bboxY, bboxWidth, bboxHeight;
	
	// flags
	
	boolean clicked = false;

	public SpineButton(SpriteBatch batch, SkeletonRenderer sr, String spineName, String animIdle, int bboxX,
			int bboxY, int bboxWidth, int bboxHeight) {

		this.batch = batch;
		this.sr = sr;
		this.spineName = spineName;
		this.animIdle = animIdle;
		this.bboxX = bboxX;
		this.bboxY = bboxY;
		this.bboxWidth = bboxWidth;
		this.bboxHeight = bboxHeight;

		spineAtlas = new TextureAtlas(Gdx.files.internal(spineName + ".atlas"));
		spineJson = new SkeletonJson(spineAtlas);
		spineSkeletonData = spineJson.readSkeletonData(Gdx.files.internal(spineName + ".json"));
		spineSkeleton = new Skeleton(spineSkeletonData);
		spineIdleAnimation = spineSkeletonData.findAnimation(animIdle);

		AnimationStateData stateData = new AnimationStateData(spineSkeletonData);
		state = new AnimationState(stateData);
		

		// spineSkeleton.setColor(new Color(0, 1, 1, 1));

		this.setBounds(bboxX, bboxY, bboxWidth, bboxHeight);

	}

	@Override
	public void act(float delta) {
		super.act(delta);
		spineSkeleton.setX(getX());
		spineSkeleton.setY(getY());
		this.setBounds(getX(), getY(), bboxWidth, bboxHeight);
		state.update(Gdx.graphics.getDeltaTime());
		state.apply(spineSkeleton);
		spineSkeleton.updateWorldTransform();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		spineSkeleton.getColor().a = parentAlpha;
		sr.draw(batch, spineSkeleton);
	}

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public AnimationState getState() {
		return state;
	}

	public void setState(AnimationState state) {
		this.state = state;
	}
	
}
