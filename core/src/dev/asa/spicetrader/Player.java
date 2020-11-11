package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Player extends Ship {

	public Player(Vector2 pos, Sprite sprite, SpiceTraderMap map, float speed, float rotationSpeed, float initialDirection) {
		super(pos, sprite, map, speed, rotationSpeed, initialDirection);
	}
	
	private Vector2 calcCanBallInitPos() {
		Vector2 initPos = new Vector2();
		Vector2 centerOfShip = this.getHitCenter();
		initPos.x = centerOfShip.x;
		initPos.y = centerOfShip.y;
		return initPos;
	}
	
	public CannonBall fireCannonLeft(Sprite sprite) {
		CannonBall shot = new CannonBall(this.calcCanBallInitPos(), sprite, this.getDirection() + 90);
		return shot;
	}

	public CannonBall fireCannonRight(Sprite sprite) {
		CannonBall shot = new CannonBall(this.calcCanBallInitPos(), sprite, this.getDirection() - 90);
		return shot;
	}

	@Override
	void tick() {
		
	}

}
