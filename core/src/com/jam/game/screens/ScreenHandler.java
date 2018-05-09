package com.jam.game.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.DefaultTimepiece;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.utils.Disposable;
import com.jam.game.Game;
import com.jam.game.managers.FileManager;
import com.jam.game.utils.enums.ScreenType;

public class ScreenHandler implements Disposable {
	
	private Game g;
	
	private HashMap<ScreenType, CustomScreen> screens;
	
	private ScreenType current;
	
	private FileManager manager;
	
	public boolean isPaused;
	
	public ScreenHandler(Game game){
		this.screens = new HashMap<ScreenType, CustomScreen>();
		
		this.g = game;
		this.current = ScreenType.SPLASH;
		
		this.isPaused = false;
		
		screens.put(ScreenType.SPLASH, new SplashScreen(this.g));
		screens.put(ScreenType.START, new StartScreen(this.g));
		screens.put(ScreenType.PAUSE, new PauseScreen(this.g));
		screens.put(ScreenType.PLAY, new GameScreen(this.g));
		screens.put(ScreenType.DEATH, new DeathScreen(this.g));
		
		this.manager = new FileManager();
		
		this.showCurrentScreen();
	}
	
	public void showCurrentScreen(){
		GdxAI.setTimepiece(new DefaultTimepiece());
		
		this.screens.get(this.current).loadAssets(this.manager);
		this.screens.get(this.current).show();
		this.screens.get(this.current).resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void tryShowPauseScreen(float dt){
		
		if(this.isPaused){
			GdxAI.setTimepiece(new DefaultTimepiece());
			this.screens.get(ScreenType.PAUSE).render(dt);
		}
	}
	
	public void moveToScreenAndDispose(ScreenType newScreen){
		this.screens.get(this.current).dispose();
		
		this.current = newScreen;
		
		this.screens.put(this.current, this.returnNewScreenType());
	}
	
	public void setCurrentScreen(ScreenType toSet){		
		this.current = toSet;
	}
	
	public CustomScreen getCurrentScreen(){
		return this.screens.get(this.current);
	}
	
	public void pauseCurrentScreen(){
		if(this.current != ScreenType.PLAY)
			return;
		this.isPaused = !this.isPaused;
		
		if(this.isPaused){
			this.screens.get(ScreenType.PAUSE).loadAssets(this.manager);
			this.screens.get(ScreenType.PAUSE).show();
			this.screens.get(ScreenType.PAUSE).resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		}else{
			this.screens.get(ScreenType.PAUSE).dispose();
		}
		
		/*if(isPaused){
			this.getCurrentScreen().pause();
		}else{
			this.getCurrentScreen().resume();
		}*/
	}
	
	private CustomScreen returnNewScreenType(){
		switch(this.current){
		
		case SPLASH:
			return new SplashScreen(g);
		case START:
			return new StartScreen(g);
		case PLAY:
			return new GameScreen(g);
		case PAUSE:
			return new PauseScreen(g);
		case DEATH:
			return new DeathScreen(g);
		default:
			return null;
		}
	}

	@Override
	public void dispose() {
		getCurrentScreen().dispose();
		this.manager.dispose();
	}
}
