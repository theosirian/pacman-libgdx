package com.theosirian.pacman.pacdot;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.theosirian.pacman.entity.Entity;
import com.theosirian.pacman.entity.Pacman;

public class Pacdot extends Entity {

	protected Pacman pacman;

	protected boolean destroy;

	protected int worth;

	protected boolean targeted;

	protected TextureRegion targetedFrame;

	public Pacdot(int x, int y, Pacman player) {
		super(x, y, player != null ? player.getCollisionLayer() : null);
		pacman = player;
		worth = 0;
	}

	@Override
	public void update(float delta) {
		if (pacman.getBounds().contains(getBounds())) {
			pacman.changeScore(worth);
			this.destroy = true;
		}
	}

	@Override
	public void draw(Batch batch) {
		if (isTargeted()) batch.draw(targetedFrame, position.x, position.y, origin.x, origin.y, size.x, size.y, scale.x, scale.y, rotation);
		else batch.draw(currentFrame, position.x, position.y, origin.x, origin.y, size.x, size.y, scale.x, scale.y, rotation);
	}

	public Pacman getPacman() {
		return pacman;
	}

	public void setPacman(Pacman pacman) {
		this.pacman = pacman;
	}

	public boolean isDestroy() {
		return destroy;
	}

	public void setTargeted() {
		this.targeted = true;
	}

	public boolean isTargeted() {
		return targeted;
	}
}
