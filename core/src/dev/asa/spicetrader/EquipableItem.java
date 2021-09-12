package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class EquipableItem extends Item {

    private Stats stats;

    public EquipableItem(String name, TextureAtlas.AtlasRegion texture, Stats stats, int sellPrice, int buyPrice) {
        super(name, texture, sellPrice, buyPrice);
        this.stats = stats;
    }

    public Stats getStats() { return stats; }
}
