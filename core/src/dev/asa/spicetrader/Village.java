package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Village extends LandEntity {

    private ItemFactory itemFactory;
    private ArrayList<Item> toSell = new ArrayList<>();
    private int tier;

    // number of frames between village item drops
    private static final int ITEM_DROP_INTERVAL = 300;
    private int currItemDropTick = 0;
    private static final int MAX_NUM_ITEMS = 3;

    public Village(Vector2 pos, Sprite sprite, EntityFactory.LandEntityLocation location, Polygon dockHitbox, ItemFactory itemFactory) {
        super(pos, sprite, location, dockHitbox);
        this.itemFactory = itemFactory;
        this.tier = location.tier;
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

        Item[] itemOptions = getItemOptions();
        int i = Utils.randInt(0, itemOptions.length - 1);
        toSell.add(itemOptions[i]);
    }

    private Item[] getItemOptions() {
        Item[] itemOptions;
        switch(tier) {
            case 1:
                itemOptions = new Item[] {
                        itemFactory.getGinger(),
                        itemFactory.getPeppercorn()
                };
                break;
            case 2:
                itemOptions = new Item[] {
                        itemFactory.getPeppercorn(),
                        itemFactory.getCinnamon(),
                        itemFactory.getCloves()
                };
                break;
            case 3:
                itemOptions = new Item[] {
                        itemFactory.getCloves(),
                        itemFactory.getNutmeg()
                };
                break;
            default:
                itemOptions = new Item[0];
        }
        return itemOptions;
    }

    public ArrayList<Item> getToSell() {
        return toSell;
    }
}
