package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Ship extends Entity {

	public Ship(SpiceTraderMap map, Vector2 pos, Sprite sprite, float speed, float rotationSpeed, float initialDirection) {
		super(map, pos, sprite, speed, rotationSpeed, initialDirection);
	}

	@Override
	void createHitbox() {
		Polygon hitbox = new Polygon(new float[] {2, 0, this.getWidth() - 2, 0, this.getWidth() - 2, this.getHeight() - 4, this.getWidth()/2, this.getHeight(), 2, this.getHeight() - 4});
		hitbox.setOrigin(this.getWidth()/2, this.getHeight()/2);
		this.setHitbox(hitbox);
	}
	
	public Vector2 moveForward() {
		float xMoveInc = -1 * (float) Math.sin(0.0175 * this.getDirection());
		float yMoveInc = (float) Math.cos(0.0175 * this.getDirection());
		float xMoveTotal = xMoveInc * this.getSpeed();
		float yMoveTotal = yMoveInc * this.getSpeed();
		
		this.updatePosition(xMoveTotal, yMoveTotal);
		
		//collision detection - undo move if hitting map 
		int numMoves = (int) this.getSpeed() + 1;
		while(!this.getMap().validShipPosition(this) && numMoves >= 0) {
			this.updatePosition(-1 * xMoveInc, -1 * yMoveInc);
			numMoves--;
		}

		return this.getHitCenter();
	}
	
	public Vector2 moveBackward() {
		float xMoveInc = (float) Math.sin(0.0175 * this.getDirection());
		float yMoveInc = -1 * (float) Math.cos(0.0175 * this.getDirection());
		float xMoveTotal = xMoveInc * this.getSpeed();
		float yMoveTotal = yMoveInc * this.getSpeed();
		
		this.updatePosition(xMoveTotal, yMoveTotal);
		
		//collision detection - undo move if hitting map 
		int numMoves = (int) this.getSpeed() + 1;
		while(!this.getMap().validShipPosition(this) && numMoves >= 0) {
			this.updatePosition(-1 * xMoveInc, -1 * yMoveInc);
			numMoves--;
		}

		return this.getHitCenter();
	}
	
	public void turnCW() {
		this.updateRotation(-1 * this.getRotationSpeed());
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (this.getRotationSpeed() + 1);
		while(!this.getMap().validShipPosition(this) && numMoves >= 0) {
			this.updateRotation(1);
			numMoves--;
		}
	}
	
	public void turnCCW() {
		this.updateRotation(this.getRotationSpeed());
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (this.getRotationSpeed() + 1);
		while(!this.getMap().validShipPosition(this) && numMoves >= 0) {
			this.updateRotation(-1);
			numMoves--;
		}
	}
}
