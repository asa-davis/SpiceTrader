package dev.asa.spicetrader;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Pirate extends Ship{
	private List<Vector2> currPath;
	
	public Pirate(Vector2 pos, Sprite sprite, SpiceTraderMap map, float maxSpeed, float accel, float rotationSpeed, float initialDirection) {
		super(pos, sprite, map, maxSpeed, accel, rotationSpeed, initialDirection);
	}

	@Override
	protected void createHitbox() {
		this.setHitbox(Ship.getShipHitbox(this.getWidth(), this.getHeight(), 2));
	}

	@Override
	public void tick() {
		super.tick();
		
		currPath = this.getMap().getDijkstraMap().getPathToPlayer(this.getHitCenter());
		//System.out.println(currPath.size() + " tiles from player.");
	}
	
	@Override
	public void drawHitbox(ShapeRenderer renderer) {
		super.drawHitbox(renderer);
		this.drawCurrPath(renderer);
	}
	
	private void drawCurrPath(ShapeRenderer renderer) {
		Vector2 currPoint;
		Vector2 nextPoint;
		for(int i = 1; i < currPath.size(); i++) {
			currPoint = currPath.get(i - 1);
			nextPoint = currPath.get(i);
			renderer.line(currPoint, nextPoint);
			currPoint = nextPoint;
		}
	}
 }
