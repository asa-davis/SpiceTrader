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
	
	public Entity(Vector2 pos, Sprite sprite) {
		this.exists = true;
		
		this.pos = pos;
		this.sprite = sprite;
		spriteWidth = sprite.getWidth();
		spriteHeight = sprite.getHeight();

		//initialize hitbox and set center point to center of sprite
		createHitbox();
		hitCenter = new Vector2((spriteWidth/2) + pos.x, (spriteHeight/2) + pos.y);
		
		hitbox.setPosition(pos.x, pos.y);
		sprite.setPosition(pos.x, pos.y);
	}
	
	//this method must instantiate our hitbox
	protected void createHitbox() {
		Rectangle rec = sprite.getBoundingRectangle();
		Polygon newHitbox = new Polygon(new float[] {rec.x, rec.y, rec.x + rec.width, rec.y, rec.x + rec.width, rec.y + rec.height, rec.x, rec.y + rec.height});
		hitbox = newHitbox;
	}
	//this method is called on every frame. 
	abstract void tick();
	
	public void setPosition(Vector2 pos) {
		this.pos = pos;
		createHitbox();
		hitCenter = new Vector2((spriteWidth/2) + pos.x, (spriteHeight/2) + pos.y);
		hitbox.setPosition(pos.x, pos.y);
		sprite.setPosition(pos.x, pos.y);
	}
	
	public void updatePosition(float xMove, float yMove) {
		pos.x += xMove;
		pos.y += yMove;
		hitCenter.x += xMove;
		hitCenter.y += yMove;
		
		sprite.translate(xMove, yMove);
		hitbox.translate(xMove, yMove);
	}
	
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public void drawHitbox(ShapeRenderer renderer) {
		renderer.polygon(hitbox.getTransformedVertices());
		//renderer.circle(hitCenter.x, hitCenter.y, 1);
	}
	
	public float getWidth() {
		return spriteWidth;
	}
	
	public float getHeight() {
		return spriteHeight;
	}
	
	public Polygon getHitbox() {
		return hitbox;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public Vector2 getHitCenter() {
		return hitCenter;
	}
	
	public void setHitbox(Polygon p) {
		hitbox = p;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
		this.sprite.setPosition(pos.x, pos.y);
	}
}
