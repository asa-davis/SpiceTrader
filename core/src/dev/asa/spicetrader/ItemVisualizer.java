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
		
		if(itemHovered != null) {
			drawItemInfo(batch);
		}
	}

	private void drawItem(SpriteBatch batch, Item item, Vector2 pos) {
		batch.draw(item.getTexture(), pos.x, pos.y);

		Rectangle itemArea = new Rectangle(pos.x, pos.y, item.getTexture().getRegionWidth(), item.getTexture().getRegionHeight());
		if(itemArea.contains(mousePos)) {
			itemHovered = item;
		}
	}

	private void drawItemInfo(SpriteBatch batch) {
		Vector2 tagPos = new Vector2(mousePos.x + 10, mousePos.y + 7 - layout.height);

		if(itemHovered instanceof EquipableItem) {
			ArrayList<String> textLines = new ArrayList<>();
			textLines.add(itemHovered.getName());
			textLines.add("");
			textLines.addAll(((EquipableItem) itemHovered).getStats().toStringList());

			int width = longestStringWidth(textLines);
			int i = 0;
			for(String str : textLines) {
				drawTextRow(batch, str, tagPos, width);
				tagPos.y -= 16;
				i++;
			}
		}
		else {
			layout.setText(font, itemHovered.getName());
			int width = (int) layout.width;
			drawTextRow(batch, itemHovered.getName(), tagPos, width);
		}
	}

	//draws a string in a line with the tag background (height = 16)
	private void drawTextRow(SpriteBatch batch, String text, Vector2 pos, int width) {
		Vector2 posCopy = new Vector2(pos);
		Vector2 textPos = new Vector2(posCopy.x + 6, posCopy.y + 3 + layout.height);

		batch.draw(nameTagEdgeTexture, posCopy.x, posCopy.y);
		width -= 5;
		posCopy.x += 11;

		while(width > 0) {
			batch.draw(nameTagMidTexture, posCopy.x, posCopy.y);
			width -= 3;
			posCopy.x += 3;
		}

		batch.draw(nameTagEdgeTexture, posCopy.x - 10, posCopy.y);

		font.setColor(Color.WHITE);
		font.draw(batch, text, textPos.x, textPos.y);
	}

	private int longestStringWidth(ArrayList<String> stringList) {
		int longestLength = 0;
		String longestString = "";
		for(String str : stringList) {
			if(str.length() > longestLength) {
				longestLength = str.length();
				longestString = str;
			}
		}
		layout.setText(font, longestString);
		return (int) layout.width;
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
