package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public abstract class Ship extends Entity {

	private SpiceTraderMap map;
	private float direction;
	private float speed;
	private float rotationSpeed;
	
	public Ship(Vector2 pos, Sprite sprite, SpiceTraderMap map, float speed, float rotationSpeed, float initialDirection) {
		super(pos, sprite);
		this.map = map;
		this.speed = speed;
		this.rotationSpeed = rotationSpeed;
		this.direction = initialDirection;
		this.getHitbox().setRotation(direction);
		this.getSprite().setRotation(direction);
	}

	@Override
	void createHitbox() {
		Polygon hitbox = new Polygon(new float[] {2, 0, this.getWidth() - 2, 0, this.getWidth() - 2, this.getHeight() - 4, this.getWidth()/2, this.getHeight(), 2, this.getHeight() - 4});
		hitbox.setOrigin(this.getWidth()/2, this.getHeight()/2);
		this.setHitbox(hitbox);
	}
	
	public Vector2 moveForward() {
		float xMoveInc = -1 * (float) Math.sin(0.0175 * this.direction);
		float yMoveInc = (float) Math.cos(0.0175 * this.direction);
		float xMoveTotal = xMoveInc * this.speed;
		float yMoveTotal = yMoveInc * this.speed;
		
		this.updatePosition(xMoveTotal, yMoveTotal);
		
		//collision detection - undo move if hitting map 
		int numMoves = (int) this.speed + 1;
		while(!this.map.validShipPosition(this) && numMoves >= 0) {
			this.updatePosition(-1 * xMoveInc, -1 * yMoveInc);
			numMoves--;
		}

		return this.getHitCenter();
	}
	
	public Vector2 moveBackward() {
		float xMoveInc = (float) Math.sin(0.0175 * this.direction);
		float yMoveInc = -1 * (float) Math.cos(0.0175 * this.direction);
		float xMoveTotal = xMoveInc * this.speed;
		float yMoveTotal = yMoveInc * this.speed;
		
		this.updatePosition(xMoveTotal, yMoveTotal);
		
		//collision detection - undo move if hitting map 
		int numMoves = (int) this.speed + 1;
		while(!this.map.validShipPosition(this) && numMoves >= 0) {
			this.updatePosition(-1 * xMoveInc, -1 * yMoveInc);
			numMoves--;
		}

		return this.getHitCenter();
	}
	
	public void turnCW() {
		this.updateRotation(-1 * this.rotationSpeed);
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (this.rotationSpeed + 1);
		while(!this.map.validShipPosition(this) && numMoves >= 0) {
			this.updateRotation(1);
			numMoves--;
		}
	}
	
	public void turnCCW() {
		this.updateRotation(this.rotationSpeed);
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (this.rotationSpeed + 1);
		while(!this.map.validShipPosition(this) && numMoves >= 0) {
			this.updateRotation(-1);
			numMoves--;
		}
	}
	
	private void updateRotation(float turnAmount) {
		this.direction = this.direction + turnAmount;
		
		this.getHitbox().setRotation(this.direction);
		this.getSprite().setRotation(this.direction);
	}
	
	@Override
	public void setSprite(Sprite sprite) {
		super.setSprite(sprite);
		this.getSprite().setRotation(this.direction);
	}
	
	public void setMap(SpiceTraderMap map) {
		this.map = map;
	}
	
	public float getDirection() {
		return this.direction;
	}
 }
