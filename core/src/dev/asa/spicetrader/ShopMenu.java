package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu extends PopupMenu {
    private Shop shop;

    private ItemVisualizer itemVis;
    private List<Vector2> itemPosList;
    private boolean closeShipMenuOnClose;

    public ShopMenu(MenuManager manager, Vector2 pos, TextureAtlas.AtlasRegion backgroundTexture) {
        super(manager, pos, backgroundTexture, true);

        Array<TextureAtlas.AtlasRegion> nametag = manager.getAtlas().findRegions("ui/nametag");
        itemVis = new ItemVisualizer(manager.getFont(0), nametag.get(0), nametag.get(1));
        itemPosList = new ArrayList<>();
        addItemTradeButtons();

        closeShipMenuOnClose = !manager.isShipMenuOpen();
        manager.openShipMenu();

        shop = (Shop) manager.getPlayer().getDockable();
    }

    public Array<TextureAtlas.AtlasRegion> getTradeButtonTexture() {
        return manager.getAtlas().findRegions("ui/buy_button");
    }

    @Override
    public void close() {
        super.close();
        if(closeShipMenuOnClose)
            manager.closeShipMenu();
    }

    public void tradeButtonClicked(int i) {
        buyItem(i);
    }


    public ArrayList<Item> getInventory() {
        return shop.getToSell();
    }

    public int getPrice(Item i) {
        return i.getBuyPrice();
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        drawItems(batch);
        drawTitle(batch, "Welcome to " + shop.getName());
    }

    @Override
    public void passMouse(Vector2 mousePos, boolean mouseClicked) {
        super.passMouse(mousePos, mouseClicked);
        itemVis.passMouse(mousePos);
    }

    private void addItemTradeButtons() {
        //create item slot buttons and buy buttons
        int totalButtonColumnHeight = (48 * 2) + 32;
        int distBetweenItemSlots = 48 + 32;
        for(int i = 0; i < 4; i++) {
            Array<TextureAtlas.AtlasRegion> itemSlotButtonTextures = manager.getAtlas().findRegions("ui/item_slot_button");
            Array<TextureAtlas.AtlasRegion> buyButtonTextures = getTradeButtonTexture();
            Vector2 itemPos = new Vector2(getPos().x + (getSize().x - 184) - ((i / 2) * 180), getPos().y + ((getSize().y / 2) - (totalButtonColumnHeight / 2)) + ((i % 2) * distBetweenItemSlots));
            itemPosList.add(itemPos);
            Vector2 buyPos = new Vector2(itemPos.x + 48 + 16, itemPos.y);
            Button itemSlot = new Button(itemSlotButtonTextures, itemPos);
            Button buyButton = new Button(buyButtonTextures, buyPos);
            itemSlot.setOnClick(new OnClickListener(){
                @Override
                public void onClick() { }
            });
            final int finalI = i;
            buyButton.setOnClick(new OnClickListener() {
                @Override
                public void onClick() { tradeButtonClicked(finalI); }
            });
            addButton(itemSlot);
            addButton(buyButton);
        }
    }

    private void drawItems(SpriteBatch batch) {
        TextureAtlas.AtlasRegion coinTexture = manager.getAtlas().findRegion("ui/coin");

        ArrayList<Item> items = getInventory();
        int numItemsToDraw = items.size();
        if(numItemsToDraw > 4) numItemsToDraw = 4;

        for(int i = 0; i < numItemsToDraw; i++) {
            itemVis.addToBatch(items.get(i), itemPosList.get(i));
            //draw item prices
            batch.draw(coinTexture, itemPosList.get(i).x - 2, itemPosList.get(i).y + 52);
            int itemPrice = getPrice(items.get(i));
            manager.getFont(0).setColor(Color.DARK_GRAY);
            manager.getFont(0).draw(batch, String.valueOf(itemPrice), itemPosList.get(i).x + 23, itemPosList.get(i).y + 68);
        }

        itemVis.drawBatch(batch);
        itemVis.clearBatch();
    }

    private void buyItem(int i) {
        if(!(shop.getToSell().size() > i)) return;

        Item toBuy = shop.getToSell().get(i);
        if(!manager.getPlayer().isCargoFull() && manager.getPlayer().getGold() >= toBuy.getBuyPrice()) {
            shop.getToSell().remove(i);
            manager.getPlayer().addToCargo(toBuy);
            manager.getPlayer().subtractGold(toBuy.getBuyPrice());
        }
    }

    //TEMPORARY: menu contents will drastically change in future
    public void drawTitle(SpriteBatch batch, String title) {
        manager.getFont(2).setColor(Color.DARK_GRAY);
        manager.getFont(2).draw(batch, title, this.getPos().x, this.getPos().y + this.getSize().y - 16, this.getSize().x, Align.center, true);
    }

    private void drawBody(SpriteBatch batch, String body) {
        manager.getFont(1).setColor(Color.DARK_GRAY);
        manager.getFont(1).draw(batch, body, this.getPos().x + 16, this.getPos().y + this.getSize().y - 64, this.getSize().x - 32, Align.center, true);
    }
}
