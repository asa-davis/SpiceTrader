package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Merchant extends LandEntity {
    private ItemFactory itemFactory;
    public Merchant(Vector2 pos, Sprite sprite, EntityFactory.LandEntityLocation location, Polygon dockHitbox, ItemFactory itemFactory)  {
        super(pos, sprite, location, dockHitbox);
        this.itemFactory = itemFactory;
    }
}
