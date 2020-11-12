package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Pirate extends Ship{

	public Pirate(Vector2 pos, Sprite sprite, SpiceTraderMap map, float speed, float rotationSpeed, float initialDirection) {
		super(pos, sprite, map, speed, rotationSpeed, initialDirection);
	}

	@Override
	void createHitbox() {
		this.setHitbox(Ship.getShipHitbox(this.getWidth(), this.getHeight(), 2));
	}

	@Override
	void tick() {
		// TODO Auto-generated method stub
		
	}

}
