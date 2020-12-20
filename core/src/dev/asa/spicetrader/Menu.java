package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Menu {
	public MenuManager manager;
	public boolean needsPause;
	private Vector2 pos;
	private AtlasRegion backgroundTexture;
	private List<Button> buttons;
	
	public Menu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture, boolean needsPause) {
		this.manager = manager;
		this.needsPause = needsPause;
		this.pos = pos;
		this.backgroundTexture = backgroundTexture;
		
		buttons = new ArrayList<Button>();
	}
	
	public void close() {
		manager.closeMenu(this);
	}
	
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
	
	public Vector2 getSize() {
		return new Vector2(backgroundTexture.getRegionWidth(), backgroundTexture.getRegionHeight());
	}
}
