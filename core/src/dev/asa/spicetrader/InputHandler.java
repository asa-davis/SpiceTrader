package dev.asa.spicetrader;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public class InputHandler {
	
	private Player player;
	private Camera camera;
	private EntityManager entManager;
	private float screenHeight;
	private MenuManager menus;
	private boolean showMapHitboxes;
	
	public InputHandler(Player player, Camera camera, EntityManager entManager, MenuManager menus, float screenHeight, boolean showMapHitboxes) {
		this.player = player;
		this.camera = camera;
		this.entManager = entManager;
		this.screenHeight = screenHeight;
		this.menus = menus;
		this.showMapHitboxes = showMapHitboxes;
	}
	
	public void process(boolean paused) {
		//mouse input for menus - mousePos needs to be inverted in Y axis for some reason
		Vector2 mousePos = new Vector2(Gdx.input.getX(), screenHeight - Gdx.input.getY());
		boolean mouseClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		menus.passMouse(mousePos, mouseClicked);
		
		if(!paused) this.handlePlayerMovement();
		
	}
	
	private void handlePlayerMovement() {
		boolean forward = false;
		boolean backward = false;
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			forward = true;
			Vector2 playerPos = player.moveForward(showMapHitboxes);
			camera.position.x = playerPos.x;
			camera.position.y = playerPos.y;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			backward = true;
			Vector2 playerPos = player.moveBackward(showMapHitboxes);
			camera.position.x = playerPos.x;
			camera.position.y = playerPos.y;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			if(backward) player.turnCCW(showMapHitboxes);
			else player.turnCW(showMapHitboxes);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			if(backward) player.turnCW(showMapHitboxes);
			else player.turnCCW(showMapHitboxes);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			this.entManager.add(player.fireCannonLeft());
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			this.entManager.add(player.fireCannonRight());
		}
	}
}
