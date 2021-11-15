package dev.asa.spicetrader.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import dev.asa.spicetrader.map.SpiceTraderMap;

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
			if(strikeCooldown == 0) {
				if(hull <= 0) {
					exists = false;
				}
			}
		}
		else
			getSprite().setColor(Color.WHITE);
	}
	
	public void strike(int damage) {
		hull -= damage;
		strikeCooldown = 10;
		getSprite().setColor(Color.RED);
	}
	
	public boolean isDead() {
		return dead;
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
		Vector2 moveVector;
		
		if(!inReverse) {
			moveVector = getMoveVector(direction);
		} else {
			moveVector = getMoveVector(direction + 180);
		}
		
		updatePosition(moveVector.x * currSpeed, moveVector.y * currSpeed);
		
		//collision detection - for collisions with other entities: 
		//pirates are kept from overlapping player with bounce back. 
		//ships are kept from overlapping map hitbox with pixel perfect retracement step.
		//pirates are kept from overlapping with other pirates too much by avoidance ai

		if(!map.validShipPosition(this)) {
			currSpeed = 0;
			handleMapCollisionSticky(moveVector);
		}
	}
	
	//slippery approach: ship will move away from the wall perpendicularly until no longer colliding. Note: current approach must be revised when new map tile hitboxes are added.
	private void handleMapCollisionSlippery() {
		//try all 4 directions
		List<Vector2> vectorsToTry = new ArrayList<Vector2>();
		vectorsToTry.add(new Vector2(0, 0));
		//cardinal directions
		vectorsToTry.add(new Vector2(1, 0));
		vectorsToTry.add(new Vector2(0, 1));
		vectorsToTry.add(new Vector2(-1, 0));
		vectorsToTry.add(new Vector2(0, -1));
		//diagonals, in case trapped in corner?
		vectorsToTry.add(new Vector2(0.70710678118f, 0.70710678118f));
		vectorsToTry.add(new Vector2(0.70710678118f, -0.70710678118f));
		vectorsToTry.add(new Vector2(-0.70710678118f, 0.70710678118f));
		vectorsToTry.add(new Vector2(-0.70710678118f, -0.70710678118f));
		
		int i = 0;
		while(!map.validShipPosition(this)) {
			updatePosition(-1 * vectorsToTry.get(i).x * maxSpeed, -1 * vectorsToTry.get(i).y * maxSpeed);
			i++;
			updatePosition(vectorsToTry.get(i).x * maxSpeed, vectorsToTry.get(i).y * maxSpeed);
		}
		
		Vector2 awayFromWall = vectorsToTry.get(i);
		
		//once we find correct direction away from wall, we move back to original position and slowly backtrack to get pixel perfect collision
		updatePosition(-1 * maxSpeed * awayFromWall.x, -1 * maxSpeed * awayFromWall.y);
		int numBacktracks = 32;
		float backtrackAmount = maxSpeed / numBacktracks; 
		while(!map.validShipPosition(this) && numBacktracks >= 0) {
			updatePosition(backtrackAmount * awayFromWall.x, backtrackAmount * awayFromWall.y);
			numBacktracks--;
		}
	}
	
	//stick approach: ship will move backwards on it's moveVector until no longer colliding. Results in ships sticking to walls.
	private void handleMapCollisionSticky(Vector2 moveVector) {
		float numBacktracks = 32;
		float backtrackAmount = maxSpeed / numBacktracks; 
		while(!map.validShipPosition(this) && numBacktracks >= 0) {
			updatePosition(-1 * backtrackAmount * moveVector.x, -1 * backtrackAmount * moveVector.y);
			numBacktracks--;
		}
	}
	
	//takes a direction in degrees and returns a normalized movement vector
	private Vector2 getMoveVector(float direction) {
		Vector2 moveVector = new Vector2();
		
		moveVector.x = -1 * (float) Math.sin(0.0175 * direction);
		moveVector.y = (float) Math.cos(0.0175 * direction);
		
		return moveVector;
	}
	
	public void turnRight() {
		if(!inReverse) 
			direction -= rotationSpeed;
		else 
			direction += rotationSpeed;
		
		updateRotation();
		
		if(!map.validShipPosition(this))
			handleMapCollisionSlippery();
	}
	
	public void turnLeft() {
		if(!inReverse)
			direction += rotationSpeed;
		else
			direction -= rotationSpeed;
		
		updateRotation();
		
		if(!map.validShipPosition(this))
			handleMapCollisionSlippery();
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

	public void setAccel(float accel) { this.accel = accel; }
	
	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }
	
	public float getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(float rotationSpeed) { this.rotationSpeed = rotationSpeed; }
	
	public int getCurrHull() {
		return hull;
	}

	public void repairHull() { hull++; }
 }
