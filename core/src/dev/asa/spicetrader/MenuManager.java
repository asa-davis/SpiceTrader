package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

//this will help us 
public class MenuManager {
	MainGame game;
	Matrix4 screenMatrix;
	List<Menu> activeMenus;
	List<Menu> menusToClose;
	BoardedMenu boardedMenu;
	BitmapFont[] fonts;
	Player player;
	Vector2 screenSize;
	TextureAtlas atlas;
	
	public MenuManager(TextureAtlas atlas, Vector2 screenSize, MainGame game, BitmapFont[] fonts) {
		this.game = game;
		this.fonts = fonts;
		this.screenSize = screenSize;
		this.atlas = atlas;
		
		screenMatrix = new Matrix4(new Matrix4().setToOrtho2D(0, 0, screenSize.x, screenSize.y));
		activeMenus = new ArrayList<Menu>();
		menusToClose = new ArrayList<Menu>();
	}
	
	//handle closing of menus and pausing/resuming of game
	public void tick() {
		//clear menus to close
		for(Menu m : menusToClose) {
			activeMenus.remove(m);
		}
		menusToClose.clear();
		
		if(this.gameShouldPause())
			game.pause();
		else
			game.resume();
	}
	
	private boolean gameShouldPause() {
		//if an active menu should have game paused, do so, otherwise resume
		for(Menu m : activeMenus) {
			if(m.needsPause) 
				return true;
		}
		return false;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void showBoardedMenu() {
		activeMenus.add(new BoardedMenu(this, screenSize, atlas, fonts, game));
	}
	
	public void showDockedMenu(Village v) {
		activeMenus.add(new DockedMenu(this, screenSize, atlas, fonts, v));
	}
	
	public void closeMenu(Menu m) {
		menusToClose.add(m);
	}
	
	public void draw(SpriteBatch batch) {
		batch.setProjectionMatrix(screenMatrix);
		batch.begin();
		this.drawDockingPrompt(batch);
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

	private void drawDockingPrompt(SpriteBatch batch) {
		if(player.getDockable() != null) {
			fonts[0].setColor(Color.WHITE);
			fonts[0].draw(batch, "Press f to dock at " + player.getDockable().getName(), 0, 32, screenSize.x, Align.center, false);
		}
	}
}
