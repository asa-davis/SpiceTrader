package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class SpiceTraderMapGenerator {

	private TextureAtlas atlas;
	
	public SpiceTraderMapGenerator(TextureAtlas atlas) {
		this.atlas = atlas;
	}
	
	public SpiceTraderMap generateMap(int numCols, int numRows, int tileWidth, int tileHeight) {
		//map generation workflow:
		//	1. set map variables
		//	2. generate tile id map showing where land is
		//	3. generate bitmask map showing which neighboring tiles are land for every tile
		//	4. generate libgdx TiledMap and MapRenderer
		
		SpiceTraderMap map = new SpiceTraderMap(numCols, numRows, tileWidth, tileHeight);
		
		int[][] tileIdMap = generateRandomTileIdMap(numRows, numCols);
		map.setTileIdMap(tileIdMap);
		
		int[][] neighborBitmaskMap = generateNeighborTileBitmaskMap(numRows, numCols);
		map.setNeighborBitmaskMap(neighborBitmaskMap);
		
		TiledMap libgdxMap = generateLibgdxMap(numCols, numRows, tileWidth, tileHeight, neighborBitmaskMap, tileIdMap);
		map.setLibgdxMap(libgdxMap);
		
		return map;
	}
	
	//sets the tile id map to random values of 0/1
	private static int[][] generateRandomTileIdMap(int numRows, int numCols) {
		int[][] tileIdMap = new int[numRows][numCols];
		for(int y = 0; y < numRows; y++) {
			for(int x = 0; x < numCols; x++) {
				tileIdMap[y][x] = MainGame.genRandomInt(0, 1);
			}
		}
		return tileIdMap;
	}
	
	//generate a map of bitmasked values representing the presence of land on all 8 neighboring tiles for a given tile. 
	private static int[][] generateNeighborTileBitmaskMap(int numRows, int numCols) {
		int[][]	neighborBitmaskMap = new int[numRows][numCols];
		return neighborBitmaskMap;
	}
	
	//generate the libgdx TiledMap object from the neighbor bitmask map and tileId map
	private TiledMap generateLibgdxMap(int numCols, int numRows, int tileWidth, int tileHeight, int[][] neighborBitmaskMap, int[][] tileIdMap) {
		TiledMap libgdxMap = new TiledMap();
		
		TiledMapTileLayer mapLayer = new TiledMapTileLayer(numCols, numRows, tileWidth, tileHeight);
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
				StaticTiledMapTile tile;
				
				if(tileIdMap[row][col] == 0) 
					tile = new StaticTiledMapTile(this.atlas.findRegions("tile/water").get(0));
				else 
					tile = new StaticTiledMapTile(this.atlas.findRegions("tile/grass").get(0));
				
				cell.setTile(tile);
				mapLayer.setCell(col, row, cell);
			}
		}
		libgdxMap.getLayers().add(mapLayer);
		
		return libgdxMap;
	}
	
	public void addVillagesToMap(List<VillageLocation> villageLocations) {
		//when villages are to be added to game:
		//	1. pass list of village locations to map
		//	2. set dock tile to 100 + village id for all locations
		//	3. regenerate TiledMap and MapRenderer
	}
	
	public List<VillageLocation> getValidVillageLocations() {
		return new ArrayList<VillageLocation>();
	}
	
}
