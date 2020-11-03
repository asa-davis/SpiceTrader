package dev.asa.spicetrader;

import java.util.List;

//holds the coordinates for a spot where a village could be added to the map
public class VillageLocation {
	private int row;
	private int col;
	private List<int[]> validDockLocations;// {row, col}
	
	public VillageLocation(int row, int col, List<int[]> validDockLocations) {
		this.row = row;
		this.col = col;
		this.validDockLocations = validDockLocations;
	}
	
	public int getRow() {
		return this.row;
	}
	public int getCol() {
		return this.col;
	}
}
