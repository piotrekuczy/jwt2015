package pl.edu.piotrekuczy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class Gamescreen implements Screen {

	// gamescreen states

	public enum GameState {
		TITLE, MAP, ARENA, GAMEOVER;
	}

	private GameState currentState;

	// main game class

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

	// stage
	Stage stage;

	// gameplay

	int level = 0;
	int pulaGracza, pulaPrzeciwnika = 0;
	boolean heroTurn = true;
	boolean rolled = false;
	// ktory raz losuje enemy
	boolean enemyOne, enemyTwo = false;
	// enemy zrollowal
	boolean enemyFail = false;
	// SWIATY
	private Array<Swiat> swiaty;

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
	SpineButton title, mapa;
	boolean pozwolSchowac = false;

	// kotara

	TextureAtlas kotaraAtlas;
	SkeletonJson kotaraJson;
	SkeletonData kotaraSkeletonData;
	Skeleton kotaraSkeleton;
	Animation kotaraIdleAnimation;
	AnimationState kotaraState;
	float kotaraAnimationTime = 0;

	// mapa

	TextureAtlas mapaAtlas;
	SkeletonJson mapaJson;
	SkeletonData mapaSkeletonData;
	Skeleton mapaSkeleton;
	Animation mapaIdleAnimation;
	AnimationState mapaState;
	float mapaAnimationTime = 0;

	// SpineActor spineact;

	// constructor
	public Gamescreen(Jamwteatrze game) {

		this.game = game;

		// set game state

		currentState = GameState.TITLE;
		// currentState = GameState.MAP;
		// currentState = GameState.ARENA;

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

		// gui

		font = Assets.manager.get(Assets.myFont, BitmapFont.class);
		font.getData().setScale(0.7f);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		scena = Assets.manager.get(Assets.scena, Texture.class);
		scena.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		panel = Assets.manager.get(Assets.panel, Texture.class);
		panel.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// GENEROWANIE GAMEPLAYU

		generateGameplay();

		// DICES

		heroDices = new Array<Dice>();
		enemyDices = new Array<Dice>();

		// ------ generate hero dieces
		heroDices.add(new Dice(this, true));
		heroDices.add(new Dice(this, true));
		// heroDices.add(new Dice(this));

		numberHeroDieces = heroDices.size;
		baseHeroDieces = numberHeroDieces;

		for (Dice dice : heroDices) {
			dice.debug();
			dice.setTouchable(Touchable.disabled);
			dice.setVisible(false);
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

		// spineact = new SpineActor(batch, sr, "characters/template");
		// spineact.setTouchable(Touchable.disabled);
		// spineact.setVisible(false);
		// spineact.debug();
		// spineact.addAction(moveTo(300, 100, 0.5f, Interpolation.linear));
		// stage.addActor(spineact);

		// kotara

		kotaraAtlas = new TextureAtlas(Gdx.files.internal("gui/kotara.atlas"));
		kotaraJson = new SkeletonJson(kotaraAtlas);
		kotaraSkeletonData = kotaraJson.readSkeletonData(Gdx.files.internal("gui/kotara.json"));
		kotaraSkeleton = new Skeleton(kotaraSkeletonData);
		kotaraIdleAnimation = kotaraSkeletonData.findAnimation("idle");
		AnimationStateData stateData = new AnimationStateData(kotaraSkeletonData);
		kotaraState = new AnimationState(stateData);
		kotaraState.addAnimation(0, "idle", true, 0);
		kotaraSkeleton.setPosition(-200, -150);

		// title

		title = new SpineButton(batch, sr, "gui/title", "idle", 100, 0, 800, 600);
		title.debug();
		title.setClicked(false);

		if (currentState == GameState.TITLE) {
			resetTitle();
		}

		// title listeners

		title.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!title.isClicked() && title.getY() <= 100) {
					title.setClicked(true);
					fadeOutTitle(Gdx.graphics.getDeltaTime());
				}
				return true;
			}
		});

		stage.addActor(title);

		// mapa

		mapa = new SpineButton(batch, sr, "gui/mapa", "show0", 100, 0, 800, 600);
		mapa.setPosition(250, -900);
		mapa.debug();
		mapa.setClicked(false);

		// mapa listeners TODO!

		mapa.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				fadeOutMapa(Gdx.graphics.getDeltaTime());
				return true;
			}
		});

		stage.addActor(mapa);

		if (currentState == GameState.ARENA) {
			resetArena();
		}
	}

	public void generateGameplay() {
		// generuj swiaty
		// tablica swiatow (0123)
		swiaty = new Array<Swiat>();
		swiaty.add(new Swiat());
		swiaty.add(new Swiat());
		swiaty.add(new Swiat());
		swiaty.add(new Swiat());
		System.out.println("ilosc swiatow w grze =" + swiaty.size);
		// ustawienie danych questow
		swiaty.get(0).getQuesty().add(new Quest("Swiat0-Quest1"));
		swiaty.get(0).getQuesty().add(new Quest("Swiat0-Quest2"));
		swiaty.get(0).getQuesty().add(new Quest("Swiat0-Quest3"));
		swiaty.get(1).getQuesty().add(new Quest("Swiat1-Quest1"));
		swiaty.get(1).getQuesty().add(new Quest("Swiat1-Quest2"));
		swiaty.get(1).getQuesty().add(new Quest("Swiat1-Quest3"));
		swiaty.get(2).getQuesty().add(new Quest("Swiat2-Quest1"));
		swiaty.get(2).getQuesty().add(new Quest("Swiat2-Quest2"));
		swiaty.get(2).getQuesty().add(new Quest("Swiat2-Quest3"));
		swiaty.get(3).getQuesty().add(new Quest("Swiat3-Quest1"));
		swiaty.get(3).getQuesty().add(new Quest("Swiat3-Quest2"));
		swiaty.get(3).getQuesty().add(new Quest("Swiat3-Quest3"));

		// z kazdego swiata
		for (Swiat swiat : swiaty) {
			// wydrukuj nazwy questow
			for (int i = 0; i < swiat.getQuesty().size; i++) {
				System.out.println(swiat.getQuesty().get(i).questName);
			}
		}
	}

	public void resetTitle() {
		title.setPosition(250, 900);
		if (currentState == GameState.TITLE) {
			title.addAction(moveTo(200, 20, 2.0f, Interpolation.bounceOut));
		}
		// show in animation (repeat 0)
		title.getState().setAnimation(0, "idle", true);
	}

	public void resetArena() {
		currentState = GameState.ARENA;
		// show hero dices
		for (int i = 0; i < heroDices.size; i++) {
			heroDices.get(i).setPosition(105 + (i * 133), -150);
		}
		for (Dice dice : heroDices) {
			dice.debug();
			dice.setTouchable(Touchable.enabled);
			dice.setVisible(true);
		}
		for (int i = 0; i < heroDices.size; i++) {
			heroDices.get(i).setPosition(105 + (i * 133), -150);
			if (heroTurn) {
				heroDices.get(i).addAction(moveTo(105 + (i * 133), 37, 1.0f, Interpolation.bounceOut));
			}
		}
	}

	@Override
	public void show() {
	}

	public void gamestates() {
		switch (currentState) {
		case TITLE:
			updateTitle(Gdx.graphics.getDeltaTime());
			break;
		case MAP:
			updateMap(Gdx.graphics.getDeltaTime());
			break;
		case ARENA:
			updateArena(Gdx.graphics.getDeltaTime());
			break;
		case GAMEOVER:
			// updateReady(delta);
			break;
		default:
			// updateRunning(delta);
			break;
		}
	}

	public void fadeOutMapa(float daleta) {
		if (!mapa.isClicked() && pozwolSchowac) {
			mapa.setClicked(true);
			// schowaj mape
			mapa.addAction(moveTo(250, -900, 1.0f, Interpolation.fade));
			// otworz kotare
			kotaraState.setAnimation(0, "open", false);
			// uruchom mechanike rozgrywki
			resetArena();

			// for (Dice dice : heroDices) {
			// dice.setTouchable(Touchable.enabled);
			// dice.setVisible(true);
			// }
		}
	}

	public void fadeOutTitle(float delta) {
		// System.out.println("fade out title");
		// hide title
		title.addAction(moveTo(200, 900, 1.0f, Interpolation.fade));
		// show map
		// show in animation (repeat 0)
		mapa.getState().setAnimation(0, "in", false);
		mapa.addAction(sequence(moveTo(250, 100, 2.0f, Interpolation.bounceOut), run(new Runnable() {
			public void run() {
				// after action show idle animation based on actual players
				// level!
				mapa.getState().addAnimation(0, "show0", false, 0);
				if (level == 0) {
					mapa.getState().addAnimation(0, "show1", false, 0);
				}
				if (level == 1) {
					mapa.getState().addAnimation(0, "show2", false, 0);
				}
				if (level == 2) {
					mapa.getState().addAnimation(0, "show3", false, 0);
				}
				if (level == 3) {
					mapa.getState().addAnimation(0, "show4", false, 0);
				}
				// pozwol tutaj schowac mape jesli to potrzebne
				pozwolSchowac = true;
				currentState = GameState.MAP;
			}
		})));
	}

	public void updateMap(float delta) {
		// System.out.println("update map");
		// draw kotara
		batch.begin();
		kotaraState.update(Gdx.graphics.getDeltaTime());
		kotaraState.apply(kotaraSkeleton);
		kotaraSkeleton.updateWorldTransform();
		sr.draw(batch, kotaraSkeleton);
		batch.end();
	}

	public void updateTitle(float delta) {
		// System.out.println("update title");
		// draw kotara
		batch.begin();
		kotaraState.update(Gdx.graphics.getDeltaTime());
		kotaraState.apply(kotaraSkeleton);
		kotaraSkeleton.updateWorldTransform();
		sr.draw(batch, kotaraSkeleton);
		batch.end();
		// sprawdz czy kotara sie odslonila
		if (kotaraState.getCurrent(0) == null) {
			System.out.println("DZIOBAK");
			currentState = GameState.MAP;
		}
	}

	public void updateArena(float delta) {
		// System.out.println("update arena");
		batch.begin();
		batch.draw(scena, 0, 0);
		batch.draw(panel, 20, 10);
		batch.draw(panel, 700, 10);
		// total power numbers
		font.draw(batch, "total: ", 490, 130);
		font.draw(batch, pulaGracza + "", 505, 100, 50, Align.center, false);
		font.draw(batch, "total: ", 695, 130);
		font.draw(batch, pulaPrzeciwnika + "", 710, 100, 50, Align.center, false);
		batch.end();

		if (heroTurn) {
			checkHeroDices();
		} else {
			generateEnemyDices();
		}

		// draw kotara
		batch.begin();
		kotaraState.update(Gdx.graphics.getDeltaTime());
		kotaraState.apply(kotaraSkeleton);
		kotaraSkeleton.updateWorldTransform();
		sr.draw(batch, kotaraSkeleton);
		batch.end();
		batch.begin();
		// turn info
		if (heroTurn) {
			font.draw(batch, "TURA KRAKUSA", 50, 700);
		} else {
			font.draw(batch, "TURA GOROLA", 1050, 700);
		}
		batch.end();
	}

	@Override
	public void render(float delta) {
		cameraInput();

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		dr.getShapeRenderer().setProjectionMatrix(camera.combined);

		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		gamestates();

		stage.act(delta);
		stage.draw();
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
		if (enemyOne == true && enemyTwo == false && enemyTurnTimer >= 2) {
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
		if (enemyOne == true && enemyTwo == true && enemyTurnTimer >= 3) {
			if (!enemyFail) {

				// TUTAJ DOPISAC ZE MA MIEC WYBOR MIEDZY DALSZYM LOSOWANIEM A
				// ATAKIEM

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
		if (enemyOne == true && enemyTwo == true && enemyFail == true && enemyTurnTimer >= 4) {
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

}
