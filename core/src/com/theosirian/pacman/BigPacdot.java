package com.theosirian.pacman;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BigPacdot extends Pacdot {

    public static Texture sprite;

    public BigPacdot(int x, int y, Pacman player) {
        super(x, y, player);
        setBounds(getX() + 4, getY() + 4, 8, 8);
        worth = 50;
    }

    @Override
    public void update(float delta) {
        setBounds(getX() + 4, getY() + 4, 8, 8);
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
