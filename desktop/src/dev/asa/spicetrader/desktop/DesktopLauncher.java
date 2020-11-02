package dev.asa.spicetrader.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import dev.asa.spicetrader.MainGame;

public class DesktopLauncher {
	
	static float SCREEN_SIZE_RATIO = 3/4f;
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Spice Trader";
		config.width = (int)(1920 * SCREEN_SIZE_RATIO);
		config.height = (int)(1080 * SCREEN_SIZE_RATIO);
		config.resizable = true;
		
		// Pack all textures into a 'texture atlas'
		TexturePacker.Settings sets = new TexturePacker.Settings();
		sets.pot = true;
		sets.fast = true;
		sets.combineSubdirectories = true;
		sets.paddingX = 1;
		sets.paddingY = 1;
		sets.edgePadding = true;
		TexturePacker.process(sets, "raw_textures", "./assets", "textures");
		
		new LwjglApplication(new MainGame(), config);
	}
}
