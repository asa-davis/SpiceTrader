package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

//TODO: make this a builder or generator class and have entFactory take care of instantiating it?

public class VillageFactory {
	private SpiceTraderMap map;
	private TextureAtlas atlas;
	private Vector2 tileSize;
	
	public VillageFactory(SpiceTraderMap map, TextureAtlas atlas) {
		this.map = map;
		this.atlas = atlas;
		this.tileSize = map.getTileSize();
	}
	
	//scans map for all possible village locations, and returns numVillages random villages
	public List<Village> getVillages(int numVillages) throws Exception {
		List<Village> villages = new ArrayList<Village>();
		List<VillageLocation> validVillageLocations = this.getValidVillageLocations();
		
		if(numVillages > validVillageLocations.size()) {
			throw new Exception("\n *** NOT ENOUGH VALID VILLAGE LOCATIONS ON THIS MAP. " + numVillages + " REQUESTED AND " + validVillageLocations.size() + " FOUND  ***\n");
		}
		
		for(int i = 0; i < numVillages; i++) {
			VillageLocation nextLocation = validVillageLocations.get(Utils.randInt(0, validVillageLocations.size() - 1));
			villages.add(makeVillage(nextLocation));
			validVillageLocations.remove(nextLocation);
		}
		
		return villages;
	}
	
	//returns list of possible village locations: 2x2 square of pure grass with atleast one potential dock location
	private List<VillageLocation> getValidVillageLocations() {
		List<VillageLocation> validVillageLocations = new ArrayList<VillageLocation>();
		
		//use the bitmask map to check for pure grass with valid dock locations. skip every other row/col so locations never overlap.
		int[][] bmMap = map.getBitmaskMap();
		for(int row = 0; row < bmMap.length; row += 3) {
			for(int col = 0; col < bmMap[0].length; col += 3) {
				
				//check for 2x2 pure grass square
				if(bmMap[row][col] == 255 && bmMap[row + 1][col] == 255 && bmMap[row][col + 1] == 255 && bmMap[row + 1][col + 1] == 255) {
					//check all 8 possible dock locations (adjacent non-diagonal tiles to 2x2 square)
					//if they are a straight beach tile, add them to list. 
					//cleaner way to do this?? loops?
					List<Vector2> dockLocations = new ArrayList<Vector2>();
					if(bmMap[row - 1][col] == 31 || bmMap[row - 1][col] == 107 || bmMap[row - 1][col] == 214 || bmMap[row - 1][col] == 248) {
						dockLocations.add(new Vector2(col, row - 1));
					}
					if(bmMap[row - 1][col + 1] == 31 || bmMap[row - 1][col + 1] == 107 || bmMap[row - 1][col + 1] == 214 || bmMap[row - 1][col + 1] == 248) {
						dockLocations.add(new Vector2(col + 1, row - 1));
					}
					if(bmMap[row][col - 1] == 31 || bmMap[row][col - 1] == 107 || bmMap[row][col - 1] == 214 || bmMap[row][col - 1] == 248) {
						dockLocations.add(new Vector2(col - 1, row));
					}
					if(bmMap[row][col + 2] == 31 || bmMap[row][col + 2] == 107 || bmMap[row][col + 2] == 214 || bmMap[row][col + 2] == 248) {
						dockLocations.add(new Vector2(col + 2, row));
					}
					if(bmMap[row + 1][col + 2] == 31 || bmMap[row + 1][col + 2] == 107 || bmMap[row + 1][col + 2] == 214 || bmMap[row + 1][col + 2] == 248) {
						dockLocations.add(new Vector2(col + 2, row + 1));
					}
					if(bmMap[row + 1][col - 1] == 31 || bmMap[row + 1][col - 1] == 107 || bmMap[row + 1][col - 1] == 214 || bmMap[row + 1][col - 1] == 248) {
						dockLocations.add(new Vector2(col - 1, row + 1));
					}
					if(bmMap[row + 2][col] == 31 || bmMap[row + 2][col] == 107 || bmMap[row + 2][col] == 214 || bmMap[row + 2][col] == 248) {
						dockLocations.add(new Vector2(col, row + 2));
					}					
					if(bmMap[row + 2][col + 1] == 31 || bmMap[row + 2][col + 1] == 107 || bmMap[row + 2][col + 1] == 214 || bmMap[row + 2][col + 1] == 248) {
						dockLocations.add(new Vector2(col + 1, row + 2));
					}
					
					//add village if at least one valid dock
					if(dockLocations.size() > 0) {
						VillageLocation l = new VillageLocation(new Vector2(col, row), dockLocations);
						validVillageLocations.add(l);
					}
				}
			}
		}
		return validVillageLocations;
	}
	
	private Village makeVillage(VillageLocation location) {
		//pick a random village texture for sprite
		int numVillageSprites = atlas.findRegions("village/village").size;
		Sprite s = atlas.createSprite("village/village", Utils.randInt(0, numVillageSprites - 1));
		
		//generate position so that sprite is centered on 2x2 square.
		Vector2 pixelCenter = map.getPixelCoordsFromTile(location.tileOrigin.add(1, 1));
		Vector2 pos = new Vector2(pixelCenter.x - s.getWidth()/2, pixelCenter.y - s.getHeight()/2);
		int roundPosX = (int)pos.x;
		int roundPosY = (int)pos.y;
		pos.x = (float)roundPosX;
		pos.y = (float)roundPosY;
		
		//choose a random dock position
		int numDockLocations = location.validDockLocations.size();
		Vector2 dockTile = location.validDockLocations.get(Utils.randInt(0, numDockLocations - 1));
		
		//construct dock hitbox
		Vector2 dockPixelOrigin = map.getPixelCoordsFromTile(dockTile);
		int offset = 2;
		float[] dockHitboxVert = {	dockPixelOrigin.x - offset, dockPixelOrigin.y - offset, 
									dockPixelOrigin.x + tileSize.x + offset, dockPixelOrigin.y - offset, 
									dockPixelOrigin.x + tileSize.x + offset, dockPixelOrigin.y + tileSize.y + offset,
									dockPixelOrigin.x - offset, dockPixelOrigin.y + tileSize.y + offset};
		Polygon dockHitbox = new Polygon(dockHitboxVert);
		
		//return
		return new Village(pos, s, location.tileOrigin, dockTile, dockHitbox);
	}
	
	private class VillageLocation {
		//all variables are tilewise
		//note: village takes up the 2x2 square with the bottom left tile on the "location"
		private Vector2 tileOrigin;
		private List<Vector2> validDockLocations;
		
		public VillageLocation(Vector2 tileOrigin, List<Vector2> validDockLocations) {
			this.tileOrigin = tileOrigin;
			this.validDockLocations = validDockLocations;
		}
	}

}
