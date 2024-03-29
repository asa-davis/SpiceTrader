package dev.asa.spicetrader.map;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import dev.asa.spicetrader.Utils;

public class SpiceTraderMapGenerator {

	private TextureAtlas atlas;
	
	public SpiceTraderMapGenerator(TextureAtlas atlas) {
		this.atlas = atlas;
	}
	
	public SpiceTraderMap generateMap(int size, int tileWidth, int tileHeight, int smoothingIterations, int seaLevelOffset) throws Exception {
		//map generation workflow:
		//	0. verify map variables (ensure size is even number)
		//	1. set map variables
		//	2. generate tile id map of random islands
		//		a. randomize id map with values between 1-100
		//		b. smooth map n times by setting each tile to avg value of it's neighbors from previous iteration
		//		c. set all tiles with values below a certain threshold to water (id=0) and all tiles above to land (id=1)
		//		d. check if map is valid (has empty 4x4 square of water at center. If not, go back to a.
		//	3. add starting island/village TODO
		//	4. generate bitmask map showing which neighboring tiles are land for every tile
		//	5. generate libgdx TiledMap and MapRenderer
		
		//0
		if(size % 2 != 0) {
			throw new Exception(" *** MAP SIZE MUST BE EVEN ***\n");
		}
		
		//1
		int maxIterations = 10000;
		boolean validMapGenerated = false;
		int waveFreq = 100;
		SpiceTraderMap map = new SpiceTraderMap(size, size, tileWidth, tileHeight, waveFreq, atlas);
		
		
		//2
		int[][] tileIdMap = new int[size][size];
		int i = 0;
		while(!validMapGenerated) {
			randomizeTileIdMap(tileIdMap, 1, 100);
			
			tileIdMap = smoothTileIdMap(smoothingIterations, tileIdMap);

			thresholdTileIdMap(tileIdMap, seaLevelOffset);
			
			validMapGenerated = checkValidTileIdMap(tileIdMap);
			
			i++;
			if(i >= maxIterations) throw new Exception(" *** MAX ITERATIONS HIT - NO VALID MAP FOUND ***\n");
		}
		System.out.println(" *** VALID MAP FOUND ON ITERATION #" + i);
		map.setTileIdMap(tileIdMap);
		
		//3 
		//TODO add starting island/village under player
		
		//4
		int[][] neighborBitmaskMap = generateNeighborTileBitmaskMap(size, size, tileIdMap);
		map.setNeighborBitmaskMap(neighborBitmaskMap);
		
		//5
		TiledMap libgdxMap = generateLibgdxMap(size, size, tileWidth, tileHeight, neighborBitmaskMap, tileIdMap, waveFreq, atlas);
		map.setLibgdxMap(libgdxMap);
		
		return map;
	}
	
	//sets the tile id map to random values of min - max
	private static void randomizeTileIdMap(int[][] tileIdMap, int min, int max) {
		for(int y = 0; y < tileIdMap.length; y++) {
			for(int x = 0; x < tileIdMap[0].length; x++) {
				tileIdMap[y][x] = Utils.randInt(min, max);
			}
		}
	}
	
	//smoothes land masses into clumps representing islands
	private static int[][] smoothTileIdMap(int iterations, int[][] tileIdMap) {
		int numRows = tileIdMap.length;
		int numCols = tileIdMap[0].length;
		int[][] smoothedMap = new int[numRows][numCols];
		
		for(int y = 0; y < numRows; y++) {
			for(int x = 0; x < numCols; x++) {
				//sum and average neighbors
				int neighborSum = 0;
				int neighborCount = 0;
				List<int[]> neighbors = Utils.getNeighborCoords(x, y, numCols, numRows, false, true);
				for(int[] currNeighbor : neighbors) {
					if(currNeighbor != null) {
						neighborSum += tileIdMap[currNeighbor[1]][currNeighbor[0]];
						neighborCount++;
					}
				}
				
				int avg = neighborSum / neighborCount;
				smoothedMap[y][x] = avg;
			}
		}
		
		//recursive
		if(iterations <= 1)
			return smoothedMap;
		else
			return smoothTileIdMap(iterations - 1, smoothedMap);
	}
	
