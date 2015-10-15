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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
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

	private SpineActor playerIcon;
	private Array<SpineActor> enemys;

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
	Stage stage, stageCharacters;

	// gameplay

	int level = 0;
	int nrQesta = 0;
	int pulaGracza, pulaPrzeciwnika = 0;
	boolean heroTurn = true;
	boolean rolled = false;
	// ktory raz losuje enemy
	boolean enemyOne, enemyTwo = false;
	// enemy zrollowal
	boolean enemyFail = false;

	boolean gameover = false;
	boolean atakowanie = false;

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
	Texture scena, panel, swiat02tlo, swiat00tlo, swiat01tlo, swiat03tlo, atakujTex;
	SpineButton title, mapa;
	boolean pozwolSchowac = false;
	ImageButton atakujButton;

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
	float mapaX = 220;
	float mapaY = 60;

	// SpineActor spineact;

	// ---------------------------------------------------- constructor

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

		// spine

		sr = new SkeletonRenderer();
		dr = new SkeletonRendererDebug();
		dr.setBoundingBoxes(false);
		dr.setRegionAttachments(false);

		// stage

		stage = new Stage(viewport, batch);
		stageCharacters = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);

		// ----------------- DEKLARACJE

		playerIcon = new SpineActor(batch, sr, shpr, "characters/goral", true, game.player.playerHp);

		playerIcon.setPosition(-400, 180);
		stageCharacters.addActor(playerIcon);

		

		generujEnemys();

		// atakuj button

		atakujTex = Assets.manager.get(Assets.atakuj, Texture.class);
		atakujTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		atakujButton = new ImageButton(new SpriteDrawable(new Sprite(atakujTex)));
		atakujButton.setPosition(565, -200);
		atakujButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (pulaGracza > 0 && !atakowanie) {
					atakowanie = true;
					System.out.println("atakuj button pressed");
					atakuj();
				}
				return true;
			}
		});

		stage.addActor(atakujButton);

		// gui

		font = Assets.manager.get(Assets.myFont, BitmapFont.class);
		font.getData().setScale(0.7f);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		scena = Assets.manager.get(Assets.scena, Texture.class);
		scena.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		panel = Assets.manager.get(Assets.panel, Texture.class);
		panel.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// tla

		swiat00tlo = Assets.manager.get(Assets.swiat00tlo, Texture.class);
		swiat00tlo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		swiat01tlo = Assets.manager.get(Assets.swiat01tlo, Texture.class);
		swiat01tlo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		swiat02tlo = Assets.manager.get(Assets.swiat02tlo, Texture.class);
		swiat02tlo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		swiat03tlo = Assets.manager.get(Assets.swiat03tlo, Texture.class);
		swiat03tlo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

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
			// dice.debug();
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
			enemyDices.get(i).setPosition(725 + (i * 133), -150);
			if (!heroTurn) {
				enemyDices.get(i).addAction(moveTo(725 + (i * 133), 37, 1.0f, Interpolation.bounceOut));
			}
		}

		for (Dice dice : enemyDices) {
			// dice.debug();
			stage.addActor(dice);
		}

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
		// title.debug();
		title.setClicked(false);

		if (currentState == GameState.TITLE) {
			resetTitle();
		}

		// title listeners

		title.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!title.isClicked() && title.getY() <= 20) {
					title.setClicked(true);
					fadeOutTitle(Gdx.graphics.getDeltaTime());
				}
				return true;
			}
		});

		stage.addActor(title);

		// mapa

		mapa = new SpineButton(batch, sr, "gui/mapa", "show0", 100, 0, 800, 600);
		mapa.setPosition(mapaX, -900);
		// mapa.debug();
		mapa.setClicked(false);

		// mapa listeners TODO!

		mapa.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				fadeOutMapa(Gdx.graphics.getDeltaTime());
				return true;
			}
		});

		stage.addActor(mapa);

		// if (currentState == GameState.ARENA) {
		// resetArena();
		// }
	}
	// ------------------- NOWE METODY

	public void showPlayerIcon() {

		playerIcon.addAction(moveTo(100, 180, 1.0f, Interpolation.linear));

	}

	public void hidePlayerIcon() {
		playerIcon.setPosition(-400, 180);
	}

	public void showEnemy() {
		enemys.peek().setPosition(1500, 180);
		stageCharacters.addActor(enemys.peek());
		enemys.peek().addAction(moveTo(1100, 180, 1.0f, Interpolation.linear));
		// System.out.println("stan enemys po ich pokazaniu");
		for (SpineActor enemy : enemys) {
			// System.out.println("enemy hp = " + enemy.getHp());
			// System.out.println("enemy status = " + enemy.isDeleted());
		}
	}

	public void hideEnemy() {
		enemys.peek().setPosition(1500, 180);
	}

	public void killEnemy() {

		enemys.peek().addAction(sequence(moveTo(1500, 180, 1.0f, Interpolation.linear), run(new Runnable() {
			public void run() {
				// enemy wyjechal juz za ekran, mozna go usunac
				enemys.peek().setDeleted(true);
			}
		})));
	}

	public void setInitialValues() {
		heroTurn = true;
		rolled = false;
		// ktory raz losuje enemy
		enemyOne = false;
		enemyTwo = false;
		// enemy zrollowal
		enemyFail = false;
		gameover = false;
		atakowanie = false;
		pozwolSchowac = false;
		title.setClicked(false);
		mapa.setClicked(false);
	}

	public void atakuj() {
		// animacja ataku i powrot do idle

		playerIcon.getState().setAnimation(0, "atak", false);
		playerIcon.getState().addAnimation(0, "idle", true, 0);
		playerIcon.addAction(
				sequence(moveTo(playerIcon.getX() + 1000, playerIcon.getY(), 0.5f, Interpolation.circle),
						moveTo(100, playerIcon.getY(), 0.5f, Interpolation.fade)));

		// odejmij punkty przeciwnikowi
		enemys.peek().setHp(enemys.peek().getHp() - pulaGracza);

		pulaGracza = 0;
		pulaPrzeciwnika = 0;

		// jesli nie zabiles przeciwnika (jeszcze zyje po ataku) to swapnij ture
		if (!(enemys.peek().getHp() <= 0)) {
			swapTury();
		} else {
			heroTurn = !heroTurn;
			swapTury();
		}
	}

	public void resetMapa() {
		mapa.setClicked(false);
		pozwolSchowac = false;
	}

	public void resetTitle() {
		gameover = false;
		title.setPosition(250, 900);
		if (currentState == GameState.TITLE) {
			title.addAction(moveTo(200, 20, 2.0f, Interpolation.bounceOut));
		}
		// show in animation (repeat 0)
		title.getState().setAnimation(0, "idle", true);

	}

	public void resetArena() {
		System.out.println("reset areny");
		currentState = GameState.ARENA;
		// show atakuj button
		atakujButton.addAction(moveTo(565, 0, 1.0f, Interpolation.bounceOut));
		// show hero dices
		for (int i = 0; i < heroDices.size; i++) {
			heroDices.get(i).setPosition(175 + (i * 133), -150);
		}
		for (Dice dice : heroDices) {
			// dice.debug();
			dice.setTouchable(Touchable.enabled);
			dice.setVisible(true);
		}
		for (int i = 0; i < heroDices.size; i++) {
			heroDices.get(i).setPosition(175 + (i * 133), -150);
			if (heroTurn) {
				heroDices.get(i).addAction(moveTo(175 + (i * 133), 5, 1.0f, Interpolation.bounceOut));
			}
		}
		// pokaz playerIcon / BOHATERA
		showPlayerIcon();
		if (enemys.size > 0) {
			// pokaz enemysa
			showEnemy();

		}
	}

	@Override
	public void show() {

		// TAK SIE DOSTAJE DO GLOBALNEGO PLAYER HP!
		// game.player.playerHp++;
		// System.out.println("php = "+game.player.playerHp);
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
			mapa.addAction(moveTo(mapaX, -900, 1.0f, Interpolation.fade));
			// otworz kotare
			kotaraState.setAnimation(0, "open", false);

			// uzupelnij();

			// uruchom mechanike rozgrywki
			resetArena();

			// for (Dice dice : heroDices) {
			// dice.setTouchable(Touchable.enabled);
			// dice.setVisible(true);
			// }
		}
	}

	public void fadeOutArena() {
		System.out.println("fade out arena");
		// wyjeb kostki
		for (Dice dice : heroDices) {
			dice.setStopklatka(false);
			dice.setTouchable(Touchable.disabled);
			dice.addAction(moveTo(dice.getX(), -200, 1.0f, Interpolation.fade));
		}
		//
		// wyjeb atakuj
		atakujButton.addAction(moveTo(565, -200, 1.0f, Interpolation.fade));
		// zamknij kotare
		kotaraState.setAnimation(0, "close", false);
		// if (kotaraState.getCurrent(0) == null) {
		// System.out.println("kotara sie zamknela");
		// }
		if (kotaraState.getCurrent(0).isComplete()) {
			System.out.println("REKSIO");
		}

		game.player.playerHp = game.initialHp;
		playerIcon.setHp(game.player.playerHp);
//		generujEnemys();
		title.setClicked(false);
		currentState = GameState.TITLE;
		resetMapa();
		resetTitle();
	}

	public void fadeOutTitle(float delta) {
		level =  MathUtils.random(0, 3);
		// System.out.println("fade out title");
		// hide title
		title.addAction(moveTo(200, 900, 1.0f, Interpolation.fade));
		// show map
		// show in animation (repeat 0)
		mapa.getState().setAnimation(0, "in", false);
		mapa.addAction(sequence(moveTo(mapaX, mapaY, 2.0f, Interpolation.bounceOut), run(new Runnable() {
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
		stage.act(delta);
		stage.draw();
	}

	public void updateTitle(float delta) {
		// System.out.println("update title");
		batch.begin();
		switch (level) {
		case 0:
			batch.draw(swiat00tlo, 0, 0);
			break;
		case 1:
			batch.draw(swiat01tlo, 0, 0);
			break;
		case 2:
			batch.draw(swiat02tlo, 0, 0);
			break;
		case 3:
			batch.draw(swiat03tlo, 0, 0);
			break;
		}
		// total power numbers
		font.draw(batch, "total: ", 35, 100);
		font.draw(batch, pulaGracza + "", 50, 70, 50, Align.center, false);
		font.draw(batch, "total: ", 1160, 100);
		font.draw(batch, pulaPrzeciwnika + "", 1175, 70, 50, Align.center, false);

		// actual hp
		font.draw(batch, game.player.playerHp + "", playerIcon.getX() - 25, playerIcon.getY() + 260, 50,
				Align.center, false);

		if (enemys.size > 0) {
			font.draw(batch, enemys.peek().getHp() + "", enemys.peek().getX() - 15, enemys.peek().getY() + 260);
		}

		batch.end();
		// draw kotara
		batch.begin();
		kotaraState.update(Gdx.graphics.getDeltaTime());
		kotaraState.apply(kotaraSkeleton);
		kotaraSkeleton.updateWorldTransform();
		sr.draw(batch, kotaraSkeleton);
		batch.end();
		// sprawdz czy kotara sie odslonila
		if (kotaraState.getCurrent(0) == null) {
			currentState = GameState.MAP;
		}
		stage.act(delta);
		stage.draw();
	}

	public void updateArena(float delta) {

		// gameover, zapraklo hp playerowi
		if (game.player.playerHp <= 0) {
//			killEnemy();
			fadeOutArena();
		}

		// zabiles wszystkie enemysy
		if (enemys.size <= 0) {
//			generujEnemys();
			fadeOutArena();
		}

		batch.begin();
		switch (level) {
		case 0:
			batch.draw(swiat00tlo, 0, 0);
			break;
		case 1:
			batch.draw(swiat01tlo, 0, 0);
			break;
		case 2:
			batch.draw(swiat02tlo, 0, 0);
			break;
		case 3:
			batch.draw(swiat03tlo, 0, 0);
			break;
		}
		// total power numbers
		font.draw(batch, "total: ", 35, 100);
		font.draw(batch, pulaGracza + "", 50, 70, 50, Align.center, false);
		font.draw(batch, "total: ", 1160, 100);
		font.draw(batch, pulaPrzeciwnika + "", 1175, 70, 50, Align.center, false);
		batch.end();

		if (heroTurn) {
			checkHeroDices();
		} else {
			generateEnemyDices();
		}

		stageCharacters.act(delta);
		stageCharacters.draw();

		batch.begin();

		// wypisywanie actual hp
		font.draw(batch, game.player.playerHp + "", playerIcon.getX() - 25, playerIcon.getY() + 260, 50,
				Align.center, false);

		if (enemys.size > 0) {
			font.draw(batch, enemys.peek().getHp() + "", enemys.peek().getX() - 15, enemys.peek().getY() + 260);
		}
		batch.end();

		// rysowanie paskow hp
		shpr.begin(ShapeType.Filled);
		playerIcon.renderHp(shpr);
		if (enemys.size > 0) {
			enemys.peek().renderHp(shpr);
		}
		shpr.end();

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
			font.draw(batch, "TURA GRACZA", 50, 700);
		} else {
			font.draw(batch, "TURA OBCYCH", 1050, 700);
		}
		// status przeciwnikow
		font.draw(batch, enemys.size + "", 600, 700, 50, Align.center, false);
		batch.end();
		stage.act(delta);
		stage.draw();

		// check for deleting enemy from quest

		for (int i = 0; i < enemys.size; i++) {
			if (enemys.get(i).isDeleted()) {
				enemys.get(i).remove();
				enemys.removeIndex(i);
				if (enemys.size > 0) {
					if(enemys.size>=45){
						game.player.playerHp = game.player.playerHp + 10;
						playerIcon.setHp(game.player.playerHp);
					}
					showEnemy();
				}
			}
		}
	}

	public void generujEnemys() {
		enemys = new Array<SpineActor>();
		// -------------------------------------------------- VVVVVVVVVVVVV
		// generowanie przeciwnikow

		for (int i = 0; i < 50; i++) {

			// losuj jaki potworek
			int random = MathUtils.random(0, 1);
			if (random == 0) {
//				enemys.add(new SpineActor(batch, sr, shpr, "characters/ufo", false, 2));
				 enemys.add(new SpineActor(batch, sr, shpr, "characters/ufo",
				 false, 10 + (i * 5)));
			} else {
//				enemys.add(new SpineActor(batch, sr, shpr, "characters/owca", false, 2));
				 enemys.add(new SpineActor(batch, sr, shpr, "characters/owca",
				 false, 10 + (i * 5)));
			}
		}
		// odwrocenie tablicy zeby najsbalsi byli na jej koncu (czyli na
		// poczatku gry)
		enemys.reverse();

		// System.out.println("stan enemys po ich wygenerowaniu");
		for (SpineActor enemy : enemys) {
			// System.out.println("enemy hp = " + enemy.getHp());
		}

		// -------------------------------------------------- VVVVVVVVVVVVV
		// generowanie przeciwnikow
	}

	@Override
	public void render(float delta) {
		cameraInput();

		// iterowanie po questach

		// iterowanie po swiatach

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		dr.getShapeRenderer().setProjectionMatrix(camera.combined);

		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		gamestates();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		stage.getViewport().update(width, height, true);
		stageCharacters.getViewport().update(width, height, true);
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
			// System.out.println("tura hero");
			atakowanie = false;
			// pulaGracza = 0;
			// pulaPrzeciwnika = 0;
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
				heroDices.get(i).setPosition(175 + (i * 133), -100);
				heroDices.get(i).addAction(moveTo(175 + (i * 133), 5, 1.0f, Interpolation.bounceOut));
			}
		} else {

			// System.out.println("tura enemy");
			// pulaGracza = 0;
			// pulaPrzeciwnika = 0;
			enemyTurnTimer = 0;
			enemyOne = false;
			enemyTwo = false;
			enemyFail = false;
			// atakowanie = false;
			for (Dice dice : heroDices) {
				dice.addAction(moveTo(dice.getX(), -150, 0.2f, Interpolation.linear));
				dice.setStopklatka(true);
			}
			numberHeroDieces = 0;

			numberEnemyDieces = 2;
			baseEnemyDieces = numberEnemyDieces;
			for (int i = 0; i < enemyDices.size; i++) {
				enemyDices.get(i).setStopklatka(false);
				enemyDices.get(i).setPosition(725 + (i * 133), -100);
				enemyDices.get(i).addAction(moveTo(725 + (i * 133), 5, 1.0f, Interpolation.bounceOut));
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
				atakowanie = false;
			}
		}
		// po 2 sec wylosuj druga kostke
		if (enemyOne == true && enemyTwo == false && enemyTurnTimer >= 2) {
			if (!enemyFail) {
				enemyDices.get(1).setStopklatka(true);
				if (enemyDices.get(1).numerKlatki != 1) {
					pulaPrzeciwnika = pulaPrzeciwnika + enemyDices.get(1).numerKlatki;
					enemyTwo = true;
					// System.out.println("tutaj enemy POWINIEN PODJAC DECYZJE
					// CO DALEJ");
				} else {
					// enemy wylosowal 1
					enemyTwo = true;
					enemyFail = true;
				}
			} else {
				// System.out.println("enemy roll na pierwszej");
				pulaGracza = 0;
				pulaPrzeciwnika = 0;
				atakowanie = false;
				swapTury();
			}
		}
		// po 3 sec swapnij ture (wlasc. zaatakuj)
		if (enemyOne == true && enemyTwo == true && enemyTurnTimer >= 3) {
			if (!enemyFail) {

				// TUTAJ DOPISAC ZE MA MIEC WYBOR MIEDZY DALSZYM LOSOWANIEM A
				// ATAKIEM
				// losuj czy losowac dalej czy atakowac
				int random = MathUtils.random(0, 1);
				if (random == 0 && game.player.playerHp > pulaPrzeciwnika) {
					// System.out.println("...I LOSUJE DALEJ");
					enemyOne = false;
					enemyTwo = false;
					enemyTurnTimer = 0;
					heroTurn = !heroTurn;
					swapTury();
				} else {
					// System.out.println("...I ATAKUJE!");

					// animacja ataku i powrot do idle

					enemys.peek().getState().setAnimation(0, "atak", false);
					enemys.peek().getState().addAnimation(0, "idle", true, 0);
					enemys.peek()
							.addAction(sequence(
									moveTo(enemys.peek().getX() - 1000, enemys.peek().getY(), 0.5f,
											Interpolation.circle),
									moveTo(1100, enemys.peek().getY(), 0.5f, Interpolation.fade)));

					// odejmij punkty graczowi (narazie tylko pierwszemu)
					game.player.playerHp = game.player.playerHp - pulaPrzeciwnika;

					playerIcon.setHp(game.player.playerHp);
					pulaGracza = 0;
					pulaPrzeciwnika = 0;
					swapTury();
				}
			} else {
				// spali jednak i swapnij
				// System.out.println("enemy roll na drugiej");
				enemyFail = true;
			}
		}
		// po 4 sec swapnij ture jesli enemy wylosowal na drugiej roll
		if (enemyOne == true && enemyTwo == true && enemyFail == true && enemyTurnTimer >= 4) {
			// System.out.println("koniec cyklu generowania enemy");
			pulaGracza = 0;
			pulaPrzeciwnika = 0;
			swapTury();
		}
	}

	private void checkHeroDices() {
		// jesli liczba wolnych niestopklatkowych kostek gracza rowna sie zero
		// to przeladuj je
		if (numberHeroDieces <= 0 && !atakowanie) {
			delayTimer += Gdx.graphics.getDeltaTime();
			if (delayTimer >= delay) {
				for (Dice dice : heroDices) {
					dice.addAction(sequence(moveTo(dice.getX(), -150, 0.2f, Interpolation.linear),
							moveTo(dice.getX(), 5, 1.0f, Interpolation.bounceOut)));
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
				for (Dice dice : heroDices) {
					dice.addAction(moveTo(dice.getX(), -150, 0.2f, Interpolation.linear));
					dice.setStopklatka(true);
				}
				swapTury();
			}
		}

		// jesli gracz wylosowal na ktorejkolwiek kostce 1 ale nie pierwszy raz
		if (numberHeroDieces == 1 && rolled)

		{
			// System.out.println("ROLLED2");
			delayTimer += Gdx.graphics.getDeltaTime();
			if (delayTimer >= delay) {
				for (Dice dice : heroDices) {
					dice.addAction(moveTo(dice.getX(), -150, 0.2f, Interpolation.linear));
					dice.setStopklatka(true);
				}
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
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			// camera.zoom += 0.02;
			kotaraState.setAnimation(0, "close", false);
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

}
