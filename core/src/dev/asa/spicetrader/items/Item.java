package dev.asa.spicetrader.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Item {

	private String name;
	private AtlasRegion texture;
	private int sellPrice;
	private int buyPrice;
	
	public Item(String name, AtlasRegion texture, int sellPrice, int buyPrice) {
		this.name = name;
		this.texture = texture;
		this.sellPrice = sellPrice;
		this.buyPrice = buyPrice;
	}

	public String getName() {
		return name;
	}
	
	public AtlasRegion getTexture() {
		return texture;
	}

	public int getSellPrice() { return sellPrice; }

	public int getBuyPrice() { return buyPrice; }
}
