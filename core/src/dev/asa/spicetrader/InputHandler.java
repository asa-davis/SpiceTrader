package dev.asa.spicetrader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import dev.asa.spicetrader.UI.MenuFactory;
import dev.asa.spicetrader.UI.MenuManager;
import dev.asa.spicetrader.entities.Merchant;
import dev.asa.spicetrader.entities.Shop;
import dev.asa.spicetrader.entities.Village;
import dev.asa.spicetrader.entities.CannonBall;
import dev.asa.spicetrader.entities.Player;
import dev.asa.spicetrader.entities.EntityManager;

public class InputHandler {
	
	private Player player;
	private EntityManager entManager;
	private float screenHeight;
	private MenuManager menuManager;
	
	public InputHandler(Player player, EntityManager entManager, MenuManager menuManager, float screenHeight) {
		this.player = player;
		this.entManager = entManager;
		this.screenHeight = screenHeight;
		this.menuManager = menuManager;
	}
	
	public void process(boolean paused) {
		this.handleMouseInput();
		if(!paused) this.handlePlayerControls();
	}
	
	private void handleMouseInput() {
		//mouse input for menuManager - mousePos needs to be inverted in Y axis for some reason
		Vector2 mousePos = new Vector2(Gdx.input.getX(), screenHeight - Gdx.input.getY());
		boolean mouseClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		menuManager.passMouse(mousePos, mouseClicked);
	}
	
	private void handlePlayerControls() {
		//docking
		if(player.getDockable() != null && Gdx.input.isKeyPressed(Input.Keys.F)) {
			if(player.getDockable() instanceof Village)
				menuManager.openMenu(MenuFactory.createMenu(menuManager, "VillageMenu"));
			else if(player.getDockable() instanceof Merchant)
				menuManager.openMenu(MenuFactory.createMenu(menuManager, "MerchantMenu"));
			else if(player.getDockable() instanceof Shop)
				menuManager.openMenu(MenuFactory.createMenu(menuManager, "ShopMenu"));
		} 
		
		//movement
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			player.accelForward();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			player.accelBackward();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.turnRight();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			player.turnLeft();
		}
		
		//shooting
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			AudioManager.getInstance().cannon(player.getCannonBalls() > 0);
			CannonBall c = player.fireCannonLeft();
			if(c != null)
				entManager.add(c);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			AudioManager.getInstance().cannon(player.getCannonBalls() > 0);
			CannonBall c = player.fireCannonRight();
			if(c != null)
				entManager.add(c);
		}
	}
}
