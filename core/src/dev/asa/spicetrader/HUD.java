package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;

public class HUD extends Menu {

	public HUD(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture, boolean needsPause) {
		super(manager, pos, backgroundTexture, needsPause);
	}

}
