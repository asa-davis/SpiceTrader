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
	
	//updated by map when player moves
	private DijkstraMap pathToPlayer;

	//created once by pirate base
	private DijkstraMap pathToSpawn;
	
	//where the pirate is currently headed.
	private Vector2 currGoal;
	
	private PirateVillage base;

	private enum MoveMode {
			WANDER,		// WHEN NOT IN CHASE RANGE OF PLAYER, AND NOT ALMOST OUT OF BASE RANGE - undefined
			CHASE,		// WHEN IN CHASE RANGE OF PLAYER, AND NOT ALMOST OUT OF BASE RANGE - follow player
			RETURN		// WHEN ALMOST OUT OF BASE RANGE - return to spawn until not almost out of base range
	}

	private MoveMode currMoveMode;
	
	public Pirate(Vector2 pos, Sprite sprite, SpiceTraderMap map, float initialDirection, PirateVillage base) {
		super(pos, sprite, map, Utils.statToUse(DEFAULT_MAX_SPEED, 'm'), Utils.statToUse(DEFAULT_ACCEL, 'a'), Utils.statToUse(DEFAULT_TURNING, 't'), initialDirection, DEFAULT_HULL);
		
		this.base = base;
		
		movementCooldown = 0;
		
		pathToPlayer = map.getPlayerDijkstraMap();
		pathToSpawn	 = base.getSpawnDijkstraMap();
	}

	@Override
	protected void createHitbox() {
		this.setHitbox(Ship.getShipHitbox(this.getWidth(), this.getHeight(), 2));
	}

	@Override
	public void tick() {
		super.tick();

		currMoveMode = calcCurrMoveMode();
		currGoal = getNextMove();

		//check if pirate can still move
		if(currGoal != null) {
			if(movementCooldown <= 0)
				moveTowardsPoint(currGoal);
			else
				movementCooldown--;
		}

	}

	private MoveMode calcCurrMoveMode() {
		if(pathToSpawn.almostOutOfRange(getHitCenter()))
			return MoveMode.RETURN;
		if(pathToPlayer.inRange(getHitCenter()))
			return MoveMode.CHASE;
		else
			return MoveMode.WANDER;
	}

	private Vector2 getNextMove() {
		switch(currMoveMode) {
			case WANDER:
				//TODO: defined and implement wander behavior
				return getHitCenter();
			case CHASE:
				return pathToPlayer.getNextMove(getHitCenter());
			case RETURN:
				return pathToSpawn.getNextMove(getHitCenter());
		}
		return null;
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
