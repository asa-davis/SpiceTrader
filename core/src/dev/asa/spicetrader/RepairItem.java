package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class RepairItem extends Item {
    public RepairItem(TextureAtlas.AtlasRegion texture, int buyPrice) {
        super("Hull Repair", texture, 0, buyPrice);
    }
}
