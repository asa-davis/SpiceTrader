package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Village extends LandEntity {

    private ItemFactory itemFactory;
    private ArrayList<Item> toSell = new ArrayList<>();
    private int tier;

    public Village(Vector2 pos, Sprite sprite, EntityFactory.LandEntityLocation location, Polygon dockHitbox, ItemFactory itemFactory) {
        super(pos, sprite, location, dockHitbox);
        this.itemFactory = itemFactory;
        this.tier = location.tier;
        toSell.add(itemFactory.getGinger());
        toSell.add(itemFactory.getCinnamon());
    }

    public int getTier() {
        return tier;
    }

    public ArrayList<Item> getToSell() {
        return toSell;
    }
}
