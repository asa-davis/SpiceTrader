package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class SpiceTraderMap {
	private int numCols;
	private int numRows;
	private int tileWidth;
	private int tileHeight;
	
	//this holds the tileIds: 0 = water, 1 = land, 100+ = dock (tileId - 100 = dockId)
	private int[][] tileIdMap;
	//this tells us for a particular tile, which of the neighboring tiles in the 8 directions are land. 
	private int[][] neighborBitmaskMap;
	//libgdx objects holding the map in the form in which it can be rendered
	private TiledMap libgdxMap;
	private TiledMapRenderer mapRenderer;
	
	
	public SpiceTraderMap(int numCols, int numRows, int tileWidth, int tileHeight) {		
		this.numCols = numCols;
		this.numRows = numRows;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
	public void render(OrthographicCamera camera) {
		this.mapRenderer.setView(camera);
		this.mapRenderer.render();
	}
	
	//returns true if ship is not intersecting with shore
	//first we get the tile coords that the center of the hitbox is on
	//then we check the tile Ids of those coords and the 8 neighboring tiles
	//if any of these are land tiles (id != 0) we fetch their appropriate hitbox using the bitmask and check it for intersections with ship hitbox
	public boolean validShipPosition(Polygon shipHitbox, Vector2 shipCenter) {
		int[] currTile = this.getTileCoordsFromPixels(shipCenter);
		List<Vector2> neighbors = Utils.getNeighborCoords(currTile[0], currTile[1], numCols, numRows, true);
		return true;
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
}
