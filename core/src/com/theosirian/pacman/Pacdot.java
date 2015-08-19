package com.theosirian.pacman;

public class Pacdot extends Entity{

    protected Pacman pacman;
    protected boolean destroy;

    protected int worth;

    public Pacdot(int x, int y, Pacman player) {
        super(x, y, player != null ? player.getCollisionLayer() : null);
        pacman = player;
        worth = 0;
    }

	@Override
	public void update(float delta){
		if (pacman.getBounds().contains(getBounds())){
			pacman.changeScore(worth);
			this.destroy = true;
		}
	}

    public Pacman getPacman() {
        return pacman;
    }

    public void setPacman(Pacman pacman) {
        this.pacman = pacman;
    }

}
