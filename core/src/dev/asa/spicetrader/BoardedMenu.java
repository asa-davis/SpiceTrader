package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

//this menu shows up when pirates collide with your ship.
//for now, being boarded ends the game. in the future, hiring crew will allow you to fight off pirates.
//the specifics of this mechanic will be worked out later.

public class BoardedMenu extends Menu{

	public BoardedMenu(Vector2 screenSize, AtlasRegion backgroundTexture, BitmapFont[] fonts, Array<AtlasRegion> restartButtonTextures, final MainGame game) {
		super(screenSize, backgroundTexture, fonts);
		
		//create restart button 
		Vector2 restartButtonPos = new Vector2(this.getPos().x + ((this.getSize().x/2)- (restartButtonTextures.get(0).getRegionWidth()/2)), this.getPos().y + 32);
		Button restartButton = new Button(restartButtonTextures, restartButtonPos);
		restartButton.setOnClick(new OnClickListener(){
            @Override
            public void onClick() {
                game.create();
            }
		});
		
		this.addButton(restartButton);
	}

	@Override
	protected void setPos() {
		int topGap = 128;
		float x = (this.getScreenSize().x / 2) - (this.getSize().x / 2);
		float y = this.getScreenSize().y - this.getSize().y - topGap;
		this.setPos(new Vector2(x, y));
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		this.drawTitle(batch, "Your ship has been boarded!");
		this.drawBody(batch, "Pirates have overrun your vessel, murdering you and your crew and looting your cargo. This marks the end of your voyage.");
	}
	
	private void drawTitle(SpriteBatch batch, String title) {
		this.getFont(1).setColor(Color.FIREBRICK);
		this.getFont(1).draw(batch, title, this.getPos().x, this.getPos().y + this.getSize().y - 16, this.getSize().x, Align.center, true);
	}
	
	private void drawBody(SpriteBatch batch, String body) {
		this.getFont(0).setColor(Color.DARK_GRAY);
		this.getFont(0).draw(batch, body, this.getPos().x + 16, this.getPos().y + this.getSize().y - 64, this.getSize().x - 32, Align.center, true);
	}
}
