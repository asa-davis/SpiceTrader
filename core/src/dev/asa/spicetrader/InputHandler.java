package dev.asa.spicetrader;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

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
			menuManager.showDockedMenu(player.getDockable());
		} 
		
		//movement
		boolean forward = false;
		boolean backward = false;
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			forward = true;
			player.moveForward();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			backward = true;
			player.moveBackward();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			if(backward) player.turnCCW();
			else player.turnCW();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			if(backward) player.turnCW();
			else player.turnCCW();
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
