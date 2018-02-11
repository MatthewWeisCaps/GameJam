package com.jam.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.jam.game.screens.DeathScreen;
import com.jam.game.screens.GameScreen;
import com.jam.game.screens.StartScreen;

public class Game implements ApplicationListener {
	
	private static Screen currentScreen;
	
	private static StartScreen startScreen;
	
	private static GameScreen gameScreen;
	
	private static DeathScreen deathScreen;
		
	@Override
	public void create () {
		setStartScreen();
//		setGameScreen();
//		setDeathScreen();
	}
	
	void setStartScreen() {
		if(currentScreen != null) currentScreen.dispose();
		startScreen = new StartScreen(this);
		setScreen(startScreen);
	}

	void setGameScreen(){
		if(currentScreen != null) currentScreen.dispose();
		gameScreen = new GameScreen(this);
		setScreen(gameScreen);
	}
	
	void setDeathScreen() {
		if(currentScreen != null) currentScreen.dispose();
		deathScreen = new DeathScreen(this);
		setScreen(deathScreen);
	}
	
	void setScreen(Screen screen) {
		currentScreen = screen;
		currentScreen.show();
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (currentScreen == gameScreen && gameScreen.doneLoading) {
			GdxAI.getTimepiece().update(Gdx.graphics.getDeltaTime());
			currentScreen.render(GdxAI.getTimepiece().getDeltaTime());
			
			if(gameScreen.playerDeath && gameScreen.finishedDeathStuff) {
				//gameScreen.dispose();
				setDeathScreen();
			}
		} else {
			currentScreen.render(Gdx.graphics.getDeltaTime());
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
		
		if(currentScreen == startScreen) {
			if(Gdx.input.isKeyJustPressed(Keys.ENTER))
				startScreen.checkStartGameOnEnter();
			if(startScreen.canStartGame()) {
				//startScreen.dispose();
				//currentScreen.pause();
				setGameScreen();
			}
		}
		
		/*if(currentScreen == deathScreen) {
			if(deathScreen.playAgain) {
				//startScreen.dispose();
				//gameScreen.dispose();
//				setGameScreen();
				setStartScreen();
			}
		}*/
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
