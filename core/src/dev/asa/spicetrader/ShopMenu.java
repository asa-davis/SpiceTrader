package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class ShopMenu extends TradeMenu {
    private Shop shop;

    public ShopMenu(MenuManager manager, Vector2 pos, TextureAtlas.AtlasRegion backgroundTexture) {
        super(manager, pos, backgroundTexture);

        shop = (Shop) manager.getPlayer().getDockable();
    }

    @Override
    public Array<TextureAtlas.AtlasRegion> getTradeButtonTexture() {
        return manager.getAtlas().findRegions("ui/buy_button");
    }

    @Override
    public void tradeButtonClicked(int i) {

    }

    @Override
    public ArrayList<Item> getInventory() {
        return null;
    }

    @Override
    public int getPrice(Item i) {
        return 0;
    }
}