	//takes avg value of all cells and divides in half. the threshold is equal to this value + offset
	//cells above the threshold are land (id=1), cells below are water(id=0)
	private static void thresholdTileIdMap(int[][] tileIdMap, int offset) {
		int numRows = tileIdMap.length;
		int numCols = tileIdMap[0].length;
		int sum = 0;
		
		for(int y = 0; y < numRows; y++) {
			for(int x = 0; x < numCols; x++) {
				sum += tileIdMap[y][x];
			}
		}
		
		int threshold = sum / (numRows * numCols);
		threshold += offset;
		
		for(int y = 0; y < numRows; y++) {
			for(int x = 0; x < numCols; x++) {
				if(tileIdMap[y][x] <= threshold)
					tileIdMap[y][x] = 0;
				else
					tileIdMap[y][x] = 1;
			}
		}
	}
	
	//checks if there is a 4x4 square of water for starting area at center of map
	//starting are consists of a 3x2 island with home village and a dock with player next to it all surrounded by water
	private static boolean checkValidTileIdMap(int[][] tileIdMap) {
		int startingZoneSize = 4;//must be even
		Vector2 startingZoneOrigin = new Vector2((tileIdMap[0].length/2) - (startingZoneSize/2), (tileIdMap.length/2) - (startingZoneSize/2));
		for(int y = 0; y < startingZoneSize; y++) {
			for(int x = 0; x < startingZoneSize; x++) {
				if(tileIdMap[(int) (startingZoneOrigin.y + y)][(int) (startingZoneOrigin.x + x)] != 0)
					return false;
			}
		}
		return true;
	}
	
	//Generate a map of bitmasked values representing the presence of land on all 8 neighboring tiles for a given tile. 
	
	//		2^0|2^1|2^2
	//		-----------
	//		2^3| x |2^4
	//		-----------
	//	  	2^5|2^6|2^7
	
	//A bitmask for tile x would be calculate by adding up the values in all neighboring squares where land was present
	
	//Note: diagonal neighboring land will only be counted if it's two surrounding neighbors are also land. 
	//For example, the top right neighbor is only counted if both the top and the right neighbors are land too.
	//This is to avoid redundant values.
	//Algorithm taken from here: https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673
	
	private static int[][] generateNeighborTileBitmaskMap(int numRows, int numCols, int[][] tileIdMap) {
		int[][]	neighborBitmaskMap = new int[numRows][numCols];

		for(int y = 0; y < numRows; y++) {
			for(int x = 0; x < numCols; x++) {
				int bitmask = 0;
				int power = 0;
				//iterate through all neighbors, increasing our power each time
				//if neighbor is land, we add 2^power to our bitmask
				List<int[]> neighbors = Utils.getNeighborCoords(x, y, numCols, numRows, false, true);
				for(int[] currNeighbor : neighbors) {
					//check for out of bounds
					if(currNeighbor != null) {
						if(tileIdMap[currNeighbor[1]][currNeighbor[0]] == 1) 
							bitmask += Math.pow(2, power);
					}

					power++;
				}
				
				//redundancy check:
				//top left
				if((bitmask & 1) > 0) {
					if((bitmask & 8) == 0 || (bitmask & 2) == 0)
						bitmask -= 1;
				}
				//top right
				if((bitmask & 4) > 0) {
					if((bitmask & 16) == 0 || (bitmask & 2) == 0)
						bitmask -= 4;
				}
				//bottom left
				if((bitmask & 32) > 0) {
					if((bitmask & 8) == 0 || (bitmask & 64) == 0)
						bitmask -= 32;
				}
				//bottom right
				if((bitmask & 128) > 0) {
					if((bitmask & 16) == 0 || (bitmask & 64) == 0)
						bitmask -= 128;
				}
				
				
				neighborBitmaskMap[y][x] = bitmask;
			}
		}
		
		return neighborBitmaskMap;
	}
	
