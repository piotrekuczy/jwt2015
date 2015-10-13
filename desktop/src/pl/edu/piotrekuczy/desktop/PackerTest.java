package pl.edu.piotrekuczy.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;


public class PackerTest {
	public static void main(String[] args) {
		Settings tpSettings = new Settings();
		tpSettings.maxWidth = 1024;
		tpSettings.maxHeight = 1024;
		
//	DICE
		TexturePacker
				.process(
						tpSettings,
						"//Users/piotrek/Documents/dev/libgdx/jwt2015_assets/pregfx/rend/",
						"/Users/piotrek/Documents/dev/libgdx/jwt2015/android/assets/bitmaps",
						"diceStandard.atlas");

	}
}