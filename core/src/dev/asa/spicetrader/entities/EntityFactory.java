package dev.asa.spicetrader.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import dev.asa.spicetrader.Utils;
import dev.asa.spicetrader.items.ItemFactory;
import dev.asa.spicetrader.entities.Merchant;
import dev.asa.spicetrader.entities.PirateVillage;
import dev.asa.spicetrader.entities.Shop;
import dev.asa.spicetrader.entities.Village;
import dev.asa.spicetrader.map.SpiceTraderMap;
import dev.asa.spicetrader.entities.Player;

import static com.badlogic.gdx.Gdx.files;

public class EntityFactory {
	
	private SpiceTraderMap map;
	private TextureAtlas atlas;
	private List<LandEntityLocation> landEntityLocations;
	private List<LandEntityLocation> takenLandEntityLocations;
	private Vector2 screenCenter;
	private float maxDist;
	private float minDist;
	private ItemFactory itemFactory;

	private List<String> names;
	Random rand;
	
	public EntityFactory(SpiceTraderMap map, TextureAtlas atlas, Vector2 screenCenter, ItemFactory itemFactory) {
		this.map = map;
		this.atlas = atlas;
		this.screenCenter = screenCenter;
		this.itemFactory = itemFactory;
		landEntityLocations = getValidVillageLocations();
		takenLandEntityLocations = new ArrayList<>();
		calcMinMaxDist();
		calcLocationTiers();

		names = new ArrayList<>();
		rand = new Random();
		loadNames();
	}

	private void loadNames() {
		FileHandle handle = files.internal("names.txt");
		String text = handle.readString();
		String wordsArray[] = text.split("\\r?\\n");
		for(String word : wordsArray) {
			names.add(word);
		}
	}

	private void calcMinMaxDist() {
		//calculate min/max distances for villages
		maxDist = 0;
		minDist = 1000;
		for(LandEntityLocation v : landEntityLocations) {
			if(v.distFromCenter < minDist)
				minDist = v.distFromCenter;
			if(v.distFromCenter > maxDist)
				maxDist = v.distFromCenter;
		}
	}

	// land entities either have tier 1, 2, or 3, indicating their distance from center.
	private void calcLocationTiers() {
		float tier1Cutoff = maxDist / 3;
		float tier2Cutoff = tier1Cutoff * 2;
		for(LandEntityLocation v : landEntityLocations) {
			if(v.distFromCenter < tier1Cutoff)
				v.tier = 1;
			else if(v.distFromCenter < tier2Cutoff)
				v.tier = 2;
			else
				v.tier = 3;
		}
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

		player.addToCargo(itemFactory.getGinger());
		player.addToCargo(itemFactory.getGinger());
		player.addToCargo(itemFactory.getGinger());

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
		List<Village> villages = new ArrayList<>();
		List<LandEntityLocation> selectedLocations = selectLandEntityLocations(villageRatio);
		for(LandEntityLocation location : selectedLocations) {
			villages.add(makeVillage(location));
		}
		
		System.out.println("Generated " + villages.size() + " villages.");

		return villages;
	}

	//creates 1/ratio the max number of remaining villages
	public List<Merchant> createMerchants(int merchantRatio, int minDistFromVillages) {
		//ensure merchants dont spawn right next to villages
		List<LandEntityLocation> locationsCloseToVillages = removeLocationsCloseToFriendlies(minDistFromVillages);

		List<Merchant> merchants = new ArrayList<Merchant>();
		List<LandEntityLocation> selectedLocations = selectLandEntityLocations(merchantRatio);
		for(LandEntityLocation location : selectedLocations) {
			merchants.add(makeMerchant(location));
		}

		landEntityLocations.addAll(locationsCloseToVillages);

		System.out.println("Generated " + merchants.size() + " merchants.");
		return merchants;
	}

	public List<Shop> createShops(int shopRatio) {
		List<Shop> shops = new ArrayList<>();
		List<LandEntityLocation> selectedLocations = selectLandEntityLocations(shopRatio);
		for(LandEntityLocation location : selectedLocations) {
			shops.add(makeShop(location));
		}

		System.out.println("Generated " + shops.size() + " shops.");
		return shops;
	}

	private List<LandEntityLocation> selectLandEntityLocations(int ratio) {
		List<LandEntityLocation> selected = new ArrayList<>();
		int maxNum = landEntityLocations.size() / ratio;
		for(int i = 0; i < maxNum; i++) {
			LandEntityLocation nextLocation = landEntityLocations.get(Utils.randInt(0, landEntityLocations.size() - 1));
			selected.add(nextLocation);
			landEntityLocations.remove(nextLocation);
			takenLandEntityLocations.add(nextLocation);
		}
		return selected;
	}
	
	//fills in remaining village locations with pirate villages, more frequent the farther from center.
	public List<PirateVillage> createPirateVillages(float minProb, float maxProb, int minDistFromFriendlies) {
		List<PirateVillage> pirateVillages = new ArrayList<PirateVillage>();

		//remove locations that are too close to villages/merchants/shops
		removeLocationsCloseToFriendlies(minDistFromFriendlies);

		int count = 0;
		//for each location, scale it's distance from center into a probability of a pirate village forming there. 
		for(LandEntityLocation villageLoc : landEntityLocations) {
			float probability = Utils.scaleToRange(villageLoc.distFromCenter, minDist, maxDist, minProb, maxProb); 
			if(Math.random() < probability) {
				count++;
				pirateVillages.add(makePirateVillage(villageLoc));
			}
		}

		System.out.println("Generated " + count + " pirate bases.");
		return pirateVillages;
	}

