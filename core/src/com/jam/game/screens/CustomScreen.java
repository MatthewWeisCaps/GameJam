package com.jam.game.screens;

import com.badlogic.gdx.Screen;
import com.jam.game.managers.FileManager;

public interface CustomScreen extends Screen{
	public void loadAssets(FileManager manager);
}
