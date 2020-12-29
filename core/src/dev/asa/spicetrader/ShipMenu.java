package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;

public class ShipMenu extends Menu {
	
	private Player player;
	
	public ShipMenu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
		super(manager, pos, backgroundTexture, false);

		this.player = manager.getPlayer();
	}
	
	private void drawStats(SpriteBatch batch) {
		int[] stats = player.getStats();
		manager.getFont(0).setColor(Color.DARK_GRAY);
		String statString;
		
		for(int i = 0; i < 5; i++) {
			statString = Integer.toString(stats[i]);
			manager.getFont(0).draw(batch, statString, this.getPos().x + 116, this.getPos().y + 204 - (16 * i));
		}
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		drawStats(batch);
	}
}
