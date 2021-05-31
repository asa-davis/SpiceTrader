package dev.asa.spicetrader;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Pirate extends Ship{
	private static int DEFAULT_MAX_SPEED = 2;
	private static int DEFAULT_ACCEL = 4;
	private static int DEFAULT_TURNING = 5;
	private static int DEFAULT_HULL = 3;
	
	//keeps pirate from moving for a few frames after knocking player.
	private int movementCooldown;
	
	//dijkstra map for finding path to player updated by map 
	DijkstraMap pathToPlayer;
	
	//where the pirate is currently headed.
	private Vector2 currGoal;
	
	private PirateVillage base;
	
	public Pirate(Vector2 pos, Sprite sprite, SpiceTraderMap map, float initialDirection, PirateVillage base) {
		super(pos, sprite, map, Utils.statToUse(DEFAULT_MAX_SPEED, 'm'), Utils.statToUse(DEFAULT_ACCEL, 'a'), Utils.statToUse(DEFAULT_TURNING, 't'), initialDirection, DEFAULT_HULL);
		
		this.base = base;
		
		movementCooldown = 0;
		
		pathToPlayer = getMap().getPlayerDijkstraMap();
		
	}

	@Override
	protected void createHitbox() {
		this.setHitbox(Ship.getShipHitbox(this.getWidth(), this.getHeight(), 2));
	}

	@Override
	public void tick() {
		super.tick();
		
		//check if in chase range of player
		if(pathToPlayer.inRange(getHitCenter())) {
			//get next move towards player
			currGoal = pathToPlayer.getNextMove(getHitCenter());
			
			//check with entity manager that goal isnt shared by other pirates
			currGoal = getManager().avoidOtherPirates(currGoal);
			
			//check if pirate can still move
			if(currGoal != null) {
				if(movementCooldown <= 0) 
					moveTowardsPoint(currGoal);
				else 
					movementCooldown--;
			}
		}
	}
	
	//first version of pirate path following behavior
	//	- If pirate isn't pointing towards point by +- prec degree, then turn towards it
	//	- If pirate is pointing towards point by +- prec degree, accel forward
	private void moveTowardsPoint(Vector2 point) {
		//determines the precision with which a pirate must be pointing towards a point before it can move forward
		int prec = 5;
		//calc directional vector
		Vector2 dVec = new Vector2(point).sub(getHitCenter());
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
		float diff = directionToPoint - getDirection();
		if(Math.abs(diff) > prec && !isInReverse()) {
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
	}
	
	@Override
	public void drawHitbox(ShapeRenderer renderer) {
		super.drawHitbox(renderer);
		this.drawCurrPath(renderer);
	}
	
	private void drawCurrPath(ShapeRenderer renderer) {
		if(currGoal == null)
			return;

		renderer.circle(currGoal.x, currGoal.y, 1);
		renderer.line(getHitCenter(), currGoal);
	}
	
	public void bounceBack() {
		for(int i = 0; i < 40; i++) {
			accelBackward();
		}
		movementCooldown = 15;
	}
	
	public PirateVillage getPirateVillage() {
		return base;
	}
 }
