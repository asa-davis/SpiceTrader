package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class DijkstraMap {
	private int windowSize;
	private SpiceTraderMap map;
	//the col and row on actual map of the bottom left dijkstra tile and player
	private Vector2 origin;
	private Vector2 playerTilePos;
	//holds the values for each tile in the map - 
	int[][] dijkstraMap;
	
	public DijkstraMap(int windowExtensionDist, SpiceTraderMap map) {
		//number of tiles the window extends left, right, up, down of player - ensures will always be odd sized, with player in exact center
		//window extension dist of 16 means a 33x33 window with player centered at tile [16, 16]
		this.windowSize = (windowExtensionDist * 2) + 1;
		this.map = map;
		
		//instantiate map matrix
		dijkstraMap = new int[windowSize][windowSize];
	}
	
	//creates a "dijkstra" map representing the distance from the player for every tile
	//algorithm taken from here: http://www.roguebasin.com/index.php?title=The_Incredible_Power_of_Dijkstra_Maps
	public void calcPlayerDistMap(Vector2 playerPixPos) {
		//0. calculate new player tile position on actual map. if unchanged, we dont do anything
		Vector2 newPlayerTilePos = new Vector2(map.getTileCoordsFromPixels(playerPixPos)[0], map.getTileCoordsFromPixels(playerPixPos)[1]);
		if(newPlayerTilePos.equals(playerTilePos))
			return;
	
		//1a. if player has changed tiles, we continue with algorithm by calculating window origin and saving current player tile pos
		playerTilePos = newPlayerTilePos;
		origin = new Vector2(playerTilePos);
		origin.add(-1 * (windowSize/2), -1 * (windowSize/2));
		
		//debug
		//System.out.println("Window origin: (" + origin.x + ", " + origin.y + ")");

		
		//1b. fill map with maximum value except for at player position (center), where value = 0
		for(int[] row : dijkstraMap) 
			Arrays.fill(row, windowSize * windowSize);
		dijkstraMap[windowSize/2][windowSize/2] = 0;
		
		
		//2. iterate through map, checking for tiles where the lowest value neighbor is more than 1 less than the tile value. 
		//	 for these tiles, set value to the neighbor value + 1. repeat until no more cases. ignore land tiles.
		boolean done = false;
		while(!done) {
			boolean tileChanged = false;
			
			for(int row = 0; row < windowSize; row++) {
				for(int col = 0; col < windowSize; col++) {
					//convert these coords to coords relative to actual map
					int actualCol = (int) (col + origin.x);
					int actualRow = (int) (row + origin.y);

					//only process ocean tiles and tiles in bounds
					boolean inBounds = true;
					if(actualRow < 0 || actualRow >= map.getSizeTiles().y) 
						inBounds = false;
					
					if(actualCol < 0 || actualCol >= map.getSizeTiles().x) 
						inBounds = false;

					
					if(inBounds && map.getTileIdMap()[actualRow][actualCol] == 0) {
						//collect current value and neighbor coords - no diags
						List<int[]> neighborCoords = Utils.getNeighborCoords(col, row, windowSize, windowSize, false, false);
						int currValue = dijkstraMap[row][col];
						
						//collect lowest neighbor value (ignoring neighbors with values greater than current)
						int lowestNeighborValue = currValue;
						for(int[] neighbor : neighborCoords) {
							if(neighbor != null) {
								int currNeighborValue = dijkstraMap[neighbor[1]][neighbor[0]];
								if(currNeighborValue < lowestNeighborValue) {
									lowestNeighborValue = currNeighborValue;
								}
							}
						}
						
						//check if more than 1 below current value and set current appropriately.
						if(lowestNeighborValue < currValue - 1) {
							tileChanged = true;
							dijkstraMap[row][col] = lowestNeighborValue + 1;
						}
					}
				}
			}
			
			if(!tileChanged) {
				done = true;
			}
		}
		
		//debug
		boolean debug = false;
		if(debug) {
			System.out.println("NEW MAP:");
			for(int row = windowSize - 1; row >= 0; row--) {
				for(int col = 0; col < windowSize; col++) {
					int val = dijkstraMap[row][col];
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
	
	//returns a set of points representing the center tiles of the path to the player
	public List<Vector2> getPathToPlayer(Vector2 startPos) {
		List<Vector2> path = new ArrayList<Vector2>();
		
		//turn start pos into tile coords
		int[] currPos = map.getTileCoordsFromPixels(startPos);
		
		//check if position is inside window - if not, return empty path
		if(currPos[0] < origin.x || currPos[0] >= origin.x + windowSize) 
			return path;
		
		if(currPos[1] < origin.y || currPos[1] >= origin.y + windowSize) 
			return path;
		
		//change position into coordinates relative to window
		currPos[0] -= origin.x;
		currPos[1] -= origin.y;
		
		//generate path of tile coords
		List<int[]> tilePath = new ArrayList<int[]>();
		tilePath.add(currPos);
		tilePath = getPathToPlayerHelper(currPos, tilePath);
		
		//turn tile path back into coords relative to actual map
		for(int[] tile : tilePath) {
			tile[0] += origin.x;
			tile[1] += origin.y;
		}
		
		//turn tile coords path into pixel path where each point is center of tile
		for(int[] tile : tilePath)
			path.add(map.getPixelCoordsFromTile(tile).add(new Vector2(map.getTileSize().x/2, map.getTileSize().y/2)));
		
		return path;
	}
	
	//recursive helper method - deals only with tile coords
	private List<int[]> getPathToPlayerHelper(int[] currPos, List<int[]> currPath) {
		int currVal = dijkstraMap[currPos[1]][currPos[0]];
		//if current position is on player tile, we are done
		if(currVal == 0) 
			return currPath;
		
		//otherwise, find lowest value neighbor square, set it to currPos, add it to currPath and continue
		List<int[]> neighbors = Utils.getNeighborCoords(currPos[0], currPos[1], windowSize, windowSize, false, true);
		int lowestVal = currVal;
		int[] lowestPos = currPos;
		
		for(int[] neighborPos : neighbors) {
			if(neighborPos != null) {
				int neighborVal = dijkstraMap[neighborPos[1]][neighborPos[0]];
				if(neighborVal < lowestVal) {
					lowestPos = neighborPos;
					lowestVal = neighborVal;
				}
			}
		}
		
		currPath.add(lowestPos);
		return getPathToPlayerHelper(lowestPos, currPath);
	}
}
