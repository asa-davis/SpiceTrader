package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class DockedMenu extends Menu {
	
	private Village village;

	public DockedMenu(final MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture, Village village) {
		super(manager, pos, backgroundTexture, true);
		
		this.village = village;
		
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
		
		addButton(leaveButton);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		drawTitle(batch, "You have docked at " + village.getName());
		drawBody(batch, "In the future, you will be able to trade spices for gold from this menu. For the best deals, you will be able to trade spices for spices directly.");
	}
	
	//TEMPORARY: menu contents will drastically change in future
	private void drawTitle(SpriteBatch batch, String title) {
		manager.getFont(1).setColor(Color.DARK_GRAY);
		manager.getFont(1).draw(batch, title, this.getPos().x, this.getPos().y + this.getSize().y - 16, this.getSize().x, Align.center, true);
	}
	
	private void drawBody(SpriteBatch batch, String body) {
		manager.getFont(0).setColor(Color.DARK_GRAY);
		manager.getFont(0).draw(batch, body, this.getPos().x + 16, this.getPos().y + this.getSize().y - 64, this.getSize().x - 32, Align.center, true);
	}
}
