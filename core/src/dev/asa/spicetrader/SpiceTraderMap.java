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
	private DijkstraMap playerDistMap;
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
		this.atlas = atlas;
		this.waveRecalcFreq = 60;
		this.waveFreq = waveFreq;
		this.frameCounter = 0;
		
		//for debugging map hitboxes
		this.hitboxRenderer = new ShapeRenderer();
		hitboxRenderer.setColor(Color.RED);
		potentialCollisions = new ArrayList<Polygon>();
		
		//for pirate pathfinding
		playerDistMap = new DijkstraMap(15, this);
		
	}
	
	public void render(OrthographicCamera camera, boolean showHitboxes, boolean showGrid) {
		this.mapRenderer.setView(camera);
		this.mapRenderer.render();
		
		//for debugging
				
		//draw grid over all tiles
		if(showGrid) {
			hitboxRenderer.setProjectionMatrix(camera.combined);
			hitboxRenderer.begin(ShapeType.Line);
			
			hitboxRenderer.setColor(Color.WHITE);
			for(int col = 0; col <= numCols; col++)
				hitboxRenderer.line(new Vector2(col * tileWidth, 0), new Vector2(col * tileWidth, this.getSizePixels().y));
			for(int row = 0; row <= numRows; row++)
				hitboxRenderer.line(new Vector2(0, row * tileHeight), new Vector2(this.getSizePixels().x, row * tileHeight));
			
			hitboxRenderer.end();
		}
		
		//draw hitboxes of tiles with boats near them
		if(showHitboxes) {
			hitboxRenderer.setProjectionMatrix(camera.combined);
			hitboxRenderer.begin(ShapeType.Line);
			
			hitboxRenderer.setColor(Color.RED);
			for(Polygon tileHitbox : potentialCollisions) 
				hitboxRenderer.polygon(tileHitbox.getVertices());
			
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
	
	//returns true if ship is not intersecting with shore
	//first we get the tile coords that the center of the hitbox is on
	//then we check the tile Ids of those coords and the 8 neighboring tiles
	//if any of these are land tiles (id != 0) we fetch their appropriate hitbox using the bitmask and check it for intersections with ship hitbox
	public boolean validShipPosition(Ship ship) {
		Polygon shipHitbox = ship.getHitbox();
		Vector2 shipCenter = ship.getHitCenter();
		int[] currTile = this.getTileCoordsFromPixels(shipCenter);
		List<int[]> neighbors = Utils.getNeighborCoords(currTile[0], currTile[1], numCols, numRows, true, true);
		
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
		mapRenderer = new OrthogonalTiledMapRenderer(libgdxMap);
	}

	public Vector2 getSizePixels() {
		return new Vector2(this.numCols * this.tileWidth, this.numRows * this.tileHeight);
	}
	
	public Vector2 getSizeTiles() {
		return new Vector2(this.numCols, this.numRows);
	}
	
	public int[][] getBitmaskMap() {
		return neighborBitmaskMap;
	}
	
	public int getTileId(int[] tile) {
		return tileIdMap[tile[1]][tile[0]];
	}
	
	public Vector2 getTileSize() {
		return new Vector2(tileWidth, tileHeight);
	}
	
	public DijkstraMap getDijkstraMap() {
		return playerDistMap;
	}
}
