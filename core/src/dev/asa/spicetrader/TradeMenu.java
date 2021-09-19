package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;


public abstract class TradeMenu extends Menu {

	private ItemVisualizer itemVis;
	private List<Button> allButtons;
	private List<Vector2> itemPosList;
	private boolean closeShipMenuOnClose;

	public TradeMenu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
		super(manager, pos, backgroundTexture, true);

		Array<AtlasRegion> nametag = manager.getAtlas().findRegions("ui/nametag");
		itemVis = new ItemVisualizer(manager.getFont(0), nametag.get(0), nametag.get(1));

		allButtons = new ArrayList<>();
		itemPosList = new ArrayList<>();

		setupUI();
		closeShipMenuOnClose = !manager.isShipMenuOpen();
		manager.openShipMenu();
	}

	@Override
	public void close() {
		super.close();
		if(closeShipMenuOnClose)
			manager.closeShipMenu();
	}

	public abstract Array<AtlasRegion> getTradeButtonTexture();

	public abstract void tradeButtonClicked(int i);

	public abstract ArrayList<Item> getInventory();

	public abstract int getPrice(Item i);
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		drawItems(batch);
	}

	@Override
	public void passMouse(Vector2 mousePos, boolean mouseClicked) {
		super.passMouse(mousePos, mouseClicked);
		itemVis.passMouse(mousePos);
	}

	private void setupUI() {
		//create leave button
		Array<AtlasRegion> leaveButtonTextures = manager.getAtlas().findRegions("ui/leave_button");
		Vector2 leaveButtonPos = new Vector2(this.getPos().x + ((this.getSize().x/2)- (leaveButtonTextures.get(0).getRegionWidth()/2)), this.getPos().y + 32);
		Button leaveButton = new Button(leaveButtonTextures, leaveButtonPos);
		leaveButton.setOnClick(new OnClickListener(){
			@Override
			public void onClick() {
				close();
			}
		});
		allButtons.add(leaveButton);

		//create item slot buttons and buy buttons
		int totalButtonRowWidth = 288;
		for(int i = 0; i < 3; i++) {
			Array<AtlasRegion> itemSlotButtonTextures = manager.getAtlas().findRegions("ui/item_slot_button");
			Array<AtlasRegion> buyButtonTextures = getTradeButtonTexture();
			Vector2 itemPos = new Vector2(getPos().x + ((getSize().x / 2) - (totalButtonRowWidth / 2)) + (i * 120), getPos().y + 160);
			itemPosList.add(itemPos);
			Vector2 buyPos = new Vector2(itemPos.x - 26, itemPos.y - 56);
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
			allButtons.add(itemSlot);
			allButtons.add(buyButton);
		}

		addButtons(allButtons);
	}

	private void drawItems(SpriteBatch batch) {
		AtlasRegion coinTexture = manager.getAtlas().findRegion("ui/coin");

		ArrayList<Item> items = getInventory();
		int numItemsToDraw = items.size();
		if(numItemsToDraw > 3) numItemsToDraw = 3;

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
