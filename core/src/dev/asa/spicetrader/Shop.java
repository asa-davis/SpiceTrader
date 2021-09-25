package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Shop extends LandEntity {
    private int tier;

    private ItemFactory itemFactory;
    private ArrayList<Item> toSell = new ArrayList<>();
    private ArrayList<Item> cannonballs = new ArrayList<>();
    private ArrayList<Item> repairs = new ArrayList<>();

    // number of frames between shop item drops
    private static final int ITEM_DROP_INTERVAL = 300;
    private int currItemDropTick = 0;
    private static final int MAX_NUM_ITEMS = 3;

    public Shop(Vector2 pos, Sprite sprite, EntityFactory.LandEntityLocation location, Polygon dockHitbox, ItemFactory itemFactory) {
        super(pos, sprite, location, dockHitbox);
        this.itemFactory = itemFactory;
        this.tier = location.tier;

        cannonballs.add(itemFactory.getCannonball());
        repairs.add(itemFactory.getRepairItem());
    }

    @Override
    public void tick() {
        super.tick();
        if(currItemDropTick >= ITEM_DROP_INTERVAL) {
            currItemDropTick = 0;
            dropItem();
        }
        else
            currItemDropTick++;
    }

    // drop random item depending on tier
    private void dropItem() {
        if(toSell.size() >= MAX_NUM_ITEMS)
            return;

        toSell.add(itemFactory.getRandomEquipable(tier));
    }

    public ArrayList<Item> getCannonballs() { return cannonballs; }

    public ArrayList<Item> getRepairs() { return repairs; }

    public ArrayList<Item> getToSell() {
        return toSell;
    }
}
