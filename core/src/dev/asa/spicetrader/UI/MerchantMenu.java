package dev.asa.spicetrader.UI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import java.util.ArrayList;

import dev.asa.spicetrader.entities.Merchant;

// Player can sell any of items in the Merchant's toBuy list
public class MerchantMenu extends TradeMenu {
    private Merchant merchant;

    public MerchantMenu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
        super(manager, pos, backgroundTexture);

        merchant = (Merchant) manager.getPlayer().getDockable();

        int numTrades = 3;
        ArrayList<Vector2> tradePosList = makeTradeButtonSetRowPos(numTrades);
        for(int i = 0; i < numTrades; i++) {
            addTradeButtonSet(new TradeButtonSet(tradePosList.get(i), TradeType.Sell, merchant.getToBuy(), i));
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        drawTitle(batch, merchant.getName());
    }
}
