package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Ship {
	
	private int maxHull;
	private int currCargo;
	private int maxCargo;
	
	private int gold = 999;
	private int cannonBalls = 5;
	
	private int cannonDamage = 5;
	private float cannonRange = 9;
	
	//player playerSprites:
	//	0 = base ship
	//	1 = base ship w/ cannons
	//	2 = left cannon firing
	//	3 = right cannon firing
	//	4 = both cannons firing
	private Sprite[] playerSprites;
	private Sprite cannonBallSprite;
	private final static int INIT_SPRITE = 1;
	private final static int FIRING_SPRITE_COOLDOWN = 10; 
	
	//These variables make sure the proper sprite is displayed after firing for the proper number of frames
	private int firingLeftSpriteCooldown;
	private int firingRightSpriteCooldown;
	private boolean firingLeft;
	private boolean firingRight;
	
	//for determining when/where a player can dock
	private Village dockable;
	
	public Player(Vector2 pos, Sprite[] playerSprites, Sprite cannonBallSprite, SpiceTraderMap map, int maxHull) {
		super(pos, playerSprites[Player.INIT_SPRITE], map, 0.8f, 0.2f, 1f, 0, maxHull);
		this.maxHull = maxHull;
		this.playerSprites = playerSprites;
		this.cannonBallSprite = cannonBallSprite;
		this.firingLeftSpriteCooldown = 0;
		this.firingRightSpriteCooldown = 0;
		this.firingLeft = false;
		this.firingRight = false;
		this.dockable = null;
		
		this.getMap().getDijkstraMap().calcPlayerDistMap(this.getHitCenter());
	}
	
	@Override
	public void tick() {
		super.tick();
		this.calcPlayerFiringSprite();
		if(this.getCurrSpeed() > 0)
			this.getMap().getDijkstraMap().calcPlayerDistMap(this.getHitCenter());
	}
	
	@Override
	protected void createHitbox() {
		this.setHitbox(Ship.getShipHitbox(this.getWidth(), this.getHeight(), 3));
	}
	
	public CannonBall fireCannonLeft() {
		if(cannonBalls > 0) {
			cannonBalls--;
			CannonBall shot = new CannonBall(this.calcBallInitPos(), new Sprite(this.cannonBallSprite), this.getDirection() + 90, cannonRange, cannonDamage);
			this.firingLeft = true;
			this.firingLeftSpriteCooldown = Player.FIRING_SPRITE_COOLDOWN;
			if(this.firingRight) 
				this.setSprite(playerSprites[4]);
			else
				this.setSprite(playerSprites[2]);
			return shot;
		}
		return null;
	}

	public CannonBall fireCannonRight() {
		if(cannonBalls > 0) {
			cannonBalls--;
			CannonBall shot = new CannonBall(this.calcBallInitPos(), new Sprite(this.cannonBallSprite), this.getDirection() - 90, cannonRange, cannonDamage);
			this.firingRight = true;
			this.firingRightSpriteCooldown = Player.FIRING_SPRITE_COOLDOWN;
			if(this.firingLeft) 
				this.setSprite(playerSprites[4]);
			else
				this.setSprite(playerSprites[3]);
			return shot;
		}
		return null;
	}
	
	//returns normalized values of stats fit to scale
	//0 = max speed, 1 = accel, 2 = turning, 3 = damage, 4 = range
	public int[] getStats() {
		int[] stats = new int[5];
		stats[0] = Utils.statToView(getMaxSpeed(), 'm');
		stats[1] = Utils.statToView(getAccel(), 'a');
		stats[2] = Utils.statToView(getRotationSpeed(), 't');
		stats[3] = Utils.statToView(cannonDamage, 'd');
		stats[4] = Utils.statToView(cannonRange, 'r');
		return stats;
	}
	
	public void setDockable(Village dockable) {
		this.dockable = dockable;
	}
	
	public Village getDockable() {
		return dockable;
	}
	
	public int getMaxHull() {
		return maxHull;
	}

	public int getCurrCargo() {
		return currCargo;
	}

	public int getMaxCargo() {
		return maxCargo;
	}

	public int getGold() {
		return gold;
	}

	public int getCannonBalls() {
		return cannonBalls;
	}
	
	//TODO: Move cannon firing stuff to another class so pirate villages can use it
	private Vector2 calcBallInitPos() {
		Vector2 initPos = new Vector2();
		Vector2 centerOfShip = this.getHitCenter();
		initPos.x = centerOfShip.x - (this.cannonBallSprite.getWidth()/2);
		initPos.y = centerOfShip.y - (this.cannonBallSprite.getHeight()/2);
		return initPos;
	}
	
	//logic for setting correct firing playerSprites
	private void calcPlayerFiringSprite() {
		if(this.firingLeft) {
			this.firingLeftSpriteCooldown--;
			if(this.firingLeftSpriteCooldown == 0) {
				this.firingLeft = false;
				if(this.firingRight) {
					this.setSprite(playerSprites[3]);
				}
				else {
					this.setSprite(playerSprites[Player.INIT_SPRITE]);
				}
			}
		}
		if(this.firingRight) {
			this.firingRightSpriteCooldown--;
			if(this.firingRightSpriteCooldown == 0) {
				this.firingRight = false;
				if(this.firingLeft) {
					this.setSprite(playerSprites[2]);
				}
				else {
					this.setSprite(playerSprites[Player.INIT_SPRITE]);
				}
			}
		}
	}
}
