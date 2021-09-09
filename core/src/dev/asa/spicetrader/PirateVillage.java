package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class PirateVillage extends Village {
	
	private static final int NUM_SECONDS_BETWEENS_SPAWNS = 5;
	private static final int MAX_ACTIVE_PIRATES = 2;
	private static final int WANDER_DISTANCE = 30;	// size of square around spawn in tiles that pirates are allowed to travel within
													// ^^ used to calculate size of dijkstra map
	
	private SpiceTraderMap map;
	
	private Vector2 spawnLocation;
	private int spawnCounter;
	private int spawnInterval;
	private Sprite pirateSprite;
	private int numActivePirates;

	private DijkstraMap pathToSpawn;
	
	public PirateVillage(Vector2 pos, Sprite sprite, EntityFactory.VillageLocation location, Polygon dockHitbox, SpiceTraderMap map, Sprite pirateSprite) {
		super(pos, sprite, location, dockHitbox);
		this.map = map;
		this.pirateSprite = pirateSprite;
		this.spawnLocation = location.spawnLocation;

		pathToSpawn = new DijkstraMap(WANDER_DISTANCE, map);
		pathToSpawn.calcDijkstraMapToPixelCoords(spawnLocation);

		//random starting spawn counter for each instance
		spawnCounter = Utils.randInt(0, 180);
		spawnInterval = 60 * NUM_SECONDS_BETWEENS_SPAWNS;
		
		numActivePirates = 0;
	}
	
	@Override
	void tick() {
		//spawn a new pirate every spawn interval
		if(spawnCounter >= spawnInterval) {
			spawnCounter = 0;
			//try and spawn a pirate if max hasnt been hit
			if(numActivePirates < MAX_ACTIVE_PIRATES) {
				Pirate p = new Pirate(new Vector2(spawnLocation), new Sprite(pirateSprite), map, 0, this);
				getManager().addNextTick(p);
				numActivePirates++;
			}
		}
		else 
			spawnCounter++;
	}
	
	public void removePirate() {
		numActivePirates--;
	}

	public DijkstraMap getSpawnDijkstraMap() {
		return pathToSpawn;
	}
}
