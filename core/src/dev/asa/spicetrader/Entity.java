package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

//parent class for everything with a hitbox, position, sprite, etc.
//children include ship (pirate, player), village, cannonball, sea monsters (?), etc.
public abstract class Entity {
	public boolean exists;
	
	//in pixels not tiles
	private Vector2 pos;
	private float spriteWidth;
	private float spriteHeight;
	
	private Polygon hitbox;
	private Vector2 hitCenter;
	private Sprite sprite;
	private SpiceTraderMap map;
	
	
	public Entity(Vector2 pos, Sprite sprite) {
		this.exists = true;
		
		this.pos = pos;
		this.sprite = sprite;
		this.spriteWidth = sprite.getWidth();
		this.spriteHeight = sprite.getHeight();

		//initialize hitbox and set center point to center of sprite
		this.createHitbox();
		this.hitCenter = new Vector2((this.spriteWidth/2) + pos.x, (this.spriteHeight/2) + pos.y);
		
		this.hitbox.setPosition(pos.x, pos.y);
		this.sprite.setPosition(pos.x, pos.y);
	}
	
	//this method must instantiate our hitbox
	abstract void createHitbox();
	//this method is called on every frame. 
	abstract void tick();
	
	public void setPosition(Vector2 pos) {
		this.pos = pos;
		this.createHitbox();
		this.hitCenter = new Vector2((this.spriteWidth/2) + pos.x, (this.spriteHeight/2) + pos.y);
		this.hitbox.setPosition(pos.x, pos.y);
		this.sprite.setPosition(pos.x, pos.y);
	}
	
	public void updatePosition(float xMove, float yMove) {
		this.pos.x += xMove;
		this.pos.y += yMove;
		this.hitCenter.x += xMove;
		this.hitCenter.y += yMove;
		
		this.sprite.translate(xMove, yMove);
		this.hitbox.translate(xMove, yMove);
	}
	
	public void draw(SpriteBatch batch) {
		this.sprite.draw(batch);
	}
	
	public void drawHitbox(ShapeRenderer renderer) {
		renderer.polygon(this.hitbox.getTransformedVertices());
		//renderer.circle(this.hitCenter.x, this.hitCenter.y, 1);
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
	
	public Sprite getSprite() {
		return this.sprite;
	}
	
	public Vector2 getHitCenter() {
		return this.hitCenter;
	}
	
	public void setHitbox(Polygon p) {
		this.hitbox = p;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
		this.sprite.setPosition(pos.x, pos.y);
	}
}
