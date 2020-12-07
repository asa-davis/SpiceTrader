package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SpiceTraderMap {
	private int numCols;
	private int numRows;
	private int tileWidth;
	private int tileHeight;
	
	//one wave per how many water tiles
	private int waveFreq;
	//how many frames between waves moving
	private int waveRecalcFreq;
	private int frameCounter;
	
	TextureAtlas atlas;
	
	//this holds the tileIds: 0 = water, 1 = land (maybe beach, maybe grass, maybe tree), 2 = village location (always grass no tree), 3 = dock
	private int[][] tileIdMap;
	//this tells us for a particular tile, which of the neighboring tiles in the 8 directions are land. 
	private int[][] neighborBitmaskMap;
	//holds the distance to player from each tile location - used for pirate pathfinding
	private int[][] playerDistMap;
	//libgdx objects holding the map in the form in which it can be rendered
	private TiledMap libgdxMap;
	private TiledMapRenderer mapRenderer;
	
	//for debugging map hitboxes
	private ShapeRenderer hitboxRenderer;
	private List<Polygon> potentialCollisions;
	
	public SpiceTraderMap(int numCols, int numRows, int tileWidth, int tileHeight, int waveFreq, TextureAtlas atlas) {		
		this.numCols = numCols;
		this.numRows = numRows;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		
		//for changing waves every x frames
		this.waveRecalcFreq = 60;
		this.waveFreq = waveFreq;
		this.frameCounter = 0;
		
		//for debugging map hitboxes
		this.hitboxRenderer = new ShapeRenderer();
		hitboxRenderer.setColor(Color.RED);
		potentialCollisions = new ArrayList<Polygon>();
		
		this.atlas = atlas;
		
		//for pirate pathfinding
		playerDistMap = new int[16][16];
		for(int[] row : playerDistMap) 
			Arrays.fill(row, numRows * numCols);
		
	}
	
	public void render(OrthographicCamera camera, boolean showHitboxes) {
		this.mapRenderer.setView(camera);
		this.mapRenderer.render();
		
		if(showHitboxes) {
			hitboxRenderer.setProjectionMatrix(camera.combined);
			hitboxRenderer.begin(ShapeType.Line);
			for(Polygon tileHitbox : potentialCollisions) 
				this.hitboxRenderer.polygon(tileHitbox.getVertices());
			hitboxRenderer.end();
		}
	}
	
	//every frame this gets called
	public void tick(boolean paused) {
		if(paused)
			return;
		
		this.frameCounter++;
		if(this.frameCounter == waveRecalcFreq) {
			this.frameCounter = 0;
			this.recalcWaves();
		}
		
		this.potentialCollisions.clear();	
	}
 	
	//returns a set of points representing the center tiles of the path to the player
	public List<Vector2> getPathToPlayer(Vector2 startPos) {
		//turn start pos into tile coords
		int[] currPos = this.getTileCoordsFromPixels(startPos);

		//generate path of tile coords
		List<int[]> tilePath = new ArrayList<int[]>();
		tilePath.add(currPos);
		tilePath = getPathToPlayerHelper(currPos, tilePath);
		
		//turn tile coords path into pixel path where each point is center of tile
		List<Vector2> path = new ArrayList<Vector2>();
		for(int[] tile : tilePath)
			path.add(this.getPixelCoordsFromTile(tile).add(new Vector2(tileWidth/2, tileHeight/2)));
		
		return path;
	}
	
	//recursive helper method - deals only with tile coords
	private List<int[]> getPathToPlayerHelper(int[] currPos, List<int[]> currPath) {
		int currVal = playerDistMap[currPos[1]][currPos[0]];
		//if current position is on player tile, we are done
		if(currVal == 0) 
			return currPath;
		
		//otherwise, find lowest value neighbor square, set it to currPos, add it to currPath and continue
		List<int[]> neighbors = Utils.getNeighborCoords(currPos[0], currPos[1], playerDistMap[0].length, playerDistMap.length, false);
		int lowestVal = currVal;
		int[] lowestPos = currPos;
		
		for(int[] neighborPos : neighbors) {
			if(neighborPos != null) {
				int neighborVal = playerDistMap[neighborPos[1]][neighborPos[0]];
				if(neighborVal < lowestVal) {
					lowestPos = neighborPos;
					lowestVal = neighborVal;
				}
			}
		}
		
		//System.out.println("Adding (" + lowestPos[0] + ", " + lowestPos[1] + ") to path with value of " + lowestVal);
		currPath.add(lowestPos);
		return getPathToPlayerHelper(lowestPos, currPath);
	}
	
	//creates a "dijkstra" map representing the distance from the player for every tile
	//algorithm taken from here: http://www.roguebasin.com/index.php?title=The_Incredible_Power_of_Dijkstra_Maps
	public void calcPlayerDistMap(Vector2 playerPos) {
		int playerCol = this.getTileCoordsFromPixels(playerPos)[0];
		int playerRow = this.getTileCoordsFromPixels(playerPos)[1];
		
		//0. check if player tile position has changed. if it hasn't, we dont need to do anything.
		if(playerDistMap[playerRow][playerCol] == 0) 
			return;
		
		//1. fill map with maximum value except for at player position, where value = 0
		for(int[] row : playerDistMap) 
			Arrays.fill(row, numRows * numCols);
		playerDistMap[playerRow][playerCol] = 0;

		//TODO: IMPLEMENT RANGE FOR THIS ALGORITHM. CURRENTLY IT CREATES A DIJSKTRA MAP FOR ENTIRE MAP
		//2. determine range of tiles to build map on - maximum size of map is 32x32, player must always be centered and we cant look outside map bounds
		
		
		//3. iterate through map, checking for tiles where the lowest value neighbor is more than 1 less than the tile value. 
		//	 for these tiles, set value to the neighbor value + 1. repeat until no more cases. ignore land tiles.
		boolean done = false;
		boolean oneMore = false;
		while(!done) {
			boolean tileChanged = false;
			
			for(int row = 0; row < numRows; row++) {
				for(int col = 0; col < numCols; col++) {
					//only process ocean tiles
					if(tileIdMap[row][col] == 0) {
						//collect current value and neighbor coords
						List<int[]> neighborCoords = Utils.getNeighborCoords(col, row, numCols, numRows, false);
						int currValue = playerDistMap[row][col];
						
						//collect lowest neighbor value (ignoring neighbors with values greater than current
						int lowestNeighborValue = currValue;
						for(int[] neighbor : neighborCoords) {
							if(neighbor != null) {
								int currNeighborValue = playerDistMap[neighbor[1]][neighbor[0]];
								if(currNeighborValue < lowestNeighborValue)
									lowestNeighborValue = currNeighborValue;	
							}
						}
						
						//check if more than 1 below current value and set current appropriately.
						if(lowestNeighborValue < currValue - 1) {
							tileChanged = true;
							playerDistMap[row][col] = lowestNeighborValue + 1;
						}
					}
				}
			}
			
			if(!tileChanged) {
				done = true;
			}
		}
		boolean debug = false;
		if(debug) {
			System.out.println("NEW MAP:");
			for(int row = numRows - 1; row >= 0; row--) {
				for(int col = 0; col < numCols; col++) {
					int val = playerDistMap[row][col];
					if(val < 10)
						System.out.print("00" + val + ", ");
					else if(val < 100)
						System.out.print("0" + val + ", ");
					else if(val < 1000)
						System.out.print(val + ", ");
				}
				System.out.print('\n');
			}
		}
	}
	
	//returns true if ship is not intersecting with shore
	//first we get the tile coords that the center of the hitbox is on
	//then we check the tile Ids of those coords and the 8 neighboring tiles
	//if any of these are land tiles (id != 0) we fetch their appropriate hitbox using the bitmask and check it for intersections with ship hitbox
	public boolean validShipPosition(Ship ship) {
		Polygon shipHitbox = ship.getHitbox();
		Vector2 shipCenter = ship.getHitCenter();
		int[] currTile = this.getTileCoordsFromPixels(shipCenter);
		List<int[]> neighbors = Utils.getNeighborCoords(currTile[0], currTile[1], numCols, numRows, true);
		
		//gather all nearby tile hitboxes
		List<Polygon> nearbyTiles = new ArrayList<Polygon>();
		for(int[] tile : neighbors) {
			if(tile != null && this.tileIdMap[tile[1]][tile[0]] != 0) {
				float xOrigin = tile[0] * this.tileWidth;
				float yOrigin = tile[1] * this.tileHeight;
				Polygon tileHitbox = new Polygon();
				tileHitbox.setVertices(new float[] {xOrigin, yOrigin, xOrigin + 16, yOrigin, xOrigin + 16, yOrigin + 16, xOrigin, yOrigin + 16});
				nearbyTiles.add(tileHitbox);
			}
		}
		
		//so we can render hitboxes if desired
		potentialCollisions.addAll(nearbyTiles);
		
		//check for collisions and determine position validity
		for(Polygon tileHitbox : nearbyTiles) {
			if(Intersector.overlapConvexPolygons(shipHitbox, tileHitbox)) {
				hitboxRenderer.end();
				return false;
			}
		}
		
		return true;
	}
	
	private void recalcWaves() {
		TiledMapTileLayer mapLayer = (TiledMapTileLayer) this.libgdxMap.getLayers().get(0);
		Array<AtlasRegion> waterTextures = this.atlas.findRegions("tile/water");
		for(int row = 0; row < this.numRows; row++) {
			for(int col = 0; col < this.numCols; col++) {
				if(this.tileIdMap[row][col] == 0) {
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
					StaticTiledMapTile tile;
					if(Utils.randInt(1, this.waveFreq) == 1) {
						tile = new StaticTiledMapTile(waterTextures.get(Utils.randInt(1, waterTextures.size - 1)));
					}
					else {
						tile = new StaticTiledMapTile(waterTextures.get(0));
					}
					cell.setTile(tile);
					mapLayer.setCell(col, row, cell);
				}
			}
		}
	}
	
	public void addVillages(List<Village> villages) {
		//for each village, we need to set the tiles behind the village to pure grass (no tree), and the tile for the dock
		//then we regenerate the TiledMap and MapRenderer
		for(Village v : villages) {
			Vector2 originTile = v.getOriginTile();
			Vector2 dockTile = v.getDockTile();
			tileIdMap[(int) originTile.y][(int) originTile.x] = 2;
			tileIdMap[(int) originTile.y - 1][(int) originTile.x] = 2;
			tileIdMap[(int) originTile.y][(int) originTile.x - 1] = 2;
			tileIdMap[(int) originTile.y - 1][(int) originTile.x - 1] = 2;
			tileIdMap[(int) dockTile.y][(int) dockTile.x] = 3;
		}
		TiledMap newMap = SpiceTraderMapGenerator.generateLibgdxMap(numCols, numRows, tileWidth, tileHeight, neighborBitmaskMap, tileIdMap, waveFreq, atlas);
		this.setLibgdxMap(newMap);
	}
	
	//returns pixel coords for bottom left corner of given tile
	public Vector2 getPixelCoordsFromTile(Vector2 tilePos) {
		return new Vector2(tilePos.x * tileWidth, tilePos.y * tileHeight);
	}
	public Vector2 getPixelCoordsFromTile(int[] tileCoords) {
		return new Vector2(tileCoords[0] * tileWidth, tileCoords[1] * tileHeight);
	}
	
	//takes a position in pixels and returns the tile coordinates
	public int[] getTileCoordsFromPixels(Vector2 pos) {
		return new int[] {(int) (pos.x/this.tileWidth), (int) (pos.y/this.tileHeight)};
	}
	
	public void setTileIdMap(int[][] tileIdMap) {
		this.tileIdMap = tileIdMap;
	}
	
	public void setNeighborBitmaskMap(int[][] neighborBitmaskMap) {
		this.neighborBitmaskMap = neighborBitmaskMap;
	}
	
	public void setLibgdxMap(TiledMap libgdxMap) {
		this.libgdxMap = libgdxMap;
		this.mapRenderer = new OrthogonalTiledMapRenderer(this.libgdxMap);
	}

	public Vector2 getSize() {
		return new Vector2(this.numCols * this.tileWidth, this.numRows * this.tileHeight);
	}
	
	public int[][] getBitmaskMap() {
		return neighborBitmaskMap;
	}
	
	public Vector2 getTileSize() {
		return new Vector2(tileWidth, tileHeight);
	}
}
