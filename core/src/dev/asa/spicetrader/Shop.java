package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Shop extends LandEntity {
    public Shop(Vector2 pos, Sprite sprite, EntityFactory.LandEntityLocation location, Polygon dockHitbox) {
        super(pos, sprite, location, dockHitbox);
    }
}
