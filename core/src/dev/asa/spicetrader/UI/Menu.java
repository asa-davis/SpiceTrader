package dev.asa.spicetrader.UI;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public abstract class Menu {
	public MenuManager manager;
	public boolean needsPause;
	private Vector2 pos;
	private AtlasRegion backgroundTexture;
	private List<Button> buttons;
	private Rectangle hitbox;
	
	public Menu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture, boolean needsPause) {
		this.manager = manager;
		this.needsPause = needsPause;
		this.pos = pos;
		this.backgroundTexture = backgroundTexture;
		hitbox = new Rectangle(pos.x, pos.y, backgroundTexture.getRegionWidth(), backgroundTexture.getRegionHeight());
		
		buttons = new ArrayList<Button>();
	}
	
	public void close() {
		manager.closeMenu(this);
	}

	//returns true if the menu is being hovered over
	public boolean passMouse(Vector2 mousePos, boolean mouseClicked) {
		for(Button b : buttons) {
			b.passMouse(mousePos, mouseClicked);
		}
		return hitbox.contains(mousePos);
	}
	
	public void addButton(Button b) {
		this.buttons.add(b);
	}

	public void addButtons(List<Button> b) {
		this.buttons.addAll(b);
	}
	
	public void draw(SpriteBatch batch) {
		batch.draw(backgroundTexture, pos.x, pos.y);
		for(Button b : buttons) {
			b.draw(batch);
		}
	}
	
	public Vector2 getPos() {
		return pos;
	}
	
	public Vector2 getSize() {
		return new Vector2(backgroundTexture.getRegionWidth(), backgroundTexture.getRegionHeight());
	}

	public void drawTitle(SpriteBatch batch, String title, Color color) {
		manager.getFont(2).setColor(color);
		manager.getFont(2).draw(batch, title, this.getPos().x, this.getPos().y + this.getSize().y - 16, this.getSize().x, Align.center, true);
	}

	public void drawTitle(SpriteBatch batch, String title) {
		drawTitle(batch, title, Color.DARK_GRAY);
	}

	public void drawBody(SpriteBatch batch, String body) {
		manager.getFont(1).setColor(Color.DARK_GRAY);
		manager.getFont(1).draw(batch, body, this.getPos().x + 16, this.getPos().y + this.getSize().y - 64, this.getSize().x - 32, Align.center, true);
	}
}
