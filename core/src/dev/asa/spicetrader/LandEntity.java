package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public abstract class LandEntity extends Entity {
	
	private Vector2 originTile;
	private Vector2 dockTile;
	private Polygon dockHitbox;
	private int tier;

	
	public LandEntity(Vector2 pos, Sprite sprite, EntityFactory.LandEntityLocation location, Polygon dockHitbox) {
		super(pos, sprite);
		this.originTile = location.tileOrigin;
		this.dockTile = location.dockTile;
		this.dockHitbox = dockHitbox;
		this.tier = location.tier;
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
		return "Unnamed Land Entity Tier " + tier;
	}
}
