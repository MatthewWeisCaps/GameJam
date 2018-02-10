package com.jam.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.jam.game.screens.GameScreen;

public class Game implements ApplicationListener {
	
	private static GameScreen currentScreen;
	private static GameScreen gameScreen;
	
	
	@Override
	public void create () {
		gameScreen = new GameScreen();
		currentScreen = gameScreen;
		
		currentScreen.show();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		GdxAI.getTimepiece().update(Gdx.graphics.getDeltaTime());
		currentScreen.render(GdxAI.getTimepiece().getDeltaTime());
	}
	
	@Override
	public void dispose () {
		currentScreen.dispose();
	}

	@Override
	public void resize(int width, int height) {
		currentScreen.resize(width, height);
	}

	@Override
	public void pause() {
		currentScreen.pause();
	}

	@Override
	public void resume() {
		currentScreen.resume();
	}
}
