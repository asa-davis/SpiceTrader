package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class VillageMenu extends Menu {
	
	private Village village;
	private ItemVisualizer itemVis;
	private List<Button> allButtons;
	private List<Vector2> itemPosList;

	public VillageMenu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
		super(manager, pos, backgroundTexture, true);
		
		village = (Village) manager.getPlayer().getDockable();

		Array<AtlasRegion> nametag = manager.getAtlas().findRegions("ui/nametag");
		itemVis = new ItemVisualizer(manager.getFont(0), nametag.get(0), nametag.get(1));

		allButtons = new ArrayList<>();
		itemPosList = new ArrayList<>();
		
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
			Array<AtlasRegion> buyButtonTextures = manager.getAtlas().findRegions("ui/buy_button");
			Vector2 itemPos = new Vector2(getPos().x + ((getSize().x / 2) - (totalButtonRowWidth / 2)) + (i * 120), getPos().y + 160);
			itemPosList.add(itemPos);
			Vector2 buyPos = new Vector2(itemPos.x - 26, itemPos.y - 56);
			Button itemSlot = new Button(itemSlotButtonTextures, itemPos);
			Button buyButton = new Button(buyButtonTextures, buyPos);
			itemSlot.setOnClick(new OnClickListener(){
				@Override
				public void onClick() { }
			});
			buyButton.setOnClick(new OnClickListener() {
				@Override
				public void onClick() { }
			});
			allButtons.add(itemSlot);
			allButtons.add(buyButton);
		}

		addButtons(allButtons);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		drawTitle(batch, "Welcome to " + village.getName());
		drawItems(batch);
	}

	@Override
	public void passMouse(Vector2 mousePos, boolean mouseClicked) {
		super.passMouse(mousePos, mouseClicked);
		itemVis.passMouse(mousePos);
	}


	private void drawItems(SpriteBatch batch) {
		ArrayList<Item> items = village.getToSell();
		int numItemsToDraw = items.size();
		if(numItemsToDraw > 3) numItemsToDraw = 3;

		for(int i = 0; i < numItemsToDraw; i++) {
			itemVis.addToBatch(items.get(i), itemPosList.get(i));
		}

		itemVis.drawBatch(batch);
		itemVis.clearBatch();
	}
	
	//TEMPORARY: menu contents will drastically change in future
	private void drawTitle(SpriteBatch batch, String title) {
		manager.getFont(2).setColor(Color.DARK_GRAY);
		manager.getFont(2).draw(batch, title, this.getPos().x, this.getPos().y + this.getSize().y - 16, this.getSize().x, Align.center, true);
	}
	
	private void drawBody(SpriteBatch batch, String body) {
		manager.getFont(1).setColor(Color.DARK_GRAY);
		manager.getFont(1).draw(batch, body, this.getPos().x + 16, this.getPos().y + this.getSize().y - 64, this.getSize().x - 32, Align.center, true);
	}
}
