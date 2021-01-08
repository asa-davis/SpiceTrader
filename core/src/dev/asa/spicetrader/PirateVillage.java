package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class PirateVillage extends Village {
	
	private Vector2 spawnLocation;
	private int spawnCounter;
	private int spawnInterval;
	private SpiceTraderMap map;
	private Sprite pirateSprite;
	
	public PirateVillage(Vector2 pos, Sprite sprite, Vector2 originTile, Vector2 dockTile, Polygon dockHitbox, float distFromCenter, SpiceTraderMap map, Sprite pirateSprite, Vector2 spawnLocation) {
		super(pos, sprite, originTile, dockTile, dockHitbox, distFromCenter);
		this.map = map;
		this.pirateSprite = pirateSprite;
		this.spawnLocation = spawnLocation;
		
		spawnCounter = 0;
		spawnInterval = 180;
	}
	
	@Override
	void tick() {
		//spawn a new pirate every spawn interval
		if(spawnCounter >= spawnInterval) {
			spawnCounter = 0;
			Pirate p = new Pirate(spawnLocation, pirateSprite, map, 0);
			p.setPosition(spawnLocation);
			//getManager().addNextTick(p);
		}
		else 
			spawnCounter++;
		
	}
}
