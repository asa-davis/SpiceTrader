package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public abstract class Ship extends Entity {

	private SpiceTraderMap map;
	private float maxSpeed;
	private float accel;
	private float rotationSpeed;
	private float decel;
	private float direction;
	private float currSpeed;
	private boolean inReverse;
	//health of ship
	private int hull;
	//number of frames until sprite color goes back to normal
	private int strikeCooldown;
	
	public Ship(Vector2 pos, Sprite sprite, SpiceTraderMap map, float maxSpeed, float accel, float rotationSpeed, float direction) {
		super(pos, sprite);
		this.map = map;
		this.maxSpeed = maxSpeed;
		this.accel = accel;
		this.rotationSpeed = rotationSpeed;
		this.direction = direction;
		this.getHitbox().setRotation(direction);
		this.getSprite().setRotation(direction);
		
		hull = 10;
		strikeCooldown = 0;
		currSpeed = 0;
		inReverse = false;
		decel = 0.01f;
	}
	
	public void tick() {
		//handle acceleration behavior
		this.move();
		if(currSpeed > 0) {
			currSpeed -= decel;
			if(currSpeed <= 0) {
				currSpeed = 0;
				inReverse = false;
			}
		}
		
		//handle red shading on strike
		if(this.strikeCooldown > 0) {
			this.strikeCooldown--;
			if(this.strikeCooldown > 0)
				this.getSprite().setColor(Color.RED);
			else {
				this.getSprite().setColor(Color.WHITE);
				if(this.hull <= 0)
					this.exists = false;
			}
		}
	}
	
	public void accelForward() {
		if(!inReverse) {
			if (currSpeed < maxSpeed) currSpeed += accel;
		}
		else {
			if(currSpeed > 0) currSpeed -= accel;
			else inReverse = false;
		}
	}
	
	public void accelBackward() {
		if(inReverse) {
			if (currSpeed < maxSpeed) currSpeed += accel;
		}
		else {

			if(currSpeed > 0) currSpeed -= accel;
			else inReverse = true;
		}
	}
	
	public void move() {
		float xMove;
		float yMove;
		if(!inReverse) {
			xMove = -1 * (float) Math.sin(0.0175 * direction);
			yMove = (float) Math.cos(0.0175 * direction);
		} else {
			xMove = (float) Math.sin(0.0175 * direction);
			yMove = -1 * (float) Math.cos(0.0175 * direction);
		}
		this.updatePosition(xMove * currSpeed, yMove * currSpeed);
		
		//collision detection - undo move if hitting map 
		int numBacktracks = 4;
		float backtrackAmount = currSpeed / 4f; 
		while(!map.validShipPosition(this) && numBacktracks >= 0) {
			currSpeed = 0;
			this.updatePosition(-1 * backtrackAmount * xMove, -1 * backtrackAmount * yMove);
			numBacktracks--;
		}
	}
	
	public void turnRight() {
		if(!inReverse)
			this.updateRotation(-1 * rotationSpeed);
		else
			this.updateRotation(rotationSpeed);
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (rotationSpeed + 1);
		while(!map.validShipPosition(this) && numMoves >= 0) {
			this.updateRotation(1);
			numMoves--;
		}
	}
	
	public void turnLeft() {
		if(!inReverse)
			this.updateRotation(rotationSpeed);
		else
			this.updateRotation(-1 * rotationSpeed);
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (rotationSpeed + 1);
		while(!map.validShipPosition(this) && numMoves >= 0) {
			this.updateRotation(-1);
			numMoves--;
		}
	}
	
	public void strike() {
		this.hull -= 2;
		this.strikeCooldown = 10;
	}
	
	private void updateRotation(float turnAmount) {
		direction = direction + turnAmount;
		
		this.getHitbox().setRotation(direction);
		this.getSprite().setRotation(direction);
	}
	
	public static Polygon getShipHitbox(float spriteWidth, float spriteHeight, int xOffset) {
		Polygon hitbox = new Polygon(new float[] {xOffset, 0, spriteWidth - xOffset, 0, spriteWidth - xOffset, spriteHeight - 4, spriteWidth/2, spriteHeight, xOffset, spriteHeight - 4});
		hitbox.setOrigin(spriteWidth/2, spriteHeight/2);
		return hitbox;
	}
	
	@Override
	public void setSprite(Sprite sprite) {
		super.setSprite(sprite);
		this.getSprite().setRotation(direction);
	}
	
	public float getDirection() {
		return direction;
	}
	
	public SpiceTraderMap getMap() {
		return map;
	}
	
	public float getCurrSpeed() {
		return currSpeed;
	}
 }
