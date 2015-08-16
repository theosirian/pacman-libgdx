package com.theosirian.pacman.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.theosirian.pacman.PacmanGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 19 * 16;
        config.height = 21 * 16;
        new LwjglApplication(new PacmanGame(), config);
    }
}
