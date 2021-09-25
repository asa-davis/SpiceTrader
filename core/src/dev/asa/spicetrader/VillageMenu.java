package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

// Playe can buy any item in the Village's toSell list
public class VillageMenu extends TradeMenu {

    private Village village;

    public VillageMenu(MenuManager manager, Vector2 pos, TextureAtlas.AtlasRegion backgroundTexture) {
        super(manager, pos, backgroundTexture);

        village = (Village) manager.getPlayer().getDockable();

        int numTrades = 3;
        ArrayList<Vector2> tradePosList = makeTradeButtonSetRowPos(numTrades);
        for(int i = 0; i < numTrades; i++) {
            addTradeButtonSet(new TradeButtonSet(tradePosList.get(i), TradeType.Buy, village.getToSell(), i));
        }
    }

    @Override
    public  void draw(SpriteBatch batch) {
        super.draw(batch);
        drawTitle(batch, "Welcome to " + village.getName());
    }
}
