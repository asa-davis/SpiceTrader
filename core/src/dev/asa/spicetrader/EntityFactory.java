package dev.asa.spicetrader;

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
	
	public Player getPlayer(String playerTexturesPath) {
		//get sprites for player object
		Array<AtlasRegion> playerSpriteTextures = this.atlas.findRegions("ships/player");
		Sprite[] playerSprites = new Sprite[playerSpriteTextures.size];
		for(int i = 0; i < playerSpriteTextures.size; i++) 
			playerSprites[i] = new Sprite(playerSpriteTextures.get(i));
		//calculate player start position
		Vector2 playerStartPos = new Vector2(screenCenter.x - (playerSprites[0].getWidth() / 2), screenCenter.y - (playerSprites[0].getHeight() / 2));
		Player player = new Player(playerStartPos, playerSprites, map, 2, 2, 0);
		return player;
	}
}
