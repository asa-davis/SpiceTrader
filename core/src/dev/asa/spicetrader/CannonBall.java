package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CannonBall extends Entity{
	
	//These values need to be messed with once pirates are in the game to figure out what works best
	private static float ACCEL = -0.2f;
	private static float SPEED_CUTOFF = 1.5f;
	private static float INITIAL_SPEED = 6;
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

	//move cannon ball in it's direction, by current speed. 
	@Override
	void tick() {
		if(this.currSpeed < CannonBall.SPEED_CUTOFF)
			this.exists = false;
		this.move();
		this.currSpeed += CannonBall.ACCEL;
	}
}
