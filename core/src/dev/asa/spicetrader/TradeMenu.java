package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;


public abstract class TradeMenu extends Menu {

	private ItemVisualizer itemVis;
	private boolean closeShipMenuOnClose;
	private ArrayList<TradeButtonSet> tradeButtonSets;

	public TradeMenu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
		super(manager, pos, backgroundTexture, true);

		Array<AtlasRegion> nametag = manager.getAtlas().findRegions("ui/nametag");
		itemVis = new ItemVisualizer(manager.getFont(0), nametag.get(0), nametag.get(1));

		tradeButtonSets = new ArrayList<>();

		addLeaveButton();

		closeShipMenuOnClose = !manager.isShipMenuOpen();
		manager.openShipMenu();
	}

	public void addTradeButtonSet(TradeButtonSet tradeButtonSet) {
		tradeButtonSets.add(tradeButtonSet);
	}

	public ArrayList<Vector2> makeTradeButtonSetRowPos(int numTrades) {
		ArrayList<Vector2> posList = new ArrayList<>();

		int distBetweenTrades = 16;
		int distAboveBottom = 100;
		int totalButtonRowWidth = (TradeButtonSet.WIDTH * numTrades) + ((numTrades - 1) * distBetweenTrades);
		Vector2 buttonRowOrigin = new Vector2((int) (getPos().x + (getSize().x / 2) - (totalButtonRowWidth / 2)), getPos().y + distAboveBottom);
		for(int i = 0; i < numTrades; i++) {
			Vector2 tradePos = new Vector2(buttonRowOrigin.x + (i * (TradeButtonSet.WIDTH + distBetweenTrades)), buttonRowOrigin.y);
			posList.add(tradePos);
		}

		return posList;
	}

	//TODO: Change this button to an X in top right.
	private void addLeaveButton() {
		Array<TextureAtlas.AtlasRegion> leaveButtonTextures = manager.getAtlas().findRegions("ui/leave_button");
		Vector2 leaveButtonPos = new Vector2(this.getPos().x + ((this.getSize().x/2)- (leaveButtonTextures.get(0).getRegionWidth()/2)), this.getPos().y + 32);
		Button leaveButton = new Button(leaveButtonTextures, leaveButtonPos);
		leaveButton.setOnClick(new OnClickListener(){
			@Override
			public void onClick() {
				close();
			}
		});
		addButton(leaveButton);
	}

	@Override
	public void close() {
		super.close();
		if(closeShipMenuOnClose)
			manager.closeShipMenu();
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		for(TradeButtonSet tradeButtonSet : tradeButtonSets) {
			tradeButtonSet.draw(batch);
			tradeButtonSet.addItemToBatch(itemVis);
		}
		itemVis.drawBatch(batch);
		itemVis.clearBatch();
	}

	@Override
	public void passMouse(Vector2 mousePos, boolean mouseClicked) {
		super.passMouse(mousePos, mouseClicked);
		for(TradeButtonSet tradeButtonSet : tradeButtonSets) {
			tradeButtonSet.passMouse(mousePos, mouseClicked);
		}
		itemVis.passMouse(mousePos);
	}


	public class TradeButtonSet {

		//change these if we ever change the buttons n stuff
		public static final int WIDTH = 100;
		public static final int HEIGHT = 129;

		private ArrayList<Item> inventory;
		private int itemIndex;

		private Button tradeButton;
		private Button itemSlotButton;
		private Vector2 pricePos;

		private TradeType tradeType;

		public TradeButtonSet(Vector2 pos, TradeType tradeType, ArrayList<Item> inventory, int itemIndex) {
			this.inventory = inventory;
			this.itemIndex = itemIndex;
			this.tradeType = tradeType;

			Array<AtlasRegion> tradeButtonTextures = setTradeButtonTextures();
			tradeButton = makeTradeButton(pos, tradeButtonTextures);
			itemSlotButton = makeItemSlotButton(pos);
			pricePos = calcPricePos(pos);
		}

		public void addItemToBatch(ItemVisualizer itemVis) {
			if(inventory.size() > itemIndex)
				itemVis.addToBatch(inventory.get(itemIndex), itemSlotButton.getPos());
		}

		public void draw(SpriteBatch batch) {
			tradeButton.draw(batch);
			itemSlotButton.draw(batch);
			drawItemPrice(batch);
		}

		public void passMouse(Vector2 mousePos, boolean isClicked) {
			tradeButton.passMouse(mousePos, isClicked);
			itemSlotButton.passMouse(mousePos, isClicked);
		}

		private void drawItemPrice(SpriteBatch batch) {
			int price = calcItemPrice();
			if(price < 0) return;

			AtlasRegion coinTexture = manager.getAtlas().findRegion("ui/coin");
			batch.draw(coinTexture, pricePos.x, pricePos.y);
			manager.getFont(0).setColor(Color.DARK_GRAY);
			manager.getFont(0).draw(batch, String.valueOf(price), pricePos.x + 25, pricePos.y + 16);
		}

		private Array<AtlasRegion> setTradeButtonTextures() {
			if(tradeType == TradeType.Buy) {
				return manager.getAtlas().findRegions("ui/buy_button");
			}
			return manager.getAtlas().findRegions("ui/sell_button");
		}

		private Button makeTradeButton(Vector2 pos, Array<AtlasRegion> tradeButtonTextures) {
			tradeButton = new Button(tradeButtonTextures, pos);
			if(tradeType == TradeType.Buy) {
				tradeButton.setOnClick(new OnClickListener() {
					@Override
					public void onClick() {
						buyItem();
					}
				});
			}
			else {
				tradeButton.setOnClick(new OnClickListener() {
					@Override
					public void onClick() {
						sellItem();
					}
				});
			}

			return tradeButton;
		}

		private Button makeItemSlotButton(Vector2 pos) {
			Vector2 itemSlotPos = new Vector2(pos.x + 26, pos.y + 56);
			Array<AtlasRegion> itemSlotTextures = manager.getAtlas().findRegions("ui/item_slot_button");
			itemSlotButton = new Button(itemSlotTextures, itemSlotPos);
			itemSlotButton.setOnClick(new OnClickListener() {
				@Override
				public void onClick() { }
			});

			return itemSlotButton;
		}

		private Vector2 calcPricePos(Vector2 pos) {
			return new Vector2(pos.x + 24, pos.y + 108);
		}

		private int calcItemPrice() {
			if(!(inventory.size() > itemIndex)) return -1;

			Item item = inventory.get(itemIndex);
			if(tradeType == TradeType.Buy)
				return item.getBuyPrice();
			return item.getSellPrice();
		}

		//player buys item from inventory
		private void buyItem() {
			if(!(inventory.size() > itemIndex)) return;

			Item toBuy = inventory.get(itemIndex);

			if(manager.getPlayer().getGold() < toBuy.getBuyPrice())
				return;

			if(toBuy instanceof CannonBallItem) {
				manager.getPlayer().addCannonball();
				manager.getPlayer().subtractGold(toBuy.getBuyPrice());
				return;
			}

			if(manager.getPlayer().isCargoFull())
				return;

			inventory.remove(itemIndex);
			manager.getPlayer().addToCargo(toBuy);
			manager.getPlayer().subtractGold(toBuy.getBuyPrice());
		}

		//player sells item from their inventory
		private void sellItem() {
			if(!(inventory.size() > itemIndex)) return;

			Item toSell = inventory.get(itemIndex);
			if(manager.getPlayer().hasItem(toSell)) {
				manager.getPlayer().removeFromCargo(toSell.getName());
				manager.getPlayer().addGold(toSell.getSellPrice());
			}
		}
	}

	public enum TradeType { Buy, Sell }
}
