package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Ship {
	
	//player playerSprites:
	//	0 = base ship
	//	1 = base ship w/ cannons
	//	2 = left cannon firing
	//	3 = right cannon firing
	//	4 = both cannons firing
	private Sprite[] playerSprites;
	Sprite cannonBallSprite;
	private static int INIT_SPRITE = 1;
	private static int FIRING_SPRITE_COOLDOWN = 10; 
	
	//These variables make sure the proper sprite is displayed after firing for the proper number of frames
	private int firingLeftSpriteCooldown;
	private int firingRightSpriteCooldown;
	private boolean firingLeft;
	private boolean firingRight;
	
	//for determining when/where a player can dock
	private Village dockable;
	
	public Player(Vector2 pos, Sprite[] playerSprites, Sprite cannonBallSprite, SpiceTraderMap map) {
		super(pos, playerSprites[Player.INIT_SPRITE], map, 3, 0.05f, 2, 0);
		this.playerSprites = playerSprites;
		this.cannonBallSprite = cannonBallSprite;
		this.firingLeftSpriteCooldown = 0;
		this.firingRightSpriteCooldown = 0;
		this.firingLeft = false;
		this.firingRight = false;
		this.dockable = null;
		
		this.getMap().calcPlayerDistMap(this.getHitCenter());
	}
	
	@Override
	public void tick() {
		super.tick();
		this.calcPlayerFiringSprite();
		if(this.getCurrSpeed() > 0)
			this.getMap().calcPlayerDistMap(this.getHitCenter());
	}
	
	@Override
	protected void createHitbox() {
		this.setHitbox(Ship.getShipHitbox(this.getWidth(), this.getHeight(), 3));
	}
	
	//TODO: this method should return the point between the two cannons on the sprite. 
	//		this can be calculated by shifting from the center of ship based on this.direction
	private Vector2 calcBallInitPos() {
		Vector2 initPos = new Vector2();
		Vector2 centerOfShip = this.getHitCenter();
		initPos.x = centerOfShip.x - (this.cannonBallSprite.getWidth()/2);
		initPos.y = centerOfShip.y - (this.cannonBallSprite.getHeight()/2);
		return initPos;
	}
	
	public CannonBall fireCannonLeft() {
		CannonBall shot = new CannonBall(this.calcBallInitPos(), new Sprite(this.cannonBallSprite), this.getDirection() + 90);
		this.firingLeft = true;
		this.firingLeftSpriteCooldown = Player.FIRING_SPRITE_COOLDOWN;
		if(this.firingRight) 
			this.setSprite(playerSprites[4]);
		else
			this.setSprite(playerSprites[2]);
		return shot;
	}

	public CannonBall fireCannonRight() {
		CannonBall shot = new CannonBall(this.calcBallInitPos(), new Sprite(this.cannonBallSprite), this.getDirection() - 90);
		this.firingRight = true;
		this.firingRightSpriteCooldown = Player.FIRING_SPRITE_COOLDOWN;
		if(this.firingLeft) 
			this.setSprite(playerSprites[4]);
		else
			this.setSprite(playerSprites[3]);
		return shot;
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

	public void setDockable(Village dockable) {
		this.dockable = dockable;
	}
	
	public Village getDockable() {
		return dockable;
	}
}
