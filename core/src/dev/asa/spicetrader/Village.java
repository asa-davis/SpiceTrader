package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Village extends Entity {
	
	Vector2 originTile;
	Vector2 dockTile;
	Polygon dockHitbox;
	float distFromCenter;
	
	public Village(Vector2 pos, Sprite sprite, EntityFactory.VillageLocation location, Polygon dockHitbox) {
		super(pos, sprite);
		this.originTile = location.tileOrigin;
		this.dockTile = location.dockTile;
		this.dockHitbox = dockHitbox;
		this.distFromCenter = location.distFromCenter;
	}

	@Override
	void tick() {
		
	}
	
	@Override
	public void drawHitbox(ShapeRenderer renderer) {
		super.drawHitbox(renderer);
		renderer.polygon(dockHitbox.getVertices());
	}
	
	public Polygon getDockHitbox() {
		return dockHitbox;
	}

	public Vector2 getOriginTile() {
		return originTile;
	}
	
	public Vector2 getDockTile() {
		return dockTile;
	}

	public String getName() {
		return "Distance from Center: " + distFromCenter;
	}
}
