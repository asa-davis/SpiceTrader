package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ItemFactory {
    private TextureAtlas atlas;

    public ItemFactory(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    //tier 1
    public Item getGinger() {
        return new Item("Ginger", atlas.findRegion("items/ginger"), 10, 7);
    }

    //tier 1 and 2
    public Item getPeppercorn() {
        return new Item("Peppercorn", atlas.findRegion("items/peppercorn"), 15, 10);
    }

    //tier 2
    public Item getCinnamon() {
        return new Item("Cinnamon", atlas.findRegion("items/cinnamon"), 30, 23);
    }

    //tier 2
    public Item getCloves() {
        return new Item("Cloves", atlas.findRegion("items/cloves"), 50, 40);
    }

    //tier 2 and 3
    public Item getNutmeg() {
        return new Item("Nutmeg", atlas.findRegion("items/nutmeg"), 100, 80);
    }

    public CannonBallItem getCannonball() { return new CannonBallItem(atlas.findRegion("items/Cannonball"), 5); }

    public EquipableItem getTestEquippable() {
        Stats testStats = new Stats();
        testStats.range = 5;
        testStats.damage = 5;
        testStats.cargo = 4;
        testStats.maxSpeed = -10;
        return new EquipableItem("Big Cannons", atlas.findRegion("items/ginger"), testStats, 200, 300);
    }
}
