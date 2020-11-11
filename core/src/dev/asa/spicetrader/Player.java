package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Ship {
	
	//player sprites:
	//	0 = base ship
	//	1 = base ship w/ cannons
	//	2 = left cannon firing
	//	3 = right cannon firing
	//	4 = both cannons firing
	private Sprite[] sprites;
	private static int INIT_SPRITE = 1;
	private static int FIRING_SPRITE_COOLDOWN = 10;
	
	//These variables make sure the proper sprite is displayed after firing for the proper number of frames
	private int firingLeftSpriteCooldown;
	private int firingRightSpriteCooldown;
	private boolean firingLeft;
	private boolean firingRight;
	
	public Player(Vector2 pos, Sprite[] sprites, SpiceTraderMap map, float speed, float rotationSpeed, float initialDirection) {
		super(pos, sprites[Player.INIT_SPRITE], map, speed, rotationSpeed, initialDirection);
		this.sprites = sprites;
		this.firingLeftSpriteCooldown = 0;
		this.firingRightSpriteCooldown = 0;
		this.firingLeft = false;
		this.firingRight = false;
	}
	
	@Override
	void tick() {
		this.calcPlayerFiringSprite();
	}
	
	//TODO: this method should return the point between the two cannons on the sprite. 
	//		this can be calculated by shifting from the center of ship based on this.direction
	private Vector2 calcBallInitPos(Sprite sprite) {
		Vector2 initPos = new Vector2();
		Vector2 centerOfShip = this.getHitCenter();
		initPos.x = centerOfShip.x - (sprite.getWidth()/2);
		initPos.y = centerOfShip.y - (sprite.getHeight()/2);
		return initPos;
	}
	
	public CannonBall fireCannonLeft(Sprite sprite) {
		CannonBall shot = new CannonBall(this.calcBallInitPos(sprite), sprite, this.getDirection() + 90);
		this.firingLeft = true;
		this.firingLeftSpriteCooldown = Player.FIRING_SPRITE_COOLDOWN;
		if(this.firingRight) 
			this.setSprite(sprites[4]);
		else
			this.setSprite(sprites[2]);
		return shot;
	}

	public CannonBall fireCannonRight(Sprite sprite) {
		CannonBall shot = new CannonBall(this.calcBallInitPos(sprite), sprite, this.getDirection() - 90);
		this.firingRight = true;
		this.firingRightSpriteCooldown = Player.FIRING_SPRITE_COOLDOWN;
		if(this.firingLeft) 
			this.setSprite(sprites[4]);
		else
			this.setSprite(sprites[3]);
		return shot;
	}
	
	//logic for setting correct firing sprites
	private void calcPlayerFiringSprite() {
		if(this.firingLeft) {
			this.firingLeftSpriteCooldown--;
			if(this.firingLeftSpriteCooldown == 0) {
				this.firingLeft = false;
				if(this.firingRight) {
					this.setSprite(sprites[3]);
				}
				else {
					this.setSprite(sprites[Player.INIT_SPRITE]);
				}
			}
		}
		if(this.firingRight) {
			this.firingRightSpriteCooldown--;
			if(this.firingRightSpriteCooldown == 0) {
				this.firingRight = false;
				if(this.firingLeft) {
					this.setSprite(sprites[2]);
				}
				else {
					this.setSprite(sprites[Player.INIT_SPRITE]);
				}
			}
		}
	}
}
