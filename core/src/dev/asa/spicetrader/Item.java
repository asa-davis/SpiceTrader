package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Item {
	
	private String name;
	private AtlasRegion texture;
	private boolean equipable;
	private int[] stats;
	
	public Item(String name, AtlasRegion texture) {
		this.name = name;
		this.texture = texture;
		equipable = false;
	}

	public Item(String name, AtlasRegion texture, int[] stats) {
		this(name, texture);
		this.stats = stats;
		equipable = true;
	}

	public String getName() {
		return name;
	}
	
	public AtlasRegion getTexture() {
		return texture;
	}
}
