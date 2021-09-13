package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Merchant extends LandEntity {
    private ItemFactory itemFactory;
    private ArrayList<Item> toBuy = new ArrayList<>();
    int tier;

    public Merchant(Vector2 pos, Sprite sprite, EntityFactory.LandEntityLocation location, Polygon dockHitbox, ItemFactory itemFactory) {
        super(pos, sprite, location, dockHitbox);
        this.itemFactory = itemFactory;
        this.tier = location.tier;
        toBuy.add(itemFactory.getGinger());
        toBuy.add(itemFactory.getCinnamon());
    }

    public ArrayList<Item> getToBuy() {
        return toBuy;
    }
}
