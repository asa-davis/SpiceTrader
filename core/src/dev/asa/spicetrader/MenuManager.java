package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//this will help us 
public class MenuManager {
	MainGame game;
	Matrix4 screenMatrix;
	List<Menu> activeMenus;
	BoardedMenu boardedMenu;
	
	public MenuManager(TextureAtlas atlas, Vector2 screenSize, MainGame game, BitmapFont[] fonts) {
		this.game = game;
		
		screenMatrix = new Matrix4();
		screenMatrix = new Matrix4(screenMatrix.setToOrtho2D(0, 0, screenSize.x, screenSize.y));
		
		activeMenus = new ArrayList<Menu>();
		boardedMenu = this.makeBoardedMenu(atlas, screenSize, fonts);
	}
	
	private BoardedMenu makeBoardedMenu(TextureAtlas atlas, Vector2 screenSize, BitmapFont[] fonts) {
		Array<AtlasRegion> restartButtonTextures = atlas.findRegions("ui/restart_button");
		return new BoardedMenu(screenSize, atlas.findRegion("ui/game_over_menu_background"), fonts, restartButtonTextures, game);
	}
	
	public void showBoardedMenu() {
		activeMenus.add(boardedMenu);
	}
	
	public void draw(SpriteBatch batch) {
		batch.setProjectionMatrix(screenMatrix);
		batch.begin();
		for(Menu m : activeMenus) {
			m.draw(batch);
		}
		batch.end();
	}
	
	public void passMouse(Vector2 mousePos, boolean mouseClicked) {
		for(Menu m : activeMenus) {
			m.passMouse(mousePos, mouseClicked);
		}
	}
}