	//VILLAGE HELPER METHODS
	
	//returns list of possible village locations: 2x2 square of pure grass with atleast one potential dock location
	private List<LandEntityLocation> getValidVillageLocations() {
		List<LandEntityLocation> validLandEntityLocations = new ArrayList<LandEntityLocation>();
		
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
						
						LandEntityLocation l = new LandEntityLocation(new Vector2(col, row), dockTile, spawnLocation, screenCenter);
						validLandEntityLocations.add(l);
						usedTiles.add(Arrays.asList(col, row));
						usedTiles.add(Arrays.asList(col + 1, row));
						usedTiles.add(Arrays.asList(col, row + 1));
						usedTiles.add(Arrays.asList(col + 1, row + 1));
					}
				}
			}
		}
		return validLandEntityLocations;
	}

	private List<LandEntityLocation> removeLocationsCloseToFriendlies(int minDist) {
		ArrayList<LandEntityLocation> locationsToRemove = new ArrayList<>();
		for(LandEntityLocation loc : landEntityLocations) {
			for(LandEntityLocation friendly : takenLandEntityLocations) {
				float dist = loc.tileOrigin.dst(friendly.tileOrigin);
				if(dist < minDist)
					locationsToRemove.add(loc);
			}
		}
		landEntityLocations.removeAll(locationsToRemove);
		return locationsToRemove;
	}
	
	private Village makeVillage(LandEntityLocation location) {
		//pick a random village texture for sprite
		int numVillageSprites = atlas.findRegions("village/village").size;
		Sprite s = atlas.createSprite("village/village", Utils.randInt(0, numVillageSprites - 1));

		Vector2 pos = makeVillagePos(location, s);
		Polygon dockHitbox = makeDockHitbox(location);
		String name = getRandName() + " Village";
		
		//return
		return new Village(name, pos, s, location, dockHitbox, itemFactory);
	}

	private Merchant makeMerchant(LandEntityLocation location) {
		Sprite s = atlas.createSprite("village/merchant");
		Vector2 pos = makeVillagePos(location, s);
		Polygon dockHitbox = makeDockHitbox(location);
		String name = "The merchant " + getRandName();
		return new Merchant(name, pos, s, location, dockHitbox, itemFactory);
	}

	private Shop makeShop(LandEntityLocation location) {
		Sprite s = atlas.createSprite("village/shop");
		Vector2 pos = makeVillagePos(location, s);
		Polygon dockHitbox = makeDockHitbox(location);
		String name = getRandName() + "'s shop";
		return new Shop(name, pos, s, location, dockHitbox, itemFactory);
	}
	
	private PirateVillage makePirateVillage(LandEntityLocation location) {
		//pick a random village texture for sprite
		int numVillageSprites = atlas.findRegions("village/pirate_village").size;
		Sprite s = atlas.createSprite("village/pirate_village", Utils.randInt(0, numVillageSprites - 1));

		Vector2 pos = makeVillagePos(location, s);
		Polygon dockHitbox = makeDockHitbox(location);
		Sprite pirateSprite = atlas.createSprite("ships/pirate");
		
		//return
		return new PirateVillage("", pos, s, location, dockHitbox, map, pirateSprite);
	}

	private String getRandName() {
		int upperbound = names.size();
		return names.get(rand.nextInt(upperbound));
	}

	//generate position so that sprite is centered on 2x2 square.
	private Vector2 makeVillagePos(LandEntityLocation location, Sprite s) {
		Vector2 pixelCenter = map.getPixelCoordsFromTile(location.tileOrigin.add(1, 1));
		Vector2 pos = new Vector2(pixelCenter.x - s.getWidth()/2, pixelCenter.y - s.getHeight()/2);
		int roundPosX = (int)pos.x;
		int roundPosY = (int)pos.y;
		pos.x = (float)roundPosX;
		pos.y = (float)roundPosY;
		return pos;
	}

	private Polygon makeDockHitbox(LandEntityLocation location) {
		Vector2 dockPixelOrigin = map.getPixelCoordsFromTile(location.dockTile);
		int offset = 2;
		Vector2 tileSize = map.getTileSize();
		float[] dockHitboxVert = {	dockPixelOrigin.x - offset, dockPixelOrigin.y - offset,
				dockPixelOrigin.x + tileSize.x + offset, dockPixelOrigin.y - offset,
				dockPixelOrigin.x + tileSize.x + offset, dockPixelOrigin.y + tileSize.y + offset,
				dockPixelOrigin.x - offset, dockPixelOrigin.y + tileSize.y + offset};
		Polygon dockHitbox = new Polygon(dockHitboxVert);
		return dockHitbox;
	}
	
	public class LandEntityLocation {
		//all variables are tilewise
		//note: village takes up the 2x2 square with the bottom left tile on the "location"
		public Vector2 tileOrigin;
		public Vector2 dockTile;
		public Vector2 spawnLocation;//this is the spot where pirates spawn for pirate village. it is useful to ensure this tile exists for all villages so we don't get inaccessible villages
		public float distFromCenter;
		public int tier;
		
		public LandEntityLocation(Vector2 tileOrigin, Vector2 dockTile, Vector2 spawnLocation, Vector2 screenCenter) {
			this.tileOrigin = tileOrigin;
			this.dockTile = dockTile;
			this.spawnLocation = spawnLocation;
			
			//calc dist from center
			distFromCenter = getPixelCenter().dst(screenCenter);
		}

		public Vector2 getPixelCenter() {
			return map.getPixelCoordsFromTile(tileOrigin).add(16, 16);
		}
	}
}
