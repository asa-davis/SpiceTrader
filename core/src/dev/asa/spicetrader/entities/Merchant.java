package dev.asa.spicetrader.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import dev.asa.spicetrader.items.Item;
import dev.asa.spicetrader.items.ItemFactory;

import java.util.ArrayList;

public class Merchant extends LandEntity {
    private ItemFactory itemFactory;
    private ArrayList<Item> toBuy = new ArrayList<>();

    public Merchant(Vector2 pos, Sprite sprite, EntityFactory.LandEntityLocation location, Polygon dockHitbox, ItemFactory itemFactory) {
        super(pos, sprite, location, dockHitbox);
        this.itemFactory = itemFactory;
        addItemsToBuy(location.tier);

    }

    public ArrayList<Item> getToBuy() {
        return toBuy;
    }

    private void addItemsToBuy(int tier) {
        switch(tier) {
            case 1:
                toBuy.add(itemFactory.getGinger());
                toBuy.add(itemFactory.getPeppercorn());
                toBuy.add(itemFactory.getCinnamon());
                break;
            case 2:
                toBuy.add(itemFactory.getPeppercorn());
                toBuy.add(itemFactory.getCinnamon());
                toBuy.add(itemFactory.getCloves());
                break;
            case 3:
                toBuy.add(itemFactory.getCinnamon());
                toBuy.add(itemFactory.getCloves());
                toBuy.add(itemFactory.getNutmeg());
                break;
        }
    }
}
