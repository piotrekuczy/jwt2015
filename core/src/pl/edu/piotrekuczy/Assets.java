package pl.edu.piotrekuczy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.sun.corba.se.spi.ior.MakeImmutable;


public class Assets {

	public static final AssetManager manager = new AssetManager();
	
	// DICES
	 public static final String diceStandardAtlas = "bitmaps/diceStandard.atlas";
	// FONTS
	 public static final String myFont = "fonts/sunday.fnt";
	// GUI
	 public static final String scena = "bitmaps/scena.png";
	 public static final String panel = "bitmaps/panel.png";
	 public static final String atakuj = "bitmaps/sztandar.png";
	 public static final String swiat02tlo = "swiaty/swiat02tlo.jpg";
	 
	// // BACKGROUND
	// public static final String backgroundChecker =
	// "backgrounds/background.png";
	// // MAPS
	// public static final String level001 = "maps/level001.tmx";
	// public static final String level002 = "maps/level002.tmx";
	// public static final String level003 = "maps/level003.tmx";
	// public static final String level004 = "maps/level004.tmx";
	// public static final String level005 = "maps/level005.tmx";
	// public static final String level006 = "maps/level006.tmx";
	// public static final String level007 = "maps/level007.tmx";
	// public static final String level008 = "maps/level008.tmx";
	// public static final String level009 = "maps/level009.tmx";
	// public static final String level010 = "maps/level0010.tmx";
	// // SPRITES
	// public static final String enemysAtlas = "sprites/enemys.atlas";
	// public static final String towersAtlas = "sprites/towers.atlas";
	// // GUI
	// public static final String uiskinAtlas = "gui/uiskin.atlas";
	// // AUDIO
	// public static final String clickSfx = "audio/click001.wav";
	// public static final String bombSfx = "audio/bomb001.wav";
	// public static final String enemyExplSfx = "audio/enemyExp001.wav";
	// public static final String fireBallSfx = "audio/fireBall001.wav";
	// public static final String hitSfx = "audio/hit001.wav";
	// public static final String iceSfx = "audio/iceSpell001.wav";
	// public static final String shootSfx = "audio/shoot002.wav";
	// public static final String spellDragSfx = "audio/spellDrag001.wav";

	public static void load() {
		
//		System.out.println("asset manager load method");
		
		// DICES
		 manager.load(diceStandardAtlas, TextureAtlas.class);
		 
		// FONTS
		 manager.load(myFont, BitmapFont.class);
		 
		// GUI
		 
		 manager.load(scena, Texture.class);
		 manager.load(panel, Texture.class);
		 manager.load(atakuj, Texture.class);
		 manager.load(swiat02tlo, Texture.class);
		 
		// manager.load(uiskinAtlas, TextureAtlas.class);
		// manager.load(backgroundChecker, Texture.class);
		// manager.load(enemysAtlas, TextureAtlas.class);
		// manager.load(towersAtlas, TextureAtlas.class);
		// manager.setLoader(TiledMap.class, new TmxMapLoader(
		// new InternalFileHandleResolver()));
		// manager.load(level001, TiledMap.class);
		// manager.load(level002, TiledMap.class);
		// manager.load(level003, TiledMap.class);
		// manager.load(level004, TiledMap.class);
		// manager.load(level005, TiledMap.class);
		// manager.load(level006, TiledMap.class);
		// manager.load(level007, TiledMap.class);
		// manager.load(level008, TiledMap.class);
		// manager.load(level009, TiledMap.class);
		// manager.load(level010, TiledMap.class);
		// manager.load(clickSfx, Sound.class);
		// manager.load(bombSfx, Sound.class);
		// manager.load(enemyExplSfx, Sound.class);
		// manager.load(fireBallSfx, Sound.class);
		// manager.load(hitSfx, Sound.class);
		// manager.load(iceSfx, Sound.class);
		// manager.load(shootSfx, Sound.class);
		// manager.load(spellDragSfx, Sound.class);
	}

	public static void dispose() {
		manager.dispose();
	}

	
}
