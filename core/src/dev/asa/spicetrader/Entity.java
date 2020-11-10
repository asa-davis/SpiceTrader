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
		this.setMap(map);
		this.pos = pos;
		this.sprite = sprite;
		this.setSpeed(speed);
		this.setRotationSpeed(rotationSpeed);
		this.setDirection(initialDirection);
		
		this.spriteWidth = sprite.getWidth();
		this.spriteHeight = sprite.getHeight();

		this.createHitbox();
		this.hitCenter = new Vector2((this.spriteWidth/2) + pos.x, (this.spriteHeight/2) + pos.y);
		
		this.hitbox.setPosition(pos.x, pos.y);
		this.sprite.setPosition(pos.x, pos.y);
		this.hitbox.setRotation(getDirection());
		this.sprite.setRotation(getDirection());
	}
	
	public Entity(SpiceTraderMap map, Vector2 pos, Sprite sprite) {
		this(map, pos, sprite, 1, 1, 0);
	}
	
	//this method must instantiate our hitbox
	abstract void createHitbox();
	
	public void updatePosition(float xMove, float yMove) {
		this.pos.x += xMove;
		this.pos.y += yMove;
		this.hitCenter.x += xMove;
		this.hitCenter.y += yMove;
		
		this.sprite.translate(xMove, yMove);
		this.hitbox.translate(xMove, yMove);
	}
	
	public void updateRotation(float turnAmount) {
		this.setDirection(this.getDirection() + turnAmount);
		
		this.hitbox.setRotation(this.getDirection());
		this.sprite.setRotation(this.getDirection());
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

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public SpiceTraderMap getMap() {
		return map;
	}

	public float getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}
}
