package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class PopupMenu extends Menu {

    public PopupMenu(MenuManager manager, Vector2 pos, TextureAtlas.AtlasRegion backgroundTexture, boolean needsPause) {
        super(manager, pos, backgroundTexture, needsPause);

        addLeaveButton();
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

    public void drawTitle(SpriteBatch batch, String title) {
        manager.getFont(2).setColor(Color.DARK_GRAY);
        manager.getFont(2).draw(batch, title, this.getPos().x, this.getPos().y + this.getSize().y - 16, this.getSize().x, Align.center, true);
    }

    private void drawBody(SpriteBatch batch, String body) {
        manager.getFont(1).setColor(Color.DARK_GRAY);
        manager.getFont(1).draw(batch, body, this.getPos().x + 16, this.getPos().y + this.getSize().y - 64, this.getSize().x - 32, Align.center, true);
    }

}
