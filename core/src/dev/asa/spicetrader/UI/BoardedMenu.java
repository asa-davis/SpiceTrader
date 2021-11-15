package dev.asa.spicetrader.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//this menu shows up when pirates collide with your ship.
//for now, being boarded ends the game. in the future, hiring crew will allow you to fight off pirates.
//the specifics of this mechanic will be worked out later.

public class BoardedMenu extends Menu {

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
		drawTitle(batch, "Your ship has been boarded!", Color.FIREBRICK);
		drawBody(batch, "Pirates have overrun your vessel. This marks the end of your voyage.\n SCORE: " + manager.getPlayer().getScore());
	}
}
