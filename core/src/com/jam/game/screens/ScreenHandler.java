package com.jam.game.screens;

import java.util.HashMap;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.DefaultTimepiece;
import com.badlogic.gdx.ai.GdxAI;
import com.jam.game.Game;

public class ScreenHandler {
	
	private Game g;
	
	private HashMap<ScreenType, Screen> screens;
	
	private ScreenType current;
	
	public ScreenHandler(Game game){
		this.screens = new HashMap<ScreenType, Screen>();
		
		this.g = game;
		this.current = ScreenType.START;
		
		screens.put(ScreenType.START, new StartScreen(this.g));
		screens.put(ScreenType.PAUSE, new PauseScreen(this.g));
		screens.put(ScreenType.PLAY, new GameScreen(this.g));
		screens.put(ScreenType.DEATH, new DeathScreen(this.g));
		
		this.showCurrentScreen();
	}
	
	public void showCurrentScreen(){
		GdxAI.setTimepiece(new DefaultTimepiece());
		this.screens.get(this.current).show();
	}
	
	public void moveToScreenAndDispose(ScreenType newScreen){
		this.screens.get(this.current).dispose();
		
		this.current = newScreen;
		this.screens.put(this.current, this.returnNewScreenType());
		
	}
	public void setCurrentScreen(ScreenType toSet){		
		this.current = toSet;
	}
	
	public Screen getCurrentScreen(){
		return this.screens.get(this.current);
	}
	
	private Screen returnNewScreenType(){
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
}
