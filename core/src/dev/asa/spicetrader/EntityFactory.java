package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EntityFactory {
	
	public static Entity createEntity(String entityType, TextureAtlas atlas, SpiceTraderMap map, Vector2 screenCenter) {
		if(entityType.equals("Player")) {
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
		
		//temporarily generates pirate in random valid location
		//in the future, pirates will be spawned by pirate bases
		else if(entityType.contentEquals("Pirate")) {
			Sprite pirateSprite = atlas.createSprite("ships/pirate");
			Pirate pirate = new Pirate(Utils.getRandShipPos(pirateSprite, map), pirateSprite, map, 0);
			while(!map.validShipPosition(pirate)) {
				pirate.setPosition(Utils.getRandShipPos(pirateSprite, map));
			}
			
			return pirate;
		}
		
		return null;
	}
}
