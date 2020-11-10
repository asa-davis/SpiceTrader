package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CannonBall extends Entity{
	
	private float direction;

	public CannonBall(Vector2 pos, Sprite sprite, float direction) {
		super(pos, sprite);
		this.direction = direction;
	}

	@Override
	void createHitbox() {
		Rectangle rec = this.getSprite().getBoundingRectangle();
		Polygon hitbox = new Polygon(new float[] {rec.x, rec.y, rec.x + rec.width, rec.y, rec.x + rec.width, rec.y + rec.height, rec.x, rec.y + rec.height});
		this.setHitbox(hitbox);
	}
}
