package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Item {
	
	private String name;
	private Vector2 pos;
	private AtlasRegion texture;
	private boolean equipable;
	private int[] stats;
	
	//for drawing name tags
	private Rectangle clickArea;
	private Vector2 mousePos;
	
	public Item(String name, Vector2 pos, AtlasRegion texture) {
		this.name = name;
		this.pos = pos;
		this.texture = texture;
		
		mousePos = new Vector2(0,0);
		clickArea = new Rectangle(pos.x, pos.y, pos.x + texture.getRegionWidth(), pos.y + texture.getRegionHeight());
		equipable = false;
	}

	public Item(String name, Vector2 pos, AtlasRegion texture, int[] stats) {
		this(name, pos, texture);
		
		this.stats = stats;
		equipable = true;
	}
	
	public void draw(SpriteBatch batch) {
		batch.draw(texture, pos.x, pos.y);
		
		//TODO: code here to draw name tag which contains stats if equipable
		if(clickArea.contains(mousePos)) {
			
		}
	}
	
	public void passMouse(Vector2 mousePos) {
		this.mousePos = mousePos;
	}
	
}
