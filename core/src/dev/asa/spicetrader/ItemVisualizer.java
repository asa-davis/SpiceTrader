package dev.asa.spicetrader;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

//this class handles drawing items for inventory, trading, and other menus. it also draws a name tag with their stats when moused over.
//this class supports a batch draw where you can add items first one at a time and then draw them after.
//this is helpful to make sure that the item nametag is displayed on top of everything else
public class ItemVisualizer {
	private Vector2 mousePos;
	private BitmapFont font;
	private List<ItemDrawing> drawBatch;
	private Item itemHovered;
	private GlyphLayout layout;
	private AtlasRegion nameTagEdgeTexture;
	private AtlasRegion nameTagMidTexture;
	
	public ItemVisualizer(BitmapFont font, AtlasRegion nameTagEdgeTexture, AtlasRegion nameTagMidTexture) {
		this.font = font;
		this.nameTagEdgeTexture = nameTagEdgeTexture;
		this.nameTagMidTexture = nameTagMidTexture;
		drawBatch = new ArrayList<ItemDrawing>();
		layout = new GlyphLayout();
		
	}
	
	public void passMouse(Vector2 mousePos) {
		this.mousePos = mousePos;
		itemHovered = null;
	}
	
	public void clearBatch() {
		drawBatch.clear();
	}
	
	public void addToBatch(Item item, Vector2 pos) {
		drawBatch.add(new ItemDrawing(item, pos));
	}
	
	public void drawBatch(SpriteBatch batch) {
		for(ItemDrawing item : drawBatch)
			item.draw(batch);
		
		if(itemHovered != null) 
			drawNameTag(batch);
	}
	
	public void drawItem(SpriteBatch batch, Item item, Vector2 pos) {
		batch.draw(item.getTexture(), pos.x, pos.y);
		
		Rectangle itemArea = new Rectangle(pos.x, pos.y, item.getTexture().getRegionWidth(), item.getTexture().getRegionHeight());
		if(itemArea.contains(mousePos)) {
			itemHovered = item;
		}
	}
	
	public void drawNameTag(SpriteBatch batch) {
		Vector2 textPos = new Vector2(mousePos.x + 16, mousePos.y + 10);
		
		layout.setText(font, itemHovered.getName());
		int spaceToFill = (int) layout.width;
		Vector2 tagPos = new Vector2(textPos.x - 6, textPos.y - layout.height - 3);
		
		batch.draw(nameTagEdgeTexture, tagPos.x, tagPos.y);
		spaceToFill -= 5;
		tagPos.x += 11;
		
		while(spaceToFill > 0) {
			batch.draw(nameTagMidTexture, tagPos.x, tagPos.y);
			spaceToFill -= 3;
			tagPos.x += 3;
		}
		
		batch.draw(nameTagEdgeTexture, tagPos.x - 10, tagPos.y);
		
		font.setColor(Color.WHITE);
		font.draw(batch, itemHovered.getName(), textPos.x, textPos.y);
	}
	
	private class ItemDrawing {
		private Item item;
		private Vector2 pos;
		
		public ItemDrawing(Item item, Vector2 pos) {
			this.item = item;
			this.pos = pos;
		}
		
		public void draw(SpriteBatch batch) {
			drawItem(batch, item, pos);
		}
	}
}
