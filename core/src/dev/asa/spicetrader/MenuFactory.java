package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;

public class MenuFactory {

	public static Menu createMenu(MenuManager manager, String menuType) {
		Vector2 pos;
		AtlasRegion backgroundTexture;
				
		if(menuType.equals("BoardedMenu")) {
			backgroundTexture = manager.getAtlas().findRegion("ui/game_over_menu_background");
			pos = getCenterMenuPos(manager, backgroundTexture);
			
			return new BoardedMenu(manager, pos, backgroundTexture);
		}
		
		else if(menuType.equals("VillageMenu")) {
			backgroundTexture = manager.getAtlas().findRegion("ui/docked_menu");
			pos = getCenterMenuPos(manager, backgroundTexture);
			
			return new VillageMenu(manager, pos, backgroundTexture);
		}

		else if(menuType.equals("MerchantMenu")) {
			backgroundTexture = manager.getAtlas().findRegion("ui/docked_menu");
			pos = getCenterMenuPos(manager, backgroundTexture);

			return new MerchantMenu(manager, pos, backgroundTexture);
		}

		else if(menuType.equals("ShopMenu")) {
			backgroundTexture = manager.getAtlas().findRegion("ui/docked_menu");
			pos = getCenterMenuPos(manager, backgroundTexture);

			return new ShopMenu(manager, pos, backgroundTexture);
		}
		
		else if(menuType.equals("HUDMenu")) {
			backgroundTexture = manager.getAtlas().findRegion("ui/HUD_Base");
			pos = new Vector2(8,8);
			
			return new HUDMenu(manager, pos, backgroundTexture);
		}
		
		else if(menuType.equals("ShipMenu")) {
			backgroundTexture = manager.getAtlas().findRegion("ui/ship_menu_base");
			pos = new Vector2(8, 86);
			
			return new ShipMenu(manager, pos, backgroundTexture);
		}
		
		return null;
	}

	private static Vector2 getCenterMenuPos(MenuManager manager, AtlasRegion backgroundTexture) {
		int topGap = 128;
		float x = (manager.getScreenSize().x / 2) - (backgroundTexture.getRegionWidth() / 2);
		float y = manager.getScreenSize().y - backgroundTexture.getRegionHeight() - topGap;
		return new Vector2(x, y);
	}
	
}
