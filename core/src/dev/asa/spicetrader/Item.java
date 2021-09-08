package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Item {

	private String name;
	private AtlasRegion texture;
	
	public Item(String name, AtlasRegion texture) {
		this.name = name;
		this.texture = texture;
	}

	public String getName() {
		return name;
	}
	
	public AtlasRegion getTexture() {
		return texture;
	}
}
