package pl.edu.piotrekuczy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
//	private float rotationSpeed = 0.5f;

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
		
		
	}

	@Override
	public void render(float delta) {
		cameraInput();

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		dr.getShapeRenderer().setProjectionMatrix(camera.combined);

		Gdx.graphics.getGL20().glClearColor(1, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
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
		// TODO Auto-generated method stub

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
		// if (Gdx.input.isKeyPressed(Input.Keys.A)) {
		// camera.zoom += 0.02;
		// }
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
}
