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
	
	public ScreenHandler(Game game){
		this.screens = new HashMap<ScreenType, CustomScreen>();
		
		this.g = game;
		this.current = ScreenType.START;
		
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
	
	private CustomScreen returnNewScreenType(){
		switch(this.current){
		
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
