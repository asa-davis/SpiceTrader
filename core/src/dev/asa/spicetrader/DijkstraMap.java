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
	
	public DijkstraMap(int windowSize, SpiceTraderMap map) {
		//window size must be odd so that player can be in very center
		if(windowSize % 2 != 0)
			this.windowSize = windowSize;
		else
			this.windowSize = windowSize - 1;
		
		
		this.map = map;
		
		//instantiate map matrix
		dijkstraMap = new int[windowSize][windowSize];
	}
	
	//creates a "dijkstra" map representing the distance from the player for every tile
	//algorithm taken from here: http://www.roguebasin.com/index.php?title=The_Incredible_Power_of_Dijkstra_Maps
	//this is nice because all pirates can share one copy of this map for chasing the player
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

					
					if(inBounds && map.getTileId(new int[] {actualCol, actualRow}) == 0) {
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
		toDijkstraWindowCoords(currPos);
		
		//generate path of tile coords
		List<int[]> tilePath = new ArrayList<int[]>();
		tilePath.add(currPos);
		tilePath = getPathToPlayerHelper(currPos, tilePath);
		
		//turn tile path back into coords relative to actual map
		for(int[] tile : tilePath) {
			toMapCoords(tile);
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
		
		//iterate through neighbors and look for tiles with lower weights than current
		boolean foundPath = false;
		for(int[] neighborPos : neighbors) {
			if(neighborPos != null) {
				int neighborVal = dijkstraMap[neighborPos[1]][neighborPos[0]];
				if(neighborVal < lowestVal && validMove(currPos, neighborPos)) {
					lowestPos = neighborPos;
					lowestVal = neighborVal;
					foundPath = true;
				}
			}
		}
		
		//check for no valid path forward - when no neighbors have lower path than current
		if(!foundPath) 
			return currPath;
		
		
		currPath.add(lowestPos);
		return getPathToPlayerHelper(lowestPos, currPath);
	}
	
	//AVOID DIAGONALS THAT CUT CORNERS ON LAND - THESE MAKE PIRATES GET STUCK
	//ex: If a pirate was on 5, the lowest neighbor would be 3. but this would get the pirate stuck. so when moving diagonally, we must check for shared land
	//
	//			1P12
	//			###3
	//			7654
	
	private boolean validMove(int[] curr, int[] next) {
		toMapCoords(next);
		toMapCoords(curr);
		
		//diag check
		if(curr[0] != next[0] && curr[1] != next[1]) { 
			int[] sharedNeighbor1 = {curr[0], next[1]};
			int[] sharedNeighbor2 = {next[0], curr[1]};
			//neighbor check
			if(map.getTileId(sharedNeighbor1) == 1 || map.getTileId(sharedNeighbor2) == 1) {
				toDijkstraWindowCoords(curr);
				toDijkstraWindowCoords(next);
				return false;
			}
		}
		toDijkstraWindowCoords(curr);
		toDijkstraWindowCoords(next);
		return true;
	}
	
	private void toMapCoords(int[] tile) {
		tile[0] += origin.x;
		tile[1] += origin.y;
	}
	
	private void toDijkstraWindowCoords(int[] tile) {
		tile[0] -= origin.x;
		tile[1] -= origin.y;
	}
}
