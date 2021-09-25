package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu extends TradeMenu {
    private Shop shop;


    public ShopMenu(MenuManager manager, Vector2 pos, TextureAtlas.AtlasRegion backgroundTexture) {
        super(manager, pos, backgroundTexture);

        shop = (Shop) manager.getPlayer().getDockable();

        int numTrades = 5;
        ArrayList<Vector2> tradePosList = makeTradeButtonSetRowPos(numTrades);

        addTradeButtonSet(new TradeButtonSet(tradePosList.get(0), TradeType.Buy, shop.getCannonballs(), 0));
        addTradeButtonSet(new TradeButtonSet(tradePosList.get(1), TradeType.Buy, shop.getRepairs(), 0));

        for(int i = 2; i < numTrades; i++) {
            addTradeButtonSet(new TradeButtonSet(tradePosList.get(i), TradeType.Buy, shop.getToSell(), i - 2));
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        drawTitle(batch, "Welcome to " + shop.getName());
    }
}
