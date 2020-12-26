package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class HUD extends Menu {
	
	private Player player;
	
	public HUD(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
		super(manager, pos, backgroundTexture, false);
		
		this.player = manager.getPlayer();
	}
	
	private void drawHullBar(SpriteBatch batch) {
		Array<AtlasRegion> hullBarTexture = manager.getAtlas().findRegions("ui/hullbar");

		for(int i = 0; i < player.getMaxHull(); i++) {
			batch.draw(hullBarTexture.get(0), getPos().x + (i * 11) - 4, getPos().y);
		}
		for(int i = 0; i < player.getCurrHull(); i++) {
			batch.draw(hullBarTexture.get(1), getPos().x + (i * 11) - 4, getPos().y);
		}
	}
	
	private void drawCargoBar(SpriteBatch batch) {
		Array<AtlasRegion> cargoBarTexture = manager.getAtlas().findRegions("ui/cargobar");

		for(int i = 0; i < player.getMaxCargo(); i++) {
			batch.draw(cargoBarTexture.get(0), getPos().x + (i * 11) - 4, getPos().y);
		}
		for(int i = 0; i < player.getCurrCargo(); i++) {
			batch.draw(cargoBarTexture.get(1), getPos().x + (i * 11) - 4, getPos().y);
		}
	}
	
	private void drawCoinCount(SpriteBatch batch) {
		String count = Integer.toString(player.getGold());
		manager.getFont(0).setColor(Color.DARK_GRAY);
		manager.getFont(0).draw(batch, count, getPos().x + 80, getPos().y + 30 + 31);
	}
	
	private void drawCannonBallCount(SpriteBatch batch) {
		String count = Integer.toString(player.getCannonBalls());
		manager.getFont(0).setColor(Color.DARK_GRAY);
		manager.getFont(0).draw(batch, count, getPos().x + 80, getPos().y + 30);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		drawHullBar(batch);
		drawCargoBar(batch);
		drawCoinCount(batch);
		drawCannonBallCount(batch);
	}
	
}
