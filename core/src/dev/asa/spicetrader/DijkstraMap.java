package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DijkstraMap {
	
	//creates a "dijkstra" map representing the distance from the goal for every tile
	//algorithm taken from here: http://www.roguebasin.com/index.php?title=The_Incredible_Power_of_Dijkstra_Maps
	
	private int windowSize;
	private SpiceTraderMap map;
	
	//the col and row on actual map of the bottom left dijkstra tile and player
	private Vector2 originMapPos;
	private Vector2 goalMapPos;
	
	//holds the values for each tile in the map
	int[][] dijkstraMap;
	
	public DijkstraMap(int windowSize, SpiceTraderMap map) {
		//window size must be odd so that goal can be in very center
		if(windowSize % 2 != 0)
			this.windowSize = windowSize;
		else
			this.windowSize = windowSize - 1;
		
		this.map = map;
		
		//instantiate map matrix
		dijkstraMap = new int[windowSize][windowSize];
	}
	
	//checks if a given pos is inside the dijkstra map
	public boolean inRange(Vector2 pos) {
		//turn start pos into tile coords
		int[] tilePos = map.getTileCoordsFromPixels(pos);
		
		//check if position is inside window - if not, return empty path
		if(tilePos[0] < originMapPos.x || tilePos[0] >= originMapPos.x + windowSize) 
			return false;
		
		if(tilePos[1] < originMapPos.y || tilePos[1] >= originMapPos.y + windowSize) 
			return false;
		
		return true;
	}

	//returns true only if a given pos is in range, but within 1 tile of being out of range
	public boolean almostOutOfRange(Vector2 pos) {
		//turn start pos into tile coords
		int[] tilePos = map.getTileCoordsFromPixels(pos);

		//check if position is inside a 16 pixel wide strip bordering the inside of the map range
		if(tilePos[0] > originMapPos.x && tilePos[0] <= originMapPos.x + 16)
			return false;

		if(tilePos[0] < originMapPos.x + windowSize && tilePos[0] >= originMapPos.x + windowSize - 16)
			return false;

		if(tilePos[1] > originMapPos.y && tilePos[1] <= originMapPos.y + 16)
			return false;

		if(tilePos[1] < originMapPos.y + windowSize && tilePos[1] >= originMapPos.y + windowSize - 16)
			return false;

		return true;
	}

	//calculates map for given pixel destination
	public void calcDijkstraMapToPixelCoords(Vector2 goalPixelPos) {
		Vector2 goalTilePos = new Vector2(map.getTileCoordsFromPixels(goalPixelPos)[0], map.getTileCoordsFromPixels(goalPixelPos)[1]);
		calcDijkstraMapToTile(goalTilePos);
	}
	
	//calculates map for given tile destination
	public void calcDijkstraMapToTile(Vector2 newGoalTilePos) {
		//0. check if goal has moved since last calculated. if it hasn't dont do anything to map.
		if(newGoalTilePos.equals(goalMapPos))
			return;
	
		//1a. if goal has changed tiles, we continue with algorithm by calculating window origin. note: goal always in center
		goalMapPos = newGoalTilePos;
		originMapPos = new Vector2(goalMapPos);
		originMapPos.add(-1 * (windowSize/2), -1 * (windowSize/2));
		
		//1b. fill map with maximum value except for at goal position (center), where value = 0
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
					int actualCol = (int) (col + originMapPos.x);
					int actualRow = (int) (row + originMapPos.y);

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
	
	public List<Vector2> getPathToGoal(Vector2 hitCenter) {
		//start list
		List<Vector2> path = new ArrayList<Vector2>();
		
		boolean done = false;
		
		Vector2 curr = hitCenter;
		Vector2 next = null;
		
		while(!done) {
			next = getNextMove(curr);
			if(next != null && !next.equals(curr)) {
				path.add(curr);
				curr = next;
			}
			else
				done = true;
		}
		
		return path;
	}
	
	//returns the pixel coords for the next tile to travel to given a pixel position
	public Vector2 getNextMove(Vector2 startPos) {
		//check for out of range
		if(!inRange(startPos))
			return null;
		
		//turn start pos into tile coords
		int[] currPos = map.getTileCoordsFromPixels(startPos);
		int[] bestMove = getNextMove(currPos);
		
		return map.getPixelCoordsFromTile(bestMove).add(8, 8);
	}
	
	//return map coords for the next tile to travel to given a map position
	private int[] getNextMove(int[] currPos) {
		//change position into coordinates relative to window
		toDijkstraWindowCoords(currPos);
		
		//get value at current location
		int currVal = dijkstraMap[currPos[1]][currPos[0]];
		
		//check if pos is already at goal
		if(currVal == 0)
			return currPos;
		
		//otherwise, find lowest value neighbor square
		List<int[]> neighbors = Utils.getNeighborCoords(currPos[0], currPos[1], windowSize, windowSize, false, true);
		int lowestVal = currVal;
		int[] lowestPos = currPos;
		
		//iterate through neighbors and look for tiles with lower weights than current
		for(int[] neighborPos : neighbors) {
			if(neighborPos != null) {
				int neighborVal = dijkstraMap[neighborPos[1]][neighborPos[0]];
				if(neighborVal < lowestVal && validMove(currPos, neighborPos)) {
					lowestPos = neighborPos;
					lowestVal = neighborVal;
				}
			}
		}
		
		//return lowest value moveable square - must turn back into map coords and then into pixel coords
		toMapCoords(lowestPos);
		return lowestPos;
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
		tile[0] += originMapPos.x;
		tile[1] += originMapPos.y;
	}
	
	private void toDijkstraWindowCoords(int[] tile) {
		tile[0] -= originMapPos.x;
		tile[1] -= originMapPos.y;
	}
	
	//moved this from pirate code to here in case we need it again
	private void drawCurrPath(ShapeRenderer renderer, List<Vector2> currPath) {
		if(currPath.size() > 1)
			renderer.circle(currPath.get(1).x, currPath.get(1).y, 1);
		
		Vector2 currPoint;
		Vector2 nextPoint;
		for(int i = 1; i < currPath.size(); i++) {
			currPoint = currPath.get(i - 1);
			nextPoint = currPath.get(i);
			renderer.line(currPoint, nextPoint);
			currPoint = nextPoint;
		}
	}
}
