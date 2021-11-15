package dev.asa.spicetrader.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class CannonBall extends Entity {
	
	private static float ACCEL = -0.2f;
	private static float SPEED_CUTOFF = 1.5f;
	private float direction;
	private float currSpeed;
	private int damage;
	
	public CannonBall(Vector2 pos, Sprite sprite, float direction, float range, int damage) {
		super(pos, sprite);
		this.direction = direction;
		this.damage = damage;
		
		
		this.currSpeed = range;
	}
	
	private void move() {
		float xMoveInc = -1 * (float) Math.sin(0.0175 * direction);
		float yMoveInc = (float) Math.cos(0.0175 * direction);
		float xMoveTotal = xMoveInc * currSpeed;
		float yMoveTotal = yMoveInc * currSpeed;
		
		this.updatePosition(xMoveTotal, yMoveTotal);
	}

	//move cannon ball in it's direction, by current speed. 
	@Override
	void tick() {
		if(currSpeed < CannonBall.SPEED_CUTOFF)
			this.exists = false;
		move();
		currSpeed += CannonBall.ACCEL;
	}
	
	public int getDamage() {
		return damage;
	}
}
