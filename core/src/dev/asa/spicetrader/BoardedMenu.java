package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

//this menu shows up when pirates collide with your ship.
//for now, being boarded ends the game. in the future, hiring crew will allow you to fight off pirates.
//the specifics of this mechanic will be worked out later.

public class BoardedMenu extends Menu{

	public BoardedMenu(final MenuManager manager, Vector2 pos, AtlasRegion backgroundTexture) {
		super(manager, pos, backgroundTexture, true);
		
		//create restart button 
		Array<AtlasRegion> restartButtonTextures = manager.getAtlas().findRegions("ui/restart_button");
		Vector2 restartButtonPos = new Vector2(this.getPos().x + ((this.getSize().x/2)- (restartButtonTextures.get(0).getRegionWidth()/2)), this.getPos().y + 22);
		Button restartButton = new Button(restartButtonTextures, restartButtonPos);
		restartButton.setOnClick(new OnClickListener(){
            @Override
            public void onClick() {
                manager.getGame().create();
            }
		});
		
		this.addButton(restartButton);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		drawTitle(batch, "Your ship has been boarded!");
		drawBody(batch, "Pirates have overrun your vessel, murdering you and your crew and looting your cargo. This marks the end of your voyage.");
	}
	
	//TEMPORARY: menu contents will drastically change in future
	private void drawTitle(SpriteBatch batch, String title) {
		manager.getFont(1).setColor(Color.FIREBRICK);
		manager.getFont(1).draw(batch, title, this.getPos().x, this.getPos().y + this.getSize().y - 24, this.getSize().x, Align.center, true);
	}
	
	private void drawBody(SpriteBatch batch, String body) {
		manager.getFont(0).setColor(Color.DARK_GRAY);
		manager.getFont(0).draw(batch, body, this.getPos().x + 16, this.getPos().y + this.getSize().y - 64, this.getSize().x - 32, Align.center, true);
	}
}
