package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class Utils {
	
	//inclusive
	public static int genRandomInt(int min, int max) {
		return (int) ((Math.random() * (max - min + 1)) + min);
	}
	
	//i made this function because i kept having to fetch all the neighbors for a tile and check if they were in bounds.
	//returns a list containing vectors representing the coords for neighboring tiles.
	//out of bounds tiles will be NULL
	//if includeCurrTile is true, the neighbors are ordered in the list like this:
	//	0|1|2
	//	-----
	//	3|4|5
	//	-----
	//	6|7|8
	//if includeCurrTile is false, they will be ordered like this:
	//	0|1|2
	//	-----
	//	3|x|4
	//	-----
	//	5|6|7
	public static List<Vector2> getNeighborCoords(int x, int y, int numCols, int numRows, boolean includeCurrTile) {
		List<Vector2> neighbors = new ArrayList<Vector2>();
		int neighborY;
		int neighborX;
		for(int yShift = 1; yShift >= -1; yShift--) {
			for(int xShift = -1; xShift <= 1; xShift++) {
				neighborY = y + yShift;
				neighborX = x + xShift;
				//check for out of bounds
				if(neighborY >= 0 && neighborY < numRows && neighborX >= 0 && neighborX < numCols) {
					neighbors.add(new Vector2(neighborX, neighborY));
				} else {
					neighbors.add(null);
				}
			}
		}
		if(!includeCurrTile)
			neighbors.remove(4);
		return neighbors;
	}
	
	public static float round(float val, int decimals) {
		return (float) (Math.round(val * Math.pow(10, decimals))/Math.pow(10, decimals));
	}
}
