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
	private MenuManager menuManager;
	private boolean showMapHitboxes;
	
	public InputHandler(Player player, Camera camera, EntityManager entManager, MenuManager menuManager, float screenHeight, boolean showMapHitboxes) {
		this.player = player;
		this.camera = camera;
		this.entManager = entManager;
		this.screenHeight = screenHeight;
		this.menuManager = menuManager;
		this.showMapHitboxes = showMapHitboxes;
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
			menuManager.showDockedMenu(player.getDockable());
		} 
		
		//movement
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
		
		//shooting
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			entManager.add(player.fireCannonLeft());
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			entManager.add(player.fireCannonRight());
		}
	}
}
