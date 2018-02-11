package com.jam.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.jam.game.screens.GameScreen;
import com.jam.game.screens.StartScreen;

public class Game implements ApplicationListener {
	
	private static Screen currentScreen;
	
	private static GameScreen gameScreen;
	
	private static StartScreen startScreen;
	
	
	@Override
	public void create () {
		startScreen = new StartScreen();
		gameScreen = new GameScreen();
		
		currentScreen = startScreen;
		currentScreen.show();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		GdxAI.getTimepiece().update(Gdx.graphics.getDeltaTime());
		currentScreen.render(GdxAI.getTimepiece().getDeltaTime());
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
		
		if(startScreen != null) {
			if(Gdx.input.isKeyJustPressed(Keys.ENTER))
				startScreen.checkStartGameOnEnter();
			if(startScreen.canStartGame()) {
				startScreen = null;
				currentScreen = gameScreen;
				currentScreen.show();
			}
		}
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
