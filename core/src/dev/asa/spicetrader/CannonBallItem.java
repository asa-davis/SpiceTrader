package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class CannonBallItem extends Item {
    public CannonBallItem(TextureAtlas.AtlasRegion texture, int buyPrice) {
        super("Cannonball", texture, 0, buyPrice);
    }
}
