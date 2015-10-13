package pl.edu.piotrekuczy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class Gamescreen implements Screen {

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

	// stage
	Stage stage;

	// animations
	// Animation diceStandardAnimation;

	// gameplay
	int pulaGracza, pulaPrzeciwnika = 0;
	boolean heroTurn = true;
	boolean rolled = false;
	// ktory raz losuje enemy
	boolean enemyOne, enemyTwo = false;
	// enemy zrollowal
	boolean enemyFail = false;

	// timers
	float delay = 0.5f; // seconds
	float delayTimer = 0;

	float enemyTurnTimer = 0;

	// DICES

	// hero dices
	private Array<Dice> heroDices;
	// ilosc kosci gracza
	private int baseHeroDieces, numberHeroDieces = 0;

	// enemy dices
	private Array<Dice> enemyDices;
	// ilosc kosci przeciwnika
	private int baseEnemyDieces, numberEnemyDieces = 0;

	// gui

	BitmapFont font;
	Texture scena, panel;
	
	
	SpineActor spineact;
	

	// constructor
	public Gamescreen(Jamwteatrze game) {

		this.game = game;

		batch = new SpriteBatch();
		camera = new OrthographicCamera(1280, 720);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		viewport = new FitViewport(1280, 720, camera);
		camera.update();

		// stage

		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);

		// spine
		sr = new SkeletonRenderer();
		dr = new SkeletonRendererDebug();
		dr.setBoundingBoxes(false);
		dr.setRegionAttachments(false);

		// sprite animations

		// diceStandardAnimation =
		// makeAnim(Assets.manager.get(Assets.diceStandardAtlas,
		// TextureAtlas.class), "dice",
		// 4, 0.2f, "LOOP_PINGPONG");

		// gui

		font = Assets.manager.get(Assets.myFont, BitmapFont.class);
		font.getData().setScale(0.7f);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		scena = Assets.manager.get(Assets.scena, Texture.class);
		scena.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		panel = Assets.manager.get(Assets.panel, Texture.class);
		panel.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// DICES

		heroDices = new Array<Dice>();
		enemyDices = new Array<Dice>();

		// ------ generate hero dieces
		heroDices.add(new Dice(this, true));
		heroDices.add(new Dice(this, true));
		// heroDices.add(new Dice(this));

		numberHeroDieces = heroDices.size;
		baseHeroDieces = numberHeroDieces;

		for (int i = 0; i < heroDices.size; i++) {
			heroDices.get(i).setPosition(105 + (i * 133), -150);
			if (heroTurn) {
				heroDices.get(i).addAction(moveTo(105 + (i * 133), 37, 1.0f, Interpolation.bounceOut));
			}
		}

		for (Dice dice : heroDices) {
			dice.debug();
			stage.addActor(dice);
		}

		// ------ generate enemy dieces
		enemyDices.add(new Dice(this, false));
		enemyDices.add(new Dice(this, false));

		numberEnemyDieces = enemyDices.size;
		baseEnemyDieces = numberEnemyDieces;

		for (int i = 0; i < enemyDices.size; i++) {
			enemyDices.get(i).setPosition(785 + (i * 133), -150);
			if (!heroTurn) {
				enemyDices.get(i).addAction(moveTo(785 + (i * 133), 37, 1.0f, Interpolation.bounceOut));
			}
		}

		for (Dice dice : enemyDices) {
			dice.debug();
			stage.addActor(dice);
		}
		
		
		spineact = new SpineActor(batch, sr);
//		spineact.setPosition(100, 0);
//		spineact.setWidth(10);
//		spineact.setHeight(10);
//		spineact.setScale(2f);
		spineact.setTouchable(Touchable.enabled);
		spineact.debug();
		spineact.addAction(moveTo(300, 100, 0.5f, Interpolation.linear));
		stage.addActor(spineact);
		
	}

	@Override
	public void show() {
		System.out.println("jwt game screen show method");
	}

	@Override
	public void render(float delta) {
		cameraInput();

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		dr.getShapeRenderer().setProjectionMatrix(camera.combined);

		Gdx.graphics.getGL20().glClearColor(1, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		batch.begin();
		batch.draw(scena, 0, 0);
		batch.draw(panel, 20, 10);
		batch.draw(panel, 700, 10);
		batch.end();

		stage.act(delta);
		stage.draw();

		batch.begin();

		// total power numbers
		font.draw(batch, "total: ", 490, 130);
		font.draw(batch, pulaGracza + "", 505, 100, 50, Align.center, false);
		font.draw(batch, "total: ", 695, 130);
		font.draw(batch, pulaPrzeciwnika + "", 710, 100, 50, Align.center, false);
		// turn info
		if (heroTurn) {
			font.draw(batch, "TURA KRAKUSA", 50, 700);
		} else {
			font.draw(batch, "TURA GOROLA", 1050, 700);
		}
		batch.end();

		if (heroTurn) {
			checkHeroDices();
		} else {
			generateEnemyDices();
		}

		// System.out.println("pula gracza= " + pulaGracza);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		stage.getViewport().update(width, height, true);
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
		stage.dispose();
		batch.dispose();
	}

	public int getPulaPrzeciwnika() {
		return pulaPrzeciwnika;
	}

	public void setPulaPrzeciwnika(int pulaPrzeciwnika) {
		this.pulaPrzeciwnika = pulaPrzeciwnika;
	}

	public int getPulaGracza() {
		return pulaGracza;
	}

	public void setPulaGracza(int pulaGracza) {
		this.pulaGracza = pulaGracza;
	}

	public int getNumberEnemyDieces() {
		return numberEnemyDieces;
	}

	public void setNumberEnemyDieces(int numberEnemyDieces) {
		this.numberEnemyDieces = numberEnemyDieces;
	}

	public int getNumberHeroDieces() {
		return numberHeroDieces;
	}

	public void setNumberHeroDieces(int numberHeroDieces) {
		this.numberHeroDieces = numberHeroDieces;
	}

	public boolean isRolled() {
		return rolled;
	}

	public void setRolled(boolean rolled) {
		this.rolled = rolled;
	}

	public float getDelayTimer() {
		return delayTimer;
	}

	public void setDelayTimer(float delayTimer) {
		this.delayTimer = delayTimer;
	}

	public float getEnemyTurnTimer() {
		return enemyTurnTimer;
	}

	public void setEnemyTurnTimer(float enemyTurnTimer) {
		this.enemyTurnTimer = enemyTurnTimer;
	}

	public void swapTury() {
		heroTurn = !heroTurn;
		// enemyTurnTimer = 0;

		if (heroTurn) {
			System.out.println("tura hero");
			pulaGracza = 0;
			pulaPrzeciwnika = 0;
			numberHeroDieces = 2;
			baseHeroDieces = numberHeroDieces;
			for (Dice dice : heroDices) {
				dice.setTouchable(Touchable.enabled);
			}
			for (Dice dice : enemyDices) {
				dice.addAction(moveTo(dice.getX(), -150, 0.2f, Interpolation.linear));
			}
			for (int i = 0; i < heroDices.size; i++) {
				heroDices.get(i).setStopklatka(false);
				heroDices.get(i).setTouchable(Touchable.enabled);
				heroDices.get(i).setPosition(105 + (i * 133), -100);
				heroDices.get(i).addAction(moveTo(105 + (i * 133), 37, 1.0f, Interpolation.bounceOut));
			}
		} else {

			System.out.println("tura enemy");
			pulaGracza = 0;
			pulaPrzeciwnika = 0;
			enemyTurnTimer = 0;
			enemyOne = false;
			enemyTwo = false;
			enemyFail = false;
			for (Dice dice : heroDices) {
				dice.addAction(moveTo(dice.getX(), -150, 0.2f, Interpolation.linear));
				dice.setStopklatka(true);
			}
			numberHeroDieces = 0;

			numberEnemyDieces = 2;
			baseEnemyDieces = numberEnemyDieces;
			for (int i = 0; i < enemyDices.size; i++) {
				enemyDices.get(i).setStopklatka(false);
				enemyDices.get(i).setPosition(785 + (i * 133), -100);
				enemyDices.get(i).addAction(moveTo(785 + (i * 133), 37, 1.0f, Interpolation.bounceOut));
			}
		}
		rolled = false;
	}

	private void generateEnemyDices() {

		// po 1 sec wylosuj pierwsza kostke
		enemyTurnTimer += Gdx.graphics.getDeltaTime();
		if (enemyOne == false && enemyTurnTimer >= 1) {
			enemyDices.get(0).setStopklatka(true);
			if (enemyDices.get(0).numerKlatki != 1) {
				pulaPrzeciwnika = pulaPrzeciwnika + enemyDices.get(0).numerKlatki;
				enemyOne = true;
				// enemyDices.get(0).enemyDiceLogic();
			} else {
				// enemy wylosowal 1
				enemyOne = true;
				enemyFail = true;
			}
		}
		// po 2 sec wylosuj pierwsza kostke
		if (enemyOne==true && enemyTwo == false && enemyTurnTimer >= 2) {
			if (!enemyFail) {
				enemyDices.get(1).setStopklatka(true);
				if (enemyDices.get(1).numerKlatki != 1) {
					pulaPrzeciwnika = pulaPrzeciwnika + enemyDices.get(1).numerKlatki;
					enemyTwo = true;
					// enemyDices.get(1).enemyDiceLogic();

				} else {
					// enemy wylosowal 1
					enemyTwo = true;
					enemyFail = true;
				}
			} else {
				System.out.println("enemy roll na pierwszej");
				swapTury();
			}
		}
		// po 3 sec swapnij ture (wlasc. zaatakuj)
		if (enemyOne==true && enemyTwo==true && enemyTurnTimer >= 3) {
			if (!enemyFail) {
				
				// TUTAJ DOPISAC ZE MA MIEC WYBOR MIEDZY DALSZYM LOSOWANIEM A ATAKIEM
				
				// atakuj
				System.out.println("ATAK");
				swapTury();
			} else {
				// spali jednak i swapnij
				System.out.println("enemy roll na drugiej");
				enemyFail = true;
			}
		}
		// po 4 sec swapnij ture jesli enemy wylosowal na drugiej roll
				if (enemyOne==true && enemyTwo==true && enemyFail==true && enemyTurnTimer >= 4) {
					System.out.println("koniec cyklu generowania enemy");
					swapTury();
				}
	}

	private void checkHeroDices() {
		// jesli liczba wolnych niestopklatkowych kostek gracza rowna sie zero
		// to przeladuj je
		if (numberHeroDieces <= 0) {
			delayTimer += Gdx.graphics.getDeltaTime();
			if (delayTimer >= delay) {
				for (Dice dice : heroDices) {
					dice.addAction(sequence(moveTo(dice.getX(), -150, 0.2f, Interpolation.linear),
							moveTo(dice.getX(), 37, 1.0f, Interpolation.bounceOut)));
					dice.setStopklatka(false);
					numberHeroDieces = baseHeroDieces;
					delayTimer = 0;
				}
			}
		}
		// jesli gracz wylosowal na ktorejkolwiek kostce 1 ale pierwszy raz
		if (numberHeroDieces == 2 && rolled) {
			for (Dice dice : heroDices) {
				dice.setTouchable(Touchable.disabled);
			}
			// System.out.println("ROLLED");
			delayTimer += Gdx.graphics.getDeltaTime();
			if (delayTimer >= delay) {
				swapTury();
			}
		}

		// jesli gracz wylosowal na ktorejkolwiek kostce 1 ale nie pierwszy raz
		if (numberHeroDieces == 1 && rolled)

		{
			// System.out.println("ROLLED");
			delayTimer += Gdx.graphics.getDeltaTime();
			if (delayTimer >= delay) {
				swapTury();
			}
		}

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

	public Animation makeAnim(TextureAtlas atlasName, String regionName, int frameNumber, float speed,
			String playType) {

		Array<AtlasRegion[]> klatki = new Array<AtlasRegion[]>(); // tablica z
																	// klatkami
		Array<Animation> animacje = new Array<Animation>(); // tablica z
															// animacjami

		klatki.add(new AtlasRegion[frameNumber]); // zrob tablice z iloscia
													// klatek

		for (int c = 0; c < frameNumber; c++) {
			klatki.peek()[c] = atlasName.findRegion(regionName + String.format("%02d", c + 1));
		}

		animacje.add(new Animation(speed, klatki.peek())); // zrob animacje

		if (playType == "NORMAL") {
			animacje.peek().setPlayMode(Animation.PlayMode.NORMAL);
		}
		if (playType == "LOOP") {
			animacje.peek().setPlayMode(Animation.PlayMode.LOOP);
		}
		if (playType == "LOOP_PINGPONG") {
			animacje.peek().setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		}
		if (playType == "LOOP_RANDOM") {
			animacje.peek().setPlayMode(Animation.PlayMode.LOOP_RANDOM);
		}
		if (playType == "REVERSED") {
			animacje.peek().setPlayMode(Animation.PlayMode.REVERSED);
		}
		if (playType == "LOOP_REVERSED") {
			animacje.peek().setPlayMode(Animation.PlayMode.LOOP_REVERSED);
		}

		return animacje.peek();
	}
}
