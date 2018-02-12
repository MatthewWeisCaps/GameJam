package com.jam.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jam.game.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 650;
		config.height = 800;
		config.title = "Lantern";
		//config.fullscreen = true;
		new LwjglApplication(new Game(), config);
	}
}
