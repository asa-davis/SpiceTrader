package dev.asa.spicetrader.UI;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import dev.asa.spicetrader.items.EquipableItem;
import dev.asa.spicetrader.items.Item;
import dev.asa.spicetrader.entities.Player;
import dev.asa.spicetrader.items.Stats;

public class ShipMenu extends Menu {
	
	private Player player;
	private List<Button> cargoItemButtons;
	private List<Button> equipedItemButtons;
	private ItemVisualizer itemVis;
	
	public ShipMenu(MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
		super(manager, pos, backgroundTexture, false);

		player = manager.getPlayer();
		
		cargoItemButtons = new ArrayList<Button>();
		equipedItemButtons = new ArrayList<Button>();
		
		Array<AtlasRegion> nametag = manager.getAtlas().findRegions("ui/nametag");
		itemVis = new ItemVisualizer(manager.getFont(0), nametag.get(0), nametag.get(1));
		
		Array<AtlasRegion> itemSlotButtonTextures = manager.getAtlas().findRegions("ui/item_slot_button");
		
		//create cargo buttons
		for(int i = 0; i < 12; i++) {
			final int cargoIndex = i;
			int row = i / 3;
			int col = i - (3 * row);
			
			Vector2 cargoSlotButtonPos = new Vector2(this.getPos().x + 158 + (50 * col), this.getPos().y + 162 - (50 * row));
			Button cargoSlotButton = new Button(itemSlotButtonTextures, cargoSlotButtonPos);
			
			cargoSlotButton.setOnClick(new OnClickListener(){
	            @Override
	            public void onClick() {
	            	handleCargoClick(cargoIndex);
	            }
			});
			
			cargoItemButtons.add(cargoSlotButton);
		}
		
		//create equipped buttons
		for(int i = 0; i < 4; i++) {
			final int cargoIndex = i;
			int row = i / 2;
			int col = i - (2 * row);
			
			Vector2 equippedSlotButtonPos = new Vector2(this.getPos().x + 22 + (50 * col), this.getPos().y + 62 - (50 * row));
			Button equippedSlotButton = new Button(itemSlotButtonTextures, equippedSlotButtonPos);
			
			equippedSlotButton.setOnClick(new OnClickListener(){
	            @Override
	            public void onClick() {
	            	handleEquippedClick(cargoIndex);
	            }
			});
			
			equipedItemButtons.add(equippedSlotButton);
		}	
	}
	
	private void handleCargoClick(int i) {
		Item item = player.getItemFromCargo(i);
		if(item != null && item instanceof EquipableItem) {
			if(player.addToEquipped(item))
				player.removeFromCargo(item);
		}
	}
	
	private void handleEquippedClick(int i) {
		Item item = player.getItemFromEquipped(i);
		if(item != null && item instanceof EquipableItem) {
			if(player.addToCargo(item))
				player.removeFromEquipped(item);
		}
	}
	
	//this method draws the cargo slot buttons, instead of in the parent class
	//then it draws each item in the player inventory using the ItemVisualizer, which knows the mouse pos.
	//when these buttons are clicked, they report back to the HUDMenu, which handles the functionality of equiping/unequiping/trading items depending on the context, and whether an item is present in that index.
	//these buttons also must be dynamically drawn depending on the amount of cargo space the player has.
	private void drawItems(SpriteBatch batch) {
		for(int i = 0; i < player.getMaxCargo(); i++) {
			Button b = cargoItemButtons.get(i);
			b.draw(batch);
			
			Item item = player.getItemFromCargo(i);
			if(item != null) 
				itemVis.addToBatch(item, b.getPos());
		}
		
		for(int i = 0; i < equipedItemButtons.size(); i++) {
			Button b = equipedItemButtons.get(i);
			b.draw(batch);
			
			Item item = player.getItemFromEquipped(i);
			if(item != null) 
				itemVis.addToBatch(item, b.getPos());	
		}
		
		itemVis.drawBatch(batch);
		itemVis.clearBatch();
	}
	
	private void drawStats(SpriteBatch batch) {
		Stats stats = player.getStats();
		manager.getFont(0).setColor(Color.DARK_GRAY);
		String statString;

		int[] statsArr = {stats.maxSpeed, stats.accel, stats.turning, stats.damage, stats.range};
		
		for(int i = 0; i < 5; i++) {
			statString = Integer.toString(statsArr[i]);
			manager.getFont(0).draw(batch, statString, this.getPos().x + 116, this.getPos().y + 204 - (16 * i));
		}
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		drawStats(batch);
		drawItems(batch);
	}
	
	@Override
	public boolean passMouse(Vector2 mousePos, boolean mouseClicked) {
		boolean hovered = super.passMouse(mousePos, mouseClicked);
		
		for(Button b : equipedItemButtons) {
			b.passMouse(mousePos, mouseClicked);
		}
		
		for(int i = 0; i < player.getMaxCargo(); i++) {
			cargoItemButtons.get(i).passMouse(mousePos, mouseClicked);
		}
		
		itemVis.passMouse(mousePos);

		return hovered;
	}
}
