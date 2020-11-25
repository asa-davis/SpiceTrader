package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public abstract class Ship extends Entity {

	private SpiceTraderMap map;
	private float direction;
	private float currSpeed;
	private float maxSpeed;
	private float accel;
	private boolean inReverse;
	private float rotationSpeed;
	//health of ship
	private int hull;
	//number of frames until sprite color goes back to normal
	private int strikeCooldown;
	
	public Ship(Vector2 pos, Sprite sprite, SpiceTraderMap map, float maxSpeed, float accel, float rotationSpeed, float initialDirection) {
		super(pos, sprite);
		this.map = map;
		this.maxSpeed = maxSpeed;
		this.accel = accel;
		this.rotationSpeed = rotationSpeed;
		this.direction = initialDirection;
		this.getHitbox().setRotation(direction);
		this.getSprite().setRotation(direction);
		
		this.hull = 10;
		this.strikeCooldown = 0;
		this.currSpeed = maxSpeed;
		this.inReverse = false;
	}
	
	public void tick() {
		//handle acceleration behavior
		//if(!inReverse)
			//this.moveForward(true);
		//else
			//this.moveBackward(true);
		//if(currSpeed > 0)
			//currSpeed -= 0.01;
		
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
	
	public void strike() {
		this.hull -= 2;
		this.strikeCooldown = 10;
	}
	
	public void moveForward() {
		float xMoveInc = -1 * (float) Math.sin(0.0175 * this.direction);
		float yMoveInc = (float) Math.cos(0.0175 * this.direction);
		float xMoveTotal = xMoveInc * currSpeed;
		float yMoveTotal = yMoveInc * currSpeed;
		
		this.updatePosition(xMoveTotal, yMoveTotal);
		
		//collision detection - undo move if hitting map 
		int numBacktracks = 4;
		float backtrackAmount = currSpeed / 4f; 
		while(!map.validShipPosition(this) && numBacktracks >= 0) {
			this.updatePosition(-1 * backtrackAmount * xMoveInc, -1 * backtrackAmount * yMoveInc);
			numBacktracks--;
		}
	}
	
	public void moveBackward() {
		float xMoveInc = (float) Math.sin(0.0175 * direction);
		float yMoveInc = -1 * (float) Math.cos(0.0175 * direction);
		float xMoveTotal = xMoveInc * currSpeed;
		float yMoveTotal = yMoveInc * currSpeed;
		
		this.updatePosition(xMoveTotal, yMoveTotal);
		
		//collision detection - undo move if hitting map 
		int numBacktracks = 4;
		float backtrackAmount = currSpeed / 4f; 
		while(!map.validShipPosition(this) && numBacktracks >= 0) {
			this.updatePosition(-1 * backtrackAmount * xMoveInc, -1 * backtrackAmount * yMoveInc);
			numBacktracks--;
		}
	}
	
	public void turnCW() {
		this.updateRotation(-1 * rotationSpeed);
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (rotationSpeed + 1);
		while(!map.validShipPosition(this) && numMoves >= 0) {
			this.updateRotation(1);
			numMoves--;
		}
	}
	
	public void turnCCW() {
		this.updateRotation(rotationSpeed);
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (rotationSpeed + 1);
		while(!map.validShipPosition(this) && numMoves >= 0) {
			this.updateRotation(-1);
			numMoves--;
		}
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
 }
