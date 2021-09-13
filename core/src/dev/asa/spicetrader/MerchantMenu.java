package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import java.util.ArrayList;
import com.badlogic.gdx.utils.Array;

public class MerchantMenu extends TradeMenu {
    private Merchant merchant;

    public MerchantMenu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
        super(manager, pos, backgroundTexture);

        merchant = (Merchant) manager.getPlayer().getDockable();
    }

    @Override
    public Array<AtlasRegion> getTradeButtonTexture() {
        return manager.getAtlas().findRegions("ui/sell_button");
    }

    @Override
    public void tradeButtonClicked(int i) {

    }

    @Override
    public ArrayList<Item> getInventory() {
        return merchant.getToBuy();
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        drawTitle(batch, "Welcome to " + merchant.getName());
    }
}
