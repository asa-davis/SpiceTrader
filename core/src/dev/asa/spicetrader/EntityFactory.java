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
	
	public EntityFactory(TextureAtlas atlas, SpiceTraderMap map, Vector2 screenCenter) {
		this.screenCenter = screenCenter;
		this.atlas = atlas;
		this.map = map;
	}
	
	public Player getPlayer() {
		//get sprites for player object
		Array<AtlasRegion> playerSpriteTextures = this.atlas.findRegions("ships/player");
		Sprite[] playerSprites = new Sprite[playerSpriteTextures.size];
		for(int i = 0; i < playerSpriteTextures.size; i++) 
			playerSprites[i] = new Sprite(playerSpriteTextures.get(i));
		
		//calculate player start position
		Vector2 playerStartPos = new Vector2(screenCenter.x - (playerSprites[0].getWidth() / 2), screenCenter.y - (playerSprites[0].getHeight() / 2));
		Sprite cannonBallSprite = atlas.createSprite("ships/cannon_ball");
		Player player = new Player(playerStartPos, playerSprites, cannonBallSprite, map, 3, 3, 0);
		
		return player;
	}
	
	//temporary method for testing - eventually the pirate villages will generate pirates
	//adds pirates randomly to the map
	public List<Pirate> getRandomPirates(int numPirates) {
		List<Pirate> pirates = new ArrayList<Pirate>();
		for(int i = 0; i < numPirates; i++) {
			Sprite pirateSprite = atlas.createSprite("ships/pirate");
			Pirate p = new Pirate(this.getRandShipPos(pirateSprite), pirateSprite, this.map, 1, 1, 0);
			while(!this.map.validShipPosition(p, false)) {
				p.setPosition(this.getRandShipPos(pirateSprite));
			}
			pirates.add(p);
		}
		
		return pirates;
	}
	
	private Vector2 getRandShipPos(Sprite sprite) {
		float xPos = (float) Utils.randInt(0, (int) (this.map.getSize().x - sprite.getWidth()));
		float yPos = (float) Utils.randInt(0, (int) (this.map.getSize().y - sprite.getHeight()));
		return new Vector2(xPos, yPos);
	}
}
