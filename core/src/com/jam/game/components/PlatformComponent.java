package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jam.game.screens.GameScreen;
import com.jam.game.utils.Rando;
import com.jam.game.utils.enums.PlatformType;

public class PlatformComponent implements Component, Poolable{
	
	private final float OIL_CHANCE = 0.25f;
	private final float MOVING_CHANCE = 0.50f;
	
	public float x, y, width, height;
	private Body body; // body associated w/ this platform
	
	private int dir = 0; //Used only for moving platforms
	public final float SPEED = 2.0f;
	private PlatformType type;
		
	public PlatformComponent() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public PlatformComponent(float x, float y, float width, float height) {
		set(x, y, width, height);
	}
	
	public boolean inRange(float _x, float _width) {
		return !(_x + _width < x || x + width < _x);
	}
	
	public void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = PlatformType.DEFAULT;
	}
	
	public void set(float x, float y, float width, float height, PlatformType type) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		
		
		if(this.type == PlatformType.MOVE) {
			this.dir = Rando.coinFlip() ? 1 : -1;
		}
	}

	public void checkChangeDir(float x){	
		if(x - this.width - this.width/2 <= 0 || x  +this.width + this.width/2 >= GameScreen.VIRTUAL_WIDTH){
			this.changeDir();
		}
	}
	
	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
		this.body.setFixedRotation(true);
	}
	
	public void changeDir(){		
		this.dir = (-1) * this.dir;
	}
	
	public int getDir(){
		return this.dir;
	}
	
	public PlatformType getType(){
		return this.type;
	}
	
	//temp
	public void setType(PlatformType type){
		this.type = type;
	}
	
	public TextureRegion getTextureRegion(){
		return this.type.getTextureRegion();
	}

	public PlatformType rollForPlatformType(){
		PlatformType type = PlatformType.DEFAULT;
		
		if(Rando.coinFlip()){ //50% chance to roll for moving
			if(Rando.getRandomNumber() <= OIL_CHANCE){
				type = PlatformType.OIL;
			}
		}else{
			if(Rando.getRandomNumber() <= MOVING_CHANCE){
				type = PlatformType.MOVE;
			}
		}
		return type;
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
}
