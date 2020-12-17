package dev.asa.spicetrader;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Pirate extends Ship{
	private List<Vector2> currPath;
	private static final float initMaxSpeed = 1.2f;
	private static final float initAccel = 0.05f;
	private static final float initRotationSpeed = 2.5f;
	
	public Pirate(Vector2 pos, Sprite sprite, SpiceTraderMap map, float initialDirection) {
		super(pos, sprite, map, initMaxSpeed, initAccel, initRotationSpeed, initialDirection);
	}

	@Override
	protected void createHitbox() {
		this.setHitbox(Ship.getShipHitbox(this.getWidth(), this.getHeight(), 2));
	}

	@Override
	public void tick() {
		super.tick();
		
		//generate path to player
		currPath = this.getMap().getDijkstraMap().getPathToPlayer(this.getHitCenter());
		
		//move towards next point on path
		if(currPath.size() > 1) {
			Vector2 nextPoint = currPath.get(1);
			this.moveTowardsPoint(nextPoint);
		}
	}
	
	//first version of pirate path following behavior
	//	- If pirate isn't pointing towards point by +- 1 degree, then turn towards it
	//	- If pirate is pointing towards point by +- 1 degree, accel forward
	private void moveTowardsPoint(Vector2 point) {
		//calc directional vector
		Vector2 dVec = new Vector2(point).sub(this.getHitCenter());
		//convert to radians
		float directionToPoint = (float) Math.atan(dVec.y / dVec.x);
		//convert to degrees
		directionToPoint *= 57.2958;
		//correct for weird libgdx directional basis
		directionToPoint -= 90;
		//another correction for negative horizontal component
		if(dVec.x < 0)
			directionToPoint += 180;
		//correct for negative rotation
		if(directionToPoint < 0)
			directionToPoint = 360 + directionToPoint;
		
		//turn to correct direction 
		float diff = directionToPoint - this.getDirection();
		if(Math.abs(diff) > 5) {
			if(diff < 0)
				diff += 360;
			if(diff > 180)
				this.turnRight();
			else
				this.turnLeft();
		}
		//move forward
		else {
			this.accelForward();
		}
		
		//this.setDirection(directionToPoint);
	}
	
	@Override
	public void drawHitbox(ShapeRenderer renderer) {
		super.drawHitbox(renderer);
		this.drawCurrPath(renderer);
	}
	
	private void drawCurrPath(ShapeRenderer renderer) {
		if(currPath.size() > 1)
			renderer.circle(currPath.get(1).x, currPath.get(1).y, 1);
		
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
