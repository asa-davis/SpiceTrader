package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

//parent class for everything with a hitbox, position, sprite, etc.
//children include ship (pirate, player), village, cannonball, sea monsters (?), etc.
public abstract class Entity {
	//in pixels not tiles
	private float xPos;
	private float yPos;
	private float direction;
	private float speed;
	private float rotationSpeed;
	private float spriteWidth;
	private float spriteHeight;
	private Polygon hitbox;
	private Sprite sprite;
	
	
	public Entity(float xPos, float yPos, Sprite sprite, float speed, float rotationSpeed, float initialDirection) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.sprite = sprite;
		this.speed = speed;
		this.rotationSpeed = rotationSpeed;
		this.direction = initialDirection;
		
		this.spriteWidth = sprite.getWidth();
		this.spriteHeight = sprite.getHeight();

		this.createHitbox();
		
		this.hitbox.setPosition(xPos, yPos);
		this.sprite.setPosition(xPos, yPos);
		this.hitbox.setRotation(direction);
		this.sprite.setRotation(direction);
	}
	
	public Entity(float xPos, float yPos, Sprite sprite) {
		this(xPos, yPos, sprite, 1, 1, 0);
	}
	
	//this method must instantiate our hitbox
	abstract void createHitbox();
	
	private void updatePosition(float xMove, float yMove) {
		this.xPos += xMove;
		this.yPos += yMove;
		
		this.sprite.translate(xMove, yMove);
		this.hitbox.translate(xMove, yMove);
	}
	
	private void updateRotation(float turnAmount) {
		this.direction += turnAmount;
		
		this.hitbox.setRotation(this.direction);
		this.sprite.setRotation(this.direction);
	}
	
	public Vector2 moveForward() {
		float xMove = -1 * (float) Math.sin(0.0175 * this.direction);
		float yMove = (float) Math.cos(0.0175 * this.direction);
		
		this.updatePosition(xMove, yMove);
		
		return new Vector2(xMove, yMove);
	}
	
	public Vector2 moveBackward() {
		float xMove = (float) Math.sin(0.0175 * this.direction);
		float yMove = -1 * (float) Math.cos(0.0175 * this.direction);
		
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
