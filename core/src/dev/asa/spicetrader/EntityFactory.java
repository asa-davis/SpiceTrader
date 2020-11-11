package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EntityFactory {

	TextureAtlas atlas;
	SpiceTraderMap map;
	Vector2 screenCenter;
	List<Entity> allEntities;
	
	public EntityFactory(TextureAtlas atlas, SpiceTraderMap map, Vector2 screenCenter, List<Entity> allEntities) {
		this.screenCenter = screenCenter;
		this.atlas = atlas;
		this.map = map;
		this.allEntities = allEntities;
	}
	
	public Player getPlayer() {
		//get sprites for player object
		Array<AtlasRegion> playerSpriteTextures = this.atlas.findRegions("ships/player");
		Sprite[] playerSprites = new Sprite[playerSpriteTextures.size];
		for(int i = 0; i < playerSpriteTextures.size; i++) 
			playerSprites[i] = new Sprite(playerSpriteTextures.get(i));
		
		//calculate player start position
		Vector2 playerStartPos = new Vector2(screenCenter.x - (playerSprites[0].getWidth() / 2), screenCenter.y - (playerSprites[0].getHeight() / 2));
		
		Player player = new Player(playerStartPos, playerSprites, map, 5, 2, 0);
	
		this.allEntities.add(player);
		
		return player;
	}
	
	//temporary method for testing - eventually the pirate villages will generate pirates
	//adds pirates randomly to the map
	public List<Pirate> addPiratesRandomly(int numPirates) {
		List<Pirate> pirates = new ArrayList<Pirate>();
		for(int i = 0; i < numPirates; i++) {
			Sprite pirateSprite = atlas.createSprite("ships/pirate");
			Pirate p = new Pirate(this.getRandShipPos(pirateSprite), pirateSprite, this.map, 1, 1, 0);
			while(!this.map.validShipPosition(p)) {
				p.setPosition(this.getRandShipPos(pirateSprite));
			}
			pirates.add(p);
		}
		this.allEntities.addAll(pirates);
		return pirates;
	}
	
	private Vector2 getRandShipPos(Sprite sprite) {
		float xPos = (float) Utils.genRandomInt(0, (int) (this.map.getSize().x - sprite.getWidth()));
		float yPos = (float) Utils.genRandomInt(0, (int) (this.map.getSize().y - sprite.getHeight()));
		return new Vector2(xPos, yPos);
	}
}
