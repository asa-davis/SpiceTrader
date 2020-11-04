package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;

public class Ship extends Entity {

	public Ship(float xPos, float yPos, Sprite sprite, float speed, float rotationSpeed, float initialDirection) {
		super(xPos, yPos, sprite, speed, rotationSpeed, initialDirection);
	}

	@Override
	void createHitbox() {
		Polygon hitbox = new Polygon(new float[] {2, 0, this.getWidth() - 2, 0, this.getWidth() - 2, this.getHeight() - 4, this.getWidth()/2, this.getHeight(), 2, this.getHeight() - 4});
		hitbox.setOrigin(this.getWidth()/2, this.getHeight()/2);
		this.setHitbox(hitbox);
	}
}
