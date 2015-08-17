package com.theosirian.pacman;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class SmallPacdot extends Pacdot {

    public static Texture sprite;

    public SmallPacdot(int x, int y, Pacman player) {
        super(x, y, player);
        setBounds(getX() + 6, getY() + 6, 4, 4);
        worth = 10;
    }

    @Override
    public void update(float delta) {
        setBounds(getX() + 6, getY() + 6, 4, 4);
        if (bounds.overlaps(pacman.getBounds())) {
            pacman.changeScore(worth);
            this.destroy = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(sprite, position.x, position.y, origin.x, origin.y, size.x, size.y, scale.x, scale.y, rotation, 0, 0, 16, 16, false, false);
    }

    @Override
    public void dispose() {
    }
}
