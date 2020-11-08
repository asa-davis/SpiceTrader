package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

//parent class for everything with a hitbox, position, sprite, etc.
//children include ship (pirate, player), village, cannonball, sea monsters (?), etc.
public abstract class Entity {
	//in pixels not tiles
	private Vector2 pos;
	private float direction;
	private float speed;
	private float rotationSpeed;
	private float spriteWidth;
	private float spriteHeight;
	private Polygon hitbox;
	private Vector2 hitCenter;
	private Sprite sprite;
	private SpiceTraderMap map;
	
	
	public Entity(SpiceTraderMap map, Vector2 pos, Sprite sprite, float speed, float rotationSpeed, float initialDirection) {
		this.map = map;
		this.pos = pos;
		this.sprite = sprite;
		this.speed = speed;
		this.rotationSpeed = rotationSpeed;
		this.direction = initialDirection;
		
		this.spriteWidth = sprite.getWidth();
		this.spriteHeight = sprite.getHeight();

		this.createHitbox();
		this.hitCenter = new Vector2((this.spriteWidth/2) + pos.x, (this.spriteHeight/2) + pos.y);
		
		this.hitbox.setPosition(pos.x, pos.y);
		this.sprite.setPosition(pos.x, pos.y);
		this.hitbox.setRotation(direction);
		this.sprite.setRotation(direction);
	}
	
	public Entity(SpiceTraderMap map, Vector2 pos, Sprite sprite) {
		this(map, pos, sprite, 1, 1, 0);
	}
	
	//this method must instantiate our hitbox
	abstract void createHitbox();
	
	private void updatePosition(float xMove, float yMove) {
		this.pos.x += xMove;
		this.pos.y += yMove;
		this.hitCenter.x += xMove;
		this.hitCenter.y += yMove;
		
		this.sprite.translate(xMove, yMove);
		this.hitbox.translate(xMove, yMove);
	}
	
	private void updateRotation(float turnAmount) {
		this.direction += turnAmount;
		
		this.hitbox.setRotation(this.direction);
		this.sprite.setRotation(this.direction);
	}
	
	public Vector2 moveForward() {
		float xMoveInc = -1 * (float) Math.sin(0.0175 * this.direction);
		float yMoveInc = (float) Math.cos(0.0175 * this.direction);
		float xMoveTotal = xMoveInc * this.speed;
		float yMoveTotal = yMoveInc * this.speed;
		
		this.updatePosition(xMoveTotal, yMoveTotal);
		
		//collision detection - undo move if hitting map 
		int numMoves = (int) this.speed + 1;
		while(!this.map.validShipPosition(this.hitbox, this.hitCenter) && numMoves >= 0) {
			this.updatePosition(-1 * xMoveInc, -1 * yMoveInc);
			numMoves--;
		}

		return this.hitCenter;
	}
	
	public Vector2 moveBackward() {
		float xMoveInc = (float) Math.sin(0.0175 * this.direction);
		float yMoveInc = -1 * (float) Math.cos(0.0175 * this.direction);
		float xMoveTotal = xMoveInc * this.speed;
		float yMoveTotal = yMoveInc * this.speed;
		
		this.updatePosition(xMoveTotal, yMoveTotal);
		
		//collision detection - undo move if hitting map 
		int numMoves = (int) this.speed + 1;
		while(!this.map.validShipPosition(this.hitbox, this.hitCenter) && numMoves >= 0) {
			this.updatePosition(-1 * xMoveInc, -1 * yMoveInc);
			numMoves--;
		}

		return this.hitCenter;
	}
	
	public void turnCW() {
		this.updateRotation(-1 * this.rotationSpeed);
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (this.rotationSpeed + 1);
		while(!this.map.validShipPosition(this.hitbox, this.hitCenter) && numMoves >= 0) {
			this.updateRotation(1);
			numMoves--;
		}
	}
	
	public void turnCCW() {
		this.updateRotation(this.rotationSpeed);
		
		//collision detection - undo move if hitting map
		int numMoves = (int) (this.rotationSpeed + 1);
		while(!this.map.validShipPosition(this.hitbox, this.hitCenter) && numMoves >= 0) {
			this.updateRotation(-1);
			numMoves--;
		}
	}
	
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public void drawHitbox(ShapeRenderer renderer) {
		renderer.polygon(this.hitbox.getTransformedVertices());
		renderer.circle(this.hitCenter.x, this.hitCenter.y, 1);
	}
	
	public float getWidth() {
		return this.spriteWidth;
	}
	
	public float getHeight() {
		return this.spriteHeight;
	}
	
	public Polygon getHitbox() {
		return this.hitbox;
	}
	
	public Vector2 getHitCenter() {
		return this.hitCenter;
	}
	
	public void setHitbox(Polygon p) {
		this.hitbox = p;
	}
	
	public void setMap(SpiceTraderMap map) {
		this.map = map;
	}
}
