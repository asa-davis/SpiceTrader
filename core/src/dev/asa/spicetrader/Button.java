package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Button {
	//0 = default, 1 = hover, 2 = click
	private Array<AtlasRegion> textures;
	private Rectangle hitbox;
	private OnClickListener listener;
	private Vector2 pos;
	private State state;
	private boolean isBeingClicked;
	
	public Button(Array<AtlasRegion> restartButtonTextures, Vector2 pos) {
		this.textures = restartButtonTextures;
		this.pos = pos;
		this.hitbox = new Rectangle(pos.x, pos.y, restartButtonTextures.get(0).getRegionWidth(), restartButtonTextures.get(0).getRegionHeight());
		this.state = State.DEFAULT;
		this.isBeingClicked = false;
	}
	
	public void passMouse(Vector2 mousePos, boolean mouseClicked) {
		if(hitbox.contains(mousePos)) {
			if(mouseClicked) {
				//buttons only do the thing after you release the left mouse button
				state = State.CLICK;
				isBeingClicked = true;
			}
			else {
				state = State.HOVER;
				if(isBeingClicked) {
					listener.onClick();
					isBeingClicked = false;
				}
			}
		}
		else {
			state = State.DEFAULT;
			if(isBeingClicked) {
				isBeingClicked = false;
			}
		}
	}
	
	public void setOnClick(OnClickListener listener) {
		this.listener = listener;
	}
	
	public void draw(SpriteBatch batch) {
		switch(state) {
			case DEFAULT:
				batch.draw(textures.get(0), pos.x, pos.y);
				break;
			case HOVER:
				batch.draw(textures.get(1), pos.x, pos.y);
				break;
			case CLICK:
				batch.draw(textures.get(2), pos.x, pos.y);
				break;
		}
	}
	
	private enum State {DEFAULT, HOVER, CLICK}
}