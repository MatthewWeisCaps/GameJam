package com.jam.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jam.game.Game;
import com.jam.game.utils.Config;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Config.SCREEN_WIDTH;
		config.height = Config.SCREEN_HEIGHT;
		config.title = "Lantern";
		//config.fullscreen = true;
		new LwjglApplication(new Game(), config);
	}
}
