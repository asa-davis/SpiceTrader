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
	
	private boolean updatePosition(float xMove, float yMove) {
		this.pos.x += xMove;
		this.pos.y += yMove;
		this.hitCenter.x += xMove;
		this.hitCenter.y += yMove;
		
		this.sprite.translate(xMove, yMove);
		this.hitbox.translate(xMove, yMove);
		
		return this.map.validShipPosition(this.hitbox, this.hitCenter);
	}
	
	private boolean updateRotation(float turnAmount) {
		this.direction += turnAmount;
		
		this.hitbox.setRotation(this.direction);
		this.sprite.setRotation(this.direction);
		
		return this.map.validShipPosition(this.hitbox, this.hitCenter);
	}
	
	public Vector2 moveForward() {
		float xMove = -1 * (float) Math.sin(0.0175 * this.direction) * this.speed;
		float yMove = (float) Math.cos(0.0175 * this.direction) * this.speed;
		
		xMove = Utils.round(xMove, 0);
		yMove = Utils.round(yMove, 0);
		
		this.updatePosition(xMove, yMove);
		
		return new Vector2(xMove, yMove);
	}
	
	public Vector2 moveBackward() {
		float xMove = (float) Math.sin(0.0175 * this.direction) * this.speed;
		float yMove = -1 * (float) Math.cos(0.0175 * this.direction) * this.speed;
		
		xMove = Utils.round(xMove, 0);
		yMove = Utils.round(yMove, 0);
		
		this.updatePosition(xMove, yMove);
		
		return new Vector2(xMove, yMove);
	}
	
	public void turnCW() {
		this.updateRotation(-1 * this.rotationSpeed);
	}
	
	public void turnCCW() {
		this.updateRotation(this.rotationSpeed);
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
	
	public void setHitbox(Polygon p) {
		this.hitbox = p;
	}
}
