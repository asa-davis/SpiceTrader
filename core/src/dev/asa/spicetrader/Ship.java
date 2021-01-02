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
	private boolean dead;
	//number of frames until sprite color goes back to normal
	private int strikeCooldown;
	
	private float xMove;
	private float yMove;
	
	public Ship(Vector2 pos, Sprite sprite, SpiceTraderMap map, float maxSpeed, float accel, float rotationSpeed, float direction, int hull) {
		super(pos, sprite);
		this.map = map;
		this.maxSpeed = maxSpeed;
		this.accel = accel;
		this.rotationSpeed = rotationSpeed;
		this.direction = direction;
		this.hull = hull;
		getHitbox().setRotation(direction);
		getSprite().setRotation(direction);
		
		strikeCooldown = 0;
		currSpeed = 0;
		inReverse = false;
		decel = 0.01f;
		dead = false;
	}
	
	public void tick() {
		//handle acceleration behavior
		move();
		if(currSpeed > 0) {
			currSpeed -= decel;
			if(currSpeed <= 0) {
				currSpeed = 0;
				inReverse = false;
			}
		}
		
		//handle red shading on strike
		if(strikeCooldown > 0) {
			strikeCooldown--;
			if(strikeCooldown > 0)
				getSprite().setColor(Color.RED);
			else {
				getSprite().setColor(Color.WHITE);
				if(hull <= 0 && getClass() == Pirate.class)
					exists = false;
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
	
	private void move() {
		if(!inReverse) {
			xMove = -1 * (float) Math.sin(0.0175 * direction);
			yMove = (float) Math.cos(0.0175 * direction);
		} else {
			xMove = (float) Math.sin(0.0175 * direction);
			yMove = -1 * (float) Math.cos(0.0175 * direction);
		}
		updatePosition(xMove * currSpeed, yMove * currSpeed);
		
		//collision detection - for collisions with other entities: 
		//pirates are kept from overlapping player with bounce back. 
		//ships are kept from overlapping map hitbox with pixel perfect retracement step.
		//pirates are kept from overlapping with other pirates too much by avoidance ai
		int numBacktracks = 4;
		float backtrackAmount = currSpeed / 4f; 
		while(!map.validShipPosition(this) && numBacktracks >= 0) {
			currSpeed = 0;
			updatePosition(-1 * backtrackAmount * xMove, -1 * backtrackAmount * yMove);
			numBacktracks--;
		}
	}
	
	public void turnRight() {
		if(!inReverse) 
			direction -= rotationSpeed;
		else 
			direction += rotationSpeed;
		
		updateRotation();
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (rotationSpeed + 1);
		while(!map.validShipPosition(this) && numMoves >= 0) {
			if(!inReverse) 
				direction += 1;
			else 
				direction -= 1;
			updateRotation();
			numMoves--;
		}
	}
	
	public void turnLeft() {
		if(!inReverse)
			direction += rotationSpeed;
		else
			direction -= rotationSpeed;
		
		updateRotation();
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (rotationSpeed + 1);
		while(!map.validShipPosition(this) && numMoves >= 0) {
			if(!inReverse) 
				direction -= 1;
			else 
				direction += 1;
			updateRotation();
			numMoves--;
		}
	}
	
	public void strike(int damage) {
		hull -= damage;
		if(hull <= 0)
			dead = true;
		strikeCooldown = 10;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public boolean isInReverse() {
		return inReverse;
	}
	
	private void updateRotation() {
		//maintain range 0 <= d <= 360
		while(direction < 0)
			direction += 360;
		while(direction > 360)
			direction -= 360;
		
		//System.out.println("" + direction);
		getHitbox().setRotation(direction);
		getSprite().setRotation(direction);
	}
	
	public void setDirection(float d) {
		//System.out.println("" + d);
		direction = d;
		
		getHitbox().setRotation(d);
		getSprite().setRotation(d);
	}
	
	public static Polygon getShipHitbox(float spriteWidth, float spriteHeight, int xOffset) {
		Polygon hitbox = new Polygon(new float[] {xOffset, 0, spriteWidth - xOffset, 0, spriteWidth - xOffset, spriteHeight - 4, spriteWidth/2, spriteHeight, xOffset, spriteHeight - 4});
		hitbox.setOrigin(spriteWidth/2, spriteHeight/2);
		return hitbox;
	}
	
	@Override
	public void setSprite(Sprite sprite) {
		super.setSprite(sprite);
		getSprite().setRotation(direction);
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
	
	public float getAccel() {
		return accel;
	}
	
	public float getMaxSpeed() {
		return maxSpeed;
	}
	
	public float getRotationSpeed() {
		return rotationSpeed;
	}
	
	public int getCurrHull() {
		return hull;
	}
 }
