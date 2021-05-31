package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EntityFactory {
	
	private SpiceTraderMap map;
	private TextureAtlas atlas;
	private List<VillageLocation> villageLocations;
	Vector2 screenCenter;
	
	public EntityFactory(SpiceTraderMap map, TextureAtlas atlas, Vector2 screenCenter) {
		this.map = map;
		this.atlas = atlas;
		this.screenCenter = screenCenter;
		villageLocations = getValidVillageLocations();
	}
	
	public Player createPlayer() {
		//get sprites for player object
		Array<AtlasRegion> playerSpriteTextures = atlas.findRegions("ships/player");
		Sprite[] playerSprites = new Sprite[playerSpriteTextures.size];
		for(int i = 0; i < playerSpriteTextures.size; i++) 
			playerSprites[i] = new Sprite(playerSpriteTextures.get(i));
		
		//calculate player start position
		Vector2 playerStartPos = new Vector2(screenCenter.x - (playerSprites[0].getWidth() / 2) + 3, screenCenter.y - (playerSprites[0].getHeight() / 2) + 3);
		Sprite cannonBallSprite = atlas.createSprite("ships/cannon_ball");
		Player player = new Player(playerStartPos, playerSprites, cannonBallSprite, map);
		
		Item ginger = new Item("Ginger", atlas.findRegion("items/ginger"));
		Item peppercorn = new Item("Peppercorn", atlas.findRegion("items/peppercorn"));
		Item cinnamon = new Item("Cinnamon", atlas.findRegion("items/cinnamon"));
		Item cloves = new Item("Cloves", atlas.findRegion("items/cloves"));
		Item nutmeg = new Item("Nutmeg", atlas.findRegion("items/nutmeg"));
		
		player.addToCargo(ginger);
		player.addToCargo(peppercorn);
		player.addToCargo(cinnamon);
		player.addToCargo(cloves);
		player.addToCargo(nutmeg);
		
		player.removeFromCargo(player.getItemFromCargo(1));
		
		return player;
	}
	
	
	//DEPRECATED
	//public Pirate createRandPirate() {
	//	Sprite pirateSprite = atlas.createSprite("ships/pirate");
	//	Pirate pirate = new Pirate(Utils.getRandShipPos(pirateSprite, map), pirateSprite, map, 0);
	//	while(!map.validShipPosition(pirate)) {
	//		pirate.setPosition(Utils.getRandShipPos(pirateSprite, map));
	//	}
	//	
	//	return pirate;
	//}
	
	//creates 1/ratio the max number of villages
	public List<Village> createVillages(int villageRatio) {
		
		List<Village> villages = new ArrayList<Village>();
		
		int maxNumVillages = villageLocations.size();
		for(int i = 0; i < maxNumVillages / villageRatio; i++) {
			VillageLocation nextLocation = villageLocations.get(Utils.randInt(0, villageLocations.size() - 1));
			villages.add(makeVillage(nextLocation));
			villageLocations.remove(nextLocation);
		}
		
		System.out.println("Generated " + maxNumVillages / villageRatio + " villages.");
		
		return villages;
	}
	
	//fills in remaining village locations with pirate villages, more frequent the farther from center.
	public List<PirateVillage> createPirateVillages(float minProb, float maxProb) {
		List<PirateVillage> pirateVillages = new ArrayList<PirateVillage>();
		
		//calculate min/max distances for villages
		float maxDist = 0;
		float minDist = 1000;
		for(VillageLocation v : villageLocations) {
			if(v.distFromCenter < minDist)
				minDist = v.distFromCenter;
			if(v.distFromCenter > maxDist)
				maxDist = v.distFromCenter;
		}
		
		//for each location, scale it's distance from center into a probability of a pirate village forming there. 
		for(VillageLocation villageLoc : villageLocations) {
			float probability = Utils.scaleToRange(villageLoc.distFromCenter, minDist, maxDist, minProb, maxProb); 
			if(Math.random() < probability)
				pirateVillages.add(makePirateVillage(villageLoc));
		}
		
		return pirateVillages;
	}

	//VILLAGE HELPER METHODS
	
	//returns list of possible village locations: 2x2 square of pure grass with atleast one potential dock location
	private List<VillageLocation> getValidVillageLocations() {
		List<VillageLocation> validVillageLocations = new ArrayList<VillageLocation>();
		
		//use the bitmask map to check for pure grass with valid dock locations. skip every other row/col so locations never overlap.
		List<List<Integer>> usedTiles = new ArrayList<List<Integer>>();
		int[][] bmMap = map.getBitmaskMap();
		for(int row = 0; row < bmMap.length; row++) {
			for(int col = 0; col < bmMap[0].length; col++) {
				
				//check for 2x2 pure grass square that hasn't been used before
				boolean occupied = false;
				if(usedTiles.contains(Arrays.asList(col, row)) || usedTiles.contains(Arrays.asList(col + 1, row)) || usedTiles.contains(Arrays.asList(col, row + 1)) || usedTiles.contains(Arrays.asList(col + 1, row + 1)))
					occupied = true;
				if(!occupied && bmMap[row][col] == 255 && bmMap[row + 1][col] == 255 && bmMap[row][col + 1] == 255 && bmMap[row + 1][col + 1] == 255) {
					//check all 8 possible dock locations (adjacent non-diagonal tiles to 2x2 square)
					//if they are a straight beach tile, add them to list. 
					//cleaner way to do this?? loops?
					List<Vector2> dockLocations = new ArrayList<Vector2>();
					List<Vector2> spawnLocations = new ArrayList<Vector2>();
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
					
					//check each dock and if it doesnt have a valid spawn location next to it, remove it.
					List<Vector2> badDocks = new ArrayList<Vector2>();
					for(Vector2 dockLocation : dockLocations) {
						boolean goodDock = false;
						
						List<int[]> dockNeighbors = Utils.getNeighborCoords((int)dockLocation.x, (int)dockLocation.y, (int)map.getSizeTiles().x, (int)map.getSizeTiles().y, false, false);
						for(int[] dockNeighbor : dockNeighbors) 
							if(dockNeighbor != null && map.getTileId(dockNeighbor) == 0) {
								goodDock = true;
								break;
							}
						
						if(!goodDock)
							badDocks.add(dockLocation);
					}
					dockLocations.removeAll(badDocks);
					
					//if a village has atleast one dock, check if it has valid spawn. if so, add it. 
					if(dockLocations.size() > 0) {
						Vector2 dockTile = dockLocations.get(Utils.randInt(0, dockLocations.size() - 1));
						Vector2 spawnLocation = null;
						List<int[]> dockNeighbors = Utils.getNeighborCoords((int) dockTile.x, (int) dockTile.y, (int)map.getSizeTiles().x, (int)map.getSizeTiles().y, false, false);
						for(int[] dockNeighbor : dockNeighbors) {
							if(dockNeighbor != null && map.getTileId(dockNeighbor) == 0) {
								spawnLocation = new Vector2(map.getPixelCoordsFromTile(dockNeighbor).add(1, 1));
								break;
							}
						}
						
						VillageLocation l = new VillageLocation(new Vector2(col, row), dockTile, spawnLocation, screenCenter);
						validVillageLocations.add(l);
						usedTiles.add(Arrays.asList(col, row));
						usedTiles.add(Arrays.asList(col + 1, row));
						usedTiles.add(Arrays.asList(col, row + 1));
						usedTiles.add(Arrays.asList(col + 1, row + 1));
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
		
		//construct dock hitbox
		Vector2 dockPixelOrigin = map.getPixelCoordsFromTile(location.dockTile);
		int offset = 2;
		Vector2 tileSize = map.getTileSize();
		float[] dockHitboxVert = {	dockPixelOrigin.x - offset, dockPixelOrigin.y - offset, 
									dockPixelOrigin.x + tileSize.x + offset, dockPixelOrigin.y - offset, 
									dockPixelOrigin.x + tileSize.x + offset, dockPixelOrigin.y + tileSize.y + offset,
									dockPixelOrigin.x - offset, dockPixelOrigin.y + tileSize.y + offset};
		Polygon dockHitbox = new Polygon(dockHitboxVert);
		
		//return
		return new Village(pos, s, location.tileOrigin, location.dockTile, dockHitbox, location.distFromCenter);
	}
	
	private PirateVillage makePirateVillage(VillageLocation location) {
		//pick a random village texture for sprite
		int numVillageSprites = atlas.findRegions("village/pirate_village").size;
		Sprite s = atlas.createSprite("village/pirate_village", Utils.randInt(0, numVillageSprites - 1));
		
		//generate position so that sprite is centered on 2x2 square.
		Vector2 pixelCenter = map.getPixelCoordsFromTile(location.tileOrigin.add(1, 1));
		Vector2 pos = new Vector2(pixelCenter.x - s.getWidth()/2, pixelCenter.y - s.getHeight()/2);
		int roundPosX = (int)pos.x;
		int roundPosY = (int)pos.y;
		pos.x = (float)roundPosX;
		pos.y = (float)roundPosY;
		
		//construct dock hitbox
		Vector2 dockPixelOrigin = map.getPixelCoordsFromTile(location.dockTile);
		int offset = 2;
		Vector2 tileSize = map.getTileSize();
		float[] dockHitboxVert = {	dockPixelOrigin.x - offset, dockPixelOrigin.y - offset, 
									dockPixelOrigin.x + tileSize.x + offset, dockPixelOrigin.y - offset, 
									dockPixelOrigin.x + tileSize.x + offset, dockPixelOrigin.y + tileSize.y + offset,
									dockPixelOrigin.x - offset, dockPixelOrigin.y + tileSize.y + offset};
		Polygon dockHitbox = new Polygon(dockHitboxVert);
		
		//pirate sprite
		Sprite pirateSprite = atlas.createSprite("ships/pirate");
		
		//return
		return new PirateVillage(pos, s, location.tileOrigin, location.dockTile, dockHitbox, location.distFromCenter, map, pirateSprite, location.spawnLocation);
	}
	
	private class VillageLocation {
		//all variables are tilewise
		//note: village takes up the 2x2 square with the bottom left tile on the "location"
		private Vector2 tileOrigin;
		private Vector2 dockTile;
		private Vector2 spawnLocation;//this is the spot where pirates spawn for pirate village. it is useful to ensure this tile exists for all villages so we don't get inaccessible villages
		private float distFromCenter;
		
		public VillageLocation(Vector2 tileOrigin, Vector2 dockTile, Vector2 spawnLocation, Vector2 screenCenter) {
			this.tileOrigin = tileOrigin;
			this.dockTile = dockTile;
			this.spawnLocation = spawnLocation;
			
			//calc dist from center
			Vector2 pixelCenter = map.getPixelCoordsFromTile(new Vector2(tileOrigin).add(1, 1));
			float distX = Math.abs(pixelCenter.x - screenCenter.x);
			float distY = Math.abs(pixelCenter.y - screenCenter.y);
			distFromCenter = (float) Math.sqrt((distX * distX) + (distY * distY));
		}
	}
}
