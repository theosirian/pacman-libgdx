package com.theosirian.pacman;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BigPacdot extends Pacdot {

    public static Texture sprite;

    public BigPacdot(int x, int y, Pacman player) {
        super(x, y, player);
        setBounds(getX() + 4, getY() + 4, 8, 8);
        worth = 50;
        currentFrame = new TextureRegion(sprite, 0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGHT);
    }

    @Override
    public void update(float delta) {
        setBounds(getX() + 4, getY() + 4, 8, 8);
        super.update(delta);
    }
}
