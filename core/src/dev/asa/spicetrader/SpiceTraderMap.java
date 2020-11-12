package dev.asa.spicetrader;

import java.util.ArrayList;
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
	
	//this holds the tileIds: 0 = water, 1 = land, 2 = village location, 100+ = dock (tileId - 100 = village index)
	private int[][] tileIdMap;
	//this tells us for a particular tile, which of the neighboring tiles in the 8 directions are land. 
	private int[][] neighborBitmaskMap;
	//libgdx objects holding the map in the form in which it can be rendered
	private TiledMap libgdxMap;
	private TiledMapRenderer mapRenderer;
	
	//for debugging map hitboxes
	private ShapeRenderer hitboxRenderer;
	
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
		
		this.atlas = atlas;
	}
	
	public void render(OrthographicCamera camera) {
		this.mapRenderer.setView(camera);
		this.mapRenderer.render();
		hitboxRenderer.setProjectionMatrix(camera.combined);
	}
	
	//every
	public void tick() {
		this.frameCounter++;
		if(this.frameCounter == waveRecalcFreq) {
			this.frameCounter = 0;
			this.recalcWaves();
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
		List<Vector2> neighbors = Utils.getNeighborCoords(currTile[0], currTile[1], numCols, numRows, true);

		hitboxRenderer.begin(ShapeType.Line);
		for(Vector2 tile : neighbors) {
			if(tile != null && this.tileIdMap[(int) tile.y][(int) tile.x] != 0) {
				float xOrigin = tile.x * this.tileWidth;
				float yOrigin = tile.y * this.tileHeight;
				Polygon tileHitbox = new Polygon();
				tileHitbox.setVertices(new float[] {xOrigin, yOrigin, xOrigin + 16, yOrigin, xOrigin + 16, yOrigin + 16, xOrigin, yOrigin + 16});
				this.hitboxRenderer.polygon(tileHitbox.getVertices());
				if(Intersector.overlapConvexPolygons(shipHitbox, tileHitbox)) {
					hitboxRenderer.end();
					return false;
				}
			}
		}
		hitboxRenderer.end();
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
					if(Utils.genRandomInt(1, this.waveFreq) == 1) {
						tile = new StaticTiledMapTile(waterTextures.get(Utils.genRandomInt(1, waterTextures.size - 1)));
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
}
