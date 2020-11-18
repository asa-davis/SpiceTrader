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

	public DockedMenu(MenuManager manager, Vector2 screenSize, TextureAtlas atlas, BitmapFont[] fonts, Village village) {
		super(manager, screenSize, atlas, fonts, true);
		this.village = village;
		
		//create leave button 
		Array<AtlasRegion> leaveButtonTextures = atlas.findRegions("ui/leave_button");
		Vector2 leaveButtonPos = new Vector2(this.getPos().x + ((this.getSize().x/2)- (leaveButtonTextures.get(0).getRegionWidth()/2)), this.getPos().y + 32);
		Button leaveButton = new Button(leaveButtonTextures, leaveButtonPos);
		leaveButton.setOnClick(new OnClickListener(){
            @Override
            public void onClick() {
                close();
            }
		});
		
		this.addButton(leaveButton);
	}

	@Override
	protected void setPos() {
		int topGap = 128;
		float x = (this.getScreenSize().x / 2) - (this.getSize().x / 2);
		float y = this.getScreenSize().y - this.getSize().y - topGap;
		this.setPos(new Vector2(x, y));
	}

	@Override
	protected void setBackground() {
		this.setBackgroundTexture(this.findRegion("ui/game_over_menu_background"));
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		this.drawTitle(batch, "You have docked at " + village.getName());
		this.drawBody(batch, "In the future, you will be able to trade spices for silver from this menu. For the best deals, you will be able to trade spices for spices directly.");
	}
	
	private void drawTitle(SpriteBatch batch, String title) {
		this.getFont(1).setColor(Color.DARK_GRAY);
		this.getFont(1).draw(batch, title, this.getPos().x, this.getPos().y + this.getSize().y - 16, this.getSize().x, Align.center, true);
	}
	
	private void drawBody(SpriteBatch batch, String body) {
		this.getFont(0).setColor(Color.DARK_GRAY);
		this.getFont(0).draw(batch, body, this.getPos().x + 16, this.getPos().y + this.getSize().y - 64, this.getSize().x - 32, Align.center, true);
	}

}
