package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class VillageMenu extends TradeMenu {

    private Village village;

    public VillageMenu(MenuManager manager, Vector2 pos, TextureAtlas.AtlasRegion backgroundTexture) {
        super(manager, pos, backgroundTexture);

        village = (Village) manager.getPlayer().getDockable();
    }

    @Override
    public Array<TextureAtlas.AtlasRegion> getTradeButtonTexture() {
        return manager.getAtlas().findRegions("ui/buy_button");
    }

    @Override
    public void tradeButtonClicked(int i) {
        buyItem(i);
    }


    @Override
    public ArrayList<Item> getInventory() {
        return village.getToSell();
    }

    @Override
    public  void draw(SpriteBatch batch) {
        super.draw(batch);
        drawTitle(batch, "Welcome to " + village.getName());
    }

    private void buyItem(int i) {
        if(!(village.getToSell().size() > i)) return;

        Item toBuy = village.getToSell().get(i);
        if(!manager.getPlayer().isCargoFull() && manager.getPlayer().getGold() >= toBuy.getBuyPrice()) {
            village.getToSell().remove(i);
            manager.getPlayer().addToCargo(toBuy);
            manager.getPlayer().subtractGold(toBuy.getBuyPrice());
        }
    }
}
