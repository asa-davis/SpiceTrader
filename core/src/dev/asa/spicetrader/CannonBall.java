package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CannonBall extends Entity{
	
	private static float INITIAL_SPEED = 5;
	private float direction;
	private float currSpeed;
	
	public CannonBall(Vector2 pos, Sprite sprite, float direction) {
		super(pos, sprite);
		this.direction = direction;
		this.currSpeed = CannonBall.INITIAL_SPEED;
	}
	
	private void move() {
		float xMoveInc = -1 * (float) Math.sin(0.0175 * this.direction);
		float yMoveInc = (float) Math.cos(0.0175 * this.direction);
		float xMoveTotal = xMoveInc * this.currSpeed;
		float yMoveTotal = yMoveInc * this.currSpeed;
		
		this.updatePosition(xMoveTotal, yMoveTotal);
	}

	@Override
	void createHitbox() {
		Rectangle rec = this.getSprite().getBoundingRectangle();
		Polygon hitbox = new Polygon(new float[] {rec.x, rec.y, rec.x + rec.width, rec.y, rec.x + rec.width, rec.y + rec.height, rec.x, rec.y + rec.height});
		this.setHitbox(hitbox);
	}

	//move cannon ball in it's direction, by current speed. 
	@Override
	void tick() {
		if(this.currSpeed < 0.75)
			this.exists = false;
		this.move();
		this.currSpeed -= 0.08;
	}
}
