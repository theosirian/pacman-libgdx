package com.theosirian.pacman.pacdot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.theosirian.pacman.entity.Pacman;
import com.theosirian.pacman.util.Settings;

public class BonusPacdot extends Pacdot {

	public static Texture sprite;
	private static int textureIndex = 0;
	private static int accumalativeBonus = 0;

	private float timer;

	public BonusPacdot(int x, int y, Pacman player) {
		super(x, y, player);
		setBounds(getX() + 4, getY() + 4, 8, 8);
		worth = Math.min(accumalativeBonus >= 1000 ? 500 + accumalativeBonus : 200 + accumalativeBonus, 5000);
		accumalativeBonus = worth;
		currentFrame = new TextureRegion(sprite, textureIndex * Settings.SPRITE_WIDTH, 16, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGHT);
		targetedFrame = new TextureRegion(sprite, textureIndex * Settings.SPRITE_WIDTH, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGHT);
		textureIndex = (textureIndex + 1) % (sprite.getWidth() / 16);
		timer = 0;
	}

	@Override
	public void update(float delta) {
		setBounds(getX() + 4, getY() + 4, 8, 8);
		timer += delta;
		if (timer > 10f) {
			accumalativeBonus = 0;
			this.destroy = true;
		} else {
			super.update(delta);
		}
	}
}
