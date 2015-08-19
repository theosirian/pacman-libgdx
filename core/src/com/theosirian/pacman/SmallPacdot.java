package com.theosirian.pacman;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SmallPacdot extends Pacdot {

    public static Texture sprite;

    public SmallPacdot(int x, int y, Pacman player) {
        super(x, y, player);
        setBounds(getX() + 6, getY() + 6, 4, 4);
        worth = 10;
	    currentFrame = new TextureRegion(sprite, 0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGHT);
    }

    @Override
    public void update(float delta) {
        setBounds(getX() + 6, getY() + 6, 4, 4);
	    super.update(delta);
    }
}
