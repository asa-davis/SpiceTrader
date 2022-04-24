package dev.asa.spicetrader.UI;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import dev.asa.spicetrader.AudioManager;
import dev.asa.spicetrader.MainGame;
import dev.asa.spicetrader.entities.Player;

//this class manages resources like textures and screen size that menus and menu factory need
//it also takes care of running the menus each tick

public class MenuManager {
	private MainGame game;
	private Matrix4 screenMatrix;
	private List<Menu> activeMenus;
	private List<Menu> menusToClose;
	private List<Menu> menusToOpen;
	private BitmapFont[] fonts;
	private Player player;
	private Vector2 screenSize;
	private TextureAtlas atlas;
	private ShipMenu shipMenu;
	
	//menus need access to these
	
	public MenuManager(TextureAtlas atlas, Vector2 screenSize, MainGame game, BitmapFont[] fonts, Player player) {
		this.game = game;
		this.fonts = fonts;
		this.screenSize = screenSize;
		this.atlas = atlas;
		this.player = player;
		
		screenMatrix = new Matrix4(new Matrix4().setToOrtho2D(0, 0, screenSize.x, screenSize.y));
		activeMenus = new ArrayList<Menu>();
		menusToClose = new ArrayList<Menu>();
		menusToOpen = new ArrayList<Menu>();
		
		activeMenus.add(MenuFactory.createMenu(this, "HUDMenu"));
		this.shipMenu = (ShipMenu) MenuFactory.createMenu(this, "ShipMenu");
	}
	
	//handle closing of menus and pausing/resuming of game
	public void tick() {
		//close and open menus - this needs to happen only here to avoid concurrent modification
		for(Menu m : menusToClose) {
			activeMenus.remove(m);
		}
		menusToClose.clear();
		for(Menu m : menusToOpen) {
			activeMenus.add(m);
		}
		menusToOpen.clear();
		
		if(gameShouldPause())
			game.pause();
		else
			game.resume();
	}
	
	public void openMenu(Menu m) {
		if(m instanceof TradeMenu)
			AudioManager.getInstance().enterVillage();

		menusToOpen.add(m);
	}

	public boolean isShipMenuOpen() {
		return activeMenus.contains(shipMenu);
	}
	
	public void closeMenu(Menu m) {
		if(m instanceof TradeMenu)
			AudioManager.getInstance().leaveVillage();

		menusToClose.add(m);
	}
	
	private boolean gameShouldPause() {
		//if an active menu should have game paused, do so, otherwise resume
		for(Menu m : activeMenus) {
			if(m.needsPause) 
				return true;
		}
		return false;
	}
	
	public void draw(SpriteBatch batch) {
		batch.setProjectionMatrix(screenMatrix);
		batch.begin();
		drawDockingPrompt(batch);
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
	
	public void toggleShipMenu() {
		if(activeMenus.contains(shipMenu))
			closeMenu(shipMenu);
		else
			openMenu(shipMenu);
	}

	public void openShipMenu() {
		if(!activeMenus.contains(shipMenu))
			openMenu(shipMenu);
	}

	public void closeShipMenu() {
		if(activeMenus.contains(shipMenu))
			closeMenu(shipMenu);
	}

	public MainGame getGame() {
		return game;
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
	}

	public BitmapFont getFont(int i) {
		return fonts[i];
	}

	public Vector2 getScreenSize() {
		return screenSize;
	}

	public Player getPlayer() {
		return player;
	}
}
