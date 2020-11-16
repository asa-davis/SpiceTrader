package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public abstract class Menu {
	private Vector2 screenSize;
	private Vector2 pos;
	private AtlasRegion backgroundTexture;
	private BitmapFont[] fonts;
	private List<Button> buttons;
	
	
	public Menu(Vector2 screenSize, AtlasRegion backgroundTexture, BitmapFont[] fonts) {
		this.screenSize = screenSize;
		this.backgroundTexture = backgroundTexture;
		this.fonts = fonts;
		this.buttons = new ArrayList<Button>();
		this.setPos();
	}
	
	//position depends on what kind of menu
	protected abstract void setPos();
	
	public void passMouse(Vector2 mousePos, boolean mouseClicked) {
		for(Button b : buttons) {
			b.passMouse(mousePos, mouseClicked);
		}
	}
	
	public void addButton(Button b) {
		this.buttons.add(b);
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
	
	public void setPos(Vector2 pos) {
		this.pos = pos;
	}
	
	public Vector2 getSize() {
		return new Vector2(backgroundTexture.getRegionWidth(), backgroundTexture.getRegionHeight());
	}
	
	public Vector2 getScreenSize() {
		return screenSize;
	}
	
	public BitmapFont getFont(int i) {
		return fonts[i];
	}
}