	//generate the libgdx TiledMap object from the neighbor bitmask map and tileId map
	public static TiledMap generateLibgdxMap(int numCols, int numRows, int tileWidth, int tileHeight, int[][] neighborBitmaskMap, int[][] tileIdMap, int waveFreq, TextureAtlas atlas) {
		TiledMap libgdxMap = new TiledMap();
		TiledMapTileLayer mapLayer = new TiledMapTileLayer(numCols, numRows, tileWidth, tileHeight);
		
		HashMap<Integer, Integer> beachBitmaskConverter = getBeachBitmaskConverter();
		HashMap<Integer, Integer> dockBitmaskConverter = getDockBitmaskConverter();
		Array<AtlasRegion> trees = atlas.findRegions("tile/tree");
		Array<AtlasRegion> water = atlas.findRegions("tile/water");
		Array<AtlasRegion> grass = atlas.findRegions("tile/grass");
		Array<AtlasRegion> dock = atlas.findRegions("tile/dock");
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
				StaticTiledMapTile tile;
				int treeFreq = 6;
				//water
				if(tileIdMap[row][col] == 0) {
					if(Utils.randInt(1, waveFreq) == 1) 
						tile = new StaticTiledMapTile(water.get(Utils.randInt(1, water.size - 1)));
					else 
						tile = new StaticTiledMapTile(water.get(0));	
				}
				//grass/beach
				else if(tileIdMap[row][col] == 1) {
					int bitmask = neighborBitmaskMap[row][col];
					int tileNum = beachBitmaskConverter.get(bitmask);
					//if pure grass tile, 1 in treeFreq chance to have a random tree tile
					if(tileNum == 0 && Utils.randInt(1, treeFreq) == 1)
						tile = new StaticTiledMapTile(trees.get(Utils.randInt(0, trees.size - 1)));	
					//otherwise we just use the appropriate grass tile
					else
						tile = new StaticTiledMapTile(grass.get(tileNum));
				}
				//grass behind village
				else if(tileIdMap[row][col] == 2) {
					tile = new StaticTiledMapTile(grass.get(0));
				}
				//dock
				else if(tileIdMap[row][col] == 3) {
					int bitmask = neighborBitmaskMap[row][col];
					int tileNum = dockBitmaskConverter.get(bitmask);
					tile = new StaticTiledMapTile(dock.get(tileNum));
				}
				else {
					tile = new StaticTiledMapTile(atlas.findRegion("tile/test_tile"));
				}
				
				cell.setTile(tile);
				mapLayer.setCell(col, row, cell);
			}
		}
		libgdxMap.getLayers().add(mapLayer);
		
		return libgdxMap;
	}
	
	//this hashmap takes a bitmask value as a key and returns the correct grass tile number
	private static HashMap<Integer, Integer> getBeachBitmaskConverter() {
		HashMap<Integer, Integer> converter = new HashMap<Integer, Integer>();
		int[] values = {255, 0, 2, 1, 8, 2, 10, 3, 11, 4, 16, 5, 18, 6, 22, 7, 24, 8, 26, 9, 27, 10, 30, 11, 31, 12, 64, 13, 66, 14, 72, 
						15, 74, 16, 75, 17, 80, 18, 82, 19, 86, 20, 88, 21, 90, 22, 91, 23, 94, 24, 95, 25, 104, 26, 106, 27, 107, 28, 120, 
						29, 122, 30, 123, 31, 126, 32, 127, 33, 208, 34, 210, 35, 214, 36, 216, 37, 218, 38, 219, 39, 222, 40, 223, 41, 248, 
						42, 250, 43, 251, 44, 254, 45, 0, 46};
		for(int i = 0; i < values.length; i += 2) {
			converter.put(values[i], values[i + 1]);
		}
		return converter;
	}
	
	private static HashMap<Integer, Integer> getDockBitmaskConverter() {
		HashMap<Integer, Integer> converter = new HashMap<Integer, Integer>();
		converter.put(31, 0);
		converter.put(107, 1);
		converter.put(214, 2);
		converter.put(248, 3);
		return converter;
	}
	
	private static void debugMap(int[][] map) {
		int numRows = map.length;
		int numCols = map[0].length;
		
		for(int y = 0; y < numRows; y++) {
			for(int x = 0; x < numCols; x++) {
				System.out.print(" " + map[y][x]);
			}
			System.out.println('\n');
		}
	}
}
