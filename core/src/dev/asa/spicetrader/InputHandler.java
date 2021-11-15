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
		Vector2 mousePos = new Vector2(Gdx.input.getX(), screenHeight - Gdx.input.getY());
		boolean mouseClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

		boolean menuHovered = handleUIControls(mousePos, mouseClicked);
		if(!paused) handleMovementControls();
		if(!paused && !menuHovered) handleShootingControls(mousePos, mouseClicked);
	}

	private boolean handleUIControls(Vector2 mousePos, boolean mouseClicked) {
		boolean menuHovered = menuManager.passMouse(mousePos, mouseClicked);

		//docking
		if(player.getDockable() != null && Gdx.input.isKeyPressed(Input.Keys.F)) {
			if(player.getDockable() instanceof Village)
				menuManager.openMenu(MenuFactory.createMenu(menuManager, "VillageMenu"));
			else if(player.getDockable() instanceof Merchant)
				menuManager.openMenu(MenuFactory.createMenu(menuManager, "MerchantMenu"));
			else if(player.getDockable() instanceof Shop)
				menuManager.openMenu(MenuFactory.createMenu(menuManager, "ShopMenu"));
		}

		return menuHovered;
	}

	private void handleMovementControls() {
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
	}

	private void handleShootingControls(Vector2 mousePos, boolean mouseClicked) {
		if(!mouseClicked)
			return;

		//shooting - todo: don't shoot if mouse is over a menu or a menu is open
		Vector2 mousePosWorld = entManager.screenPosToMapPos(mousePos);
		Vector2 mousePosRelativeToPlayer = mousePosWorld.sub(player.getHitCenter());
		mousePosRelativeToPlayer.y *= -1;
		float angle = mousePosRelativeToPlayer.angleDeg(player.getHitCenter());
		angle -= 45;
		if(angle < 0) angle += 360;
		CannonBall c = player.fireCannon(angle);;
		if(c != null)
			entManager.add(c);
	}

	//deprecated
	private void handleShootingControlsOld() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			CannonBall c = player.fireCannonLeft();
			if(c != null)
				entManager.add(c);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			CannonBall c = player.fireCannonRight();
			if(c != null)
				entManager.add(c);
		}
	}
}
