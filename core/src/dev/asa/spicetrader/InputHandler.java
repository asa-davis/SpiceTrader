package dev.asa.spicetrader;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public class InputHandler {
	
	private Player player;
	private Camera camera;
	private List<Entity> allEntities;
	
	public InputHandler(Player player, Camera camera, List<Entity> allEntities) {
		this.player = player;
		this.camera = camera;
		this.allEntities = allEntities;
	}
	
	public void process() {
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			Vector2 playerPos = this.player.moveForward();
			this.camera.position.x = playerPos.x;
			camera.position.y = playerPos.y;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			Vector2 playerPos = this.player.moveBackward();
			this.camera.position.x = playerPos.x;
			this.camera.position.y = playerPos.y;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			this.player.turnCW();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			this.player.turnCCW();
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			this.allEntities.add(this.player.fireCannonLeft());
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			this.allEntities.add(this.player.fireCannonRight());
		}
	}
}
