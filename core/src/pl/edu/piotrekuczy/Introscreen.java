package pl.edu.piotrekuczy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

public class Introscreen implements Screen {

	Jamwteatrze game;

	// renderers
	SpriteBatch batch;
	ShapeRenderer shpr = new ShapeRenderer();
	SkeletonRenderer sr;
	SkeletonRendererDebug dr;

	// camera & viwport
	OrthographicCamera camera;
	Viewport viewport;
	Boolean fullscreen = false;
	// private float rotationSpeed = 0.5f;
	boolean toTheEnd = false;

	// SPINE logomenu

	TextureAtlas logoAtlas;
	SkeletonJson logoJson;
	SkeletonData logoSkeletonData;
	Skeleton logoSkeleton;
	Animation logoInAnimation;
	float animationTime = 0;
	AnimationState state;

	// constructor

	public Introscreen(Jamwteatrze game) {
		this.game = game;

		batch = new SpriteBatch();
		camera = new OrthographicCamera(1280, 720);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		viewport = new FitViewport(1280, 720, camera);
		camera.update();

		// spine
		sr = new SkeletonRenderer();
		dr = new SkeletonRendererDebug();
		dr.setBoundingBoxes(false);
		dr.setRegionAttachments(false);

	}

	@Override
	public void show() {
		System.out.println("jwt intro screen show method");
		toTheEnd = false;
		System.out.println("show method of menuscreen");

		// Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();

		// spine
		sr = new SkeletonRenderer();
		camera = new OrthographicCamera(1280, 720);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		viewport = new FitViewport(1280, 720, camera);
		camera.update();
		// spine

		logoAtlas = new TextureAtlas(Gdx.files.internal("intro/intro.atlas"));
		logoJson = new SkeletonJson(logoAtlas);
		logoSkeletonData = logoJson.readSkeletonData(Gdx.files.internal("intro/intro.json"));
		logoSkeleton = new Skeleton(logoSkeletonData);
		logoInAnimation = logoSkeletonData.findAnimation("animation");
		logoSkeleton.getRootBone().setScale(1f);
		logoSkeleton.setPosition(640, 0);

		logoSkeleton.updateWorldTransform();
		AnimationStateData stateData = new AnimationStateData(logoSkeletonData);
		state = new AnimationState(stateData);
		state.setAnimation(0, "animation", false);

	}

	@Override
	public void render(float delta) {
		cameraInput();

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		dr.getShapeRenderer().setProjectionMatrix(camera.combined);

		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		state.update(Gdx.graphics.getDeltaTime());
		state.apply(logoSkeleton);
		logoSkeleton.updateWorldTransform();
		batch.begin();
		sr.draw(batch, logoSkeleton);
		batch.end();
		if (state.getCurrent(0) == null) {
			game.setScreen(game.getGamescreen());
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	private void cameraInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.TAB)) {
			fullscreen = !fullscreen;
			if (fullscreen) {
				Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
						Gdx.graphics.getDesktopDisplayMode().height, fullscreen);
			} else {
				Gdx.graphics.setDisplayMode(1280, 720, false);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			// camera.zoom += 0.02;
			game.setScreen(game.getGamescreen());
		}
		// if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
		// camera.zoom -= 0.02;
		// }
		// if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
		// camera.translate(3, 0, 0);
		// }
		// if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
		// camera.translate(-3, 0, 0);
		// }
		// if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
		// camera.translate(0, 3, 0);
		// }
		// if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
		// camera.translate(0, -3, 0);
		// }
		// if (Gdx.input.isKeyPressed(Input.Keys.W)) {
		// camera.rotate(-rotationSpeed, 0, 0, 1);
		// }
		// if (Gdx.input.isKeyPressed(Input.Keys.E)) {
		// camera.rotate(rotationSpeed, 0, 0, 1);
		// }

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		batch.dispose();
		shpr.dispose();
	}

}
