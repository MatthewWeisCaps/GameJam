package com.jam.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jam.game.screens.GameScreen;
import com.jam.game.systems.LevelSystem;

public class UI {
    private SpriteBatch batch;
    private LevelSystem level;
    
    private BitmapFont font;
    
    
	public UI(SpriteBatch batch, LevelSystem level){
		this.batch = batch;
		this.level = level;

		this.font = new BitmapFont();
		font.setColor(Color.WHITE);
	}
	
	public void draw(){
		this.batch.begin();
//		this.batch.draw(this.numbers, 0, GameScreen.VIRTUAL_HEIGHT * 10 - (GameScreen.VIRTUAL_HEIGHT/3));
		font.draw(this.batch, "Score: " + Integer.toString(this.level.getScore()), 0, GameScreen.VIRTUAL_HEIGHT * 10 - (GameScreen.VIRTUAL_HEIGHT/3));
		this.batch.end();
	}
}
