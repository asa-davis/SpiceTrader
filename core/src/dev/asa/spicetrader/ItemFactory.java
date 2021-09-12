package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ItemFactory {
    private TextureAtlas atlas;

    public ItemFactory(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public Item getGinger() {
        return new Item("Ginger", atlas.findRegion("items/ginger"));
    }

    public Item getPeppercorn() {
        return new Item("Peppercorn", atlas.findRegion("items/peppercorn"));
    }

    public Item getCinnamon() {
        return new Item("Cinnamon", atlas.findRegion("items/cinnamon"));
    }

    public Item getCloves() {
        return new Item("Cloves", atlas.findRegion("items/cloves"));
    }

    public Item getNutmeg() {
        return new Item("Nutmeg", atlas.findRegion("items/nutmeg"));
    }

    public EquipableItem getTestEquippable() {
        Stats testStats = new Stats();
        testStats.range = 5;
        testStats.damage = 5;
        testStats.cargo = 4;
        testStats.maxSpeed = -10;
        return new EquipableItem("Big Cannons", atlas.findRegion("items/ginger"), testStats);
    }
}
