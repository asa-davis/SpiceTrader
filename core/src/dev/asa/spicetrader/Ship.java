package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public abstract class Ship extends Entity {

	private SpiceTraderMap map;
	private float direction;
	private float speed;
	private float rotationSpeed;
	//health of ship
	private int hull;
	//number of frames until sprite color goes back to normal
	private int strikeCooldown;
	
	public Ship(Vector2 pos, Sprite sprite, SpiceTraderMap map, float speed, float rotationSpeed, float initialDirection) {
		super(pos, sprite);
		this.map = map;
		this.speed = speed;
		this.rotationSpeed = rotationSpeed;
		this.direction = initialDirection;
		this.getHitbox().setRotation(direction);
		this.getSprite().setRotation(direction);
		
		this.hull = 10;
		this.strikeCooldown = 0;
	}
	
	public void tick() {
		if(this.strikeCooldown > 0) {
			this.strikeCooldown--;
			if(this.strikeCooldown > 0)
				this.getSprite().setColor(Color.RED);
			else
				this.getSprite().setColor(Color.WHITE);
		}
		if(this.hull == 0)
			this.exists = false;
	}
	
	public void strike() {
		this.hull -= 2;
		this.strikeCooldown = 10;
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
	
	public static Polygon getShipHitbox(float spriteWidth, float spriteHeight, int xOffset) {
		Polygon hitbox = new Polygon(new float[] {xOffset, 0, spriteWidth - xOffset, 0, spriteWidth - xOffset, spriteHeight - 4, spriteWidth/2, spriteHeight, xOffset, spriteHeight - 4});
		hitbox.setOrigin(spriteWidth/2, spriteHeight/2);
		return hitbox;
	}
	
	@Override
	public void setSprite(Sprite sprite) {
		super.setSprite(sprite);
		this.getSprite().setRotation(this.direction);
	}
	
	public float getDirection() {
		return this.direction;
	}
 }
