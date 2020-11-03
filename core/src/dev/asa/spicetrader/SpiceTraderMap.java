package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

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
