package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jam.game.screens.GameScreen;
import com.jam.game.utils.Rando;
import com.jam.game.utils.enums.PlatformType;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class PlatformComponent implements Component, Poolable{
	
	private final float SPECIAL_CHANCE = 0.35f;
	private final float MOVING_CHANCE = 0.50f;
	private final float SECOND_TYPE_CHANCE = 0.45f;
	
	private final PlatformType[] specialPlatformTypes = { PlatformType.OIL, PlatformType.CONVEYOR };
	
	public float x, y, width, height;
	private Body body; // body associated w/ this platform
	
	private int dir = 0; //Used only for moving platforms
	public final float SPEED = 2.0f;
	private PlatformType type;
	
	private PlatformType secondType; //TODO: Make it so moving (or others that are implemented) can have a second type like conveyor to make game harder
		
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
	
	public PlatformType getTrueType(){
		return this.secondType != null ? this.secondType : this.type;
	}
	
	//temp
	public void setType(PlatformType type){
		this.type = type;
	}
	
	public TextureRegion getTextureRegion(){
		switch(this.type){
			case OIL:
				 return new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), 0, 3*32, 32, 7);
			case MOVE:
				return new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), 2*32, 0, 32, 7);
			case DAMAGE:
				return new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), 2*32, 0, 32, 7);
			case NUB:
				return new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), 2*32, 0, 32, 7);
			case WALL:
				return new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), 0, 0, 32, 8);
			case DEFAULT:
				return new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), 2*32, 0, 32, 7);
			default:
				return new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), 0, 0, 32, 8);
		}
	}

	public PlatformType rollForPlatformType(){
		PlatformType type = PlatformType.DEFAULT;
		
		if(Rando.coinFlip()){ //50% chance to roll for moving
			if(Rando.getRandomNumber() <= SPECIAL_CHANCE){
				type = this.specialPlatformTypes[Rando.getRandomBetweenInt(this.specialPlatformTypes.length)];
			}
		}else{
			if(Rando.getRandomNumber() <= MOVING_CHANCE){
				type = PlatformType.MOVE;
			}
		}
		
		if(type == PlatformType.MOVE){
			if(Rando.getRandomNumber() <= SECOND_TYPE_CHANCE){
				secondType = this.specialPlatformTypes[Rando.getRandomBetweenInt(this.specialPlatformTypes.length)];
			}
		}
		
		return type;
	}
	
	public AnimatedBox2DSprite getPlatformAnimation(){
		float aniSpeed = 0.075f;
		Array<TextureRegion> regions;
		AnimatedBox2DSprite sprite;
		
		PlatformType t = this.type;
		
		if(this.secondType != null)
			t = this.secondType;
		
		switch(t){
			case OIL:
				regions = new Array<TextureRegion>();
				
				int extraWait = Rando.getRandomBetweenInt(40);
				
				regions.setSize(8 + extraWait);
				for(int i=1; i<=8 + extraWait; i++){
					if(i > 8){
						regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), 0*32, 2*32, 32, 32));
					}else{
						regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), (i-1)*32, 2*32, 32, 32));
					}
				}
				
				sprite = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
				
				sprite.setUseOrigin(false);
				sprite.setScale(1, 4.0f);
				sprite.setPosition(0, -1.75f);
				
				return sprite;
			case MOVE:
				regions = new Array<TextureRegion>();
				regions.setSize(1);
				
				for(int i=1; i<=1; i++){
					regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), (i-1)*32, 0*32, 32, 8));
				}
				sprite = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));

				return sprite;
			case DAMAGE:
				regions = new Array<TextureRegion>();
				regions.setSize(1);
				regions.set(0, this.getTextureRegion());
				
				regions.add(this.getTextureRegion());
				sprite = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));

				return sprite;
			case NUB:
				regions = new Array<TextureRegion>(3);
				regions.setSize(3);
				for(int i=1; i<=3; i++){
					regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), (i-1)*32, 3*32, 32, 32));
				}
				sprite = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
				sprite.setUseOrigin(false);
				sprite.setScale(2.5f);
				sprite.setPosition(0, 0);
			
				return sprite;
			case WALL:
				regions = new Array<TextureRegion>();
				regions.setSize(1);
				
				for(int i=1; i<=1; i++){
					regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), (i-1)*32, 0*32, 32, 8));
				}
				sprite = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));

				return sprite;
			case CONVEYOR:
				regions = new Array<TextureRegion>();
				regions.setSize(6);
				for(int i=1; i<=6; i++){
					regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), (i-1)*32, 1*32, 32, 6));
				}
				
				sprite = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));

				return sprite;
			case DEFAULT:
				regions = new Array<TextureRegion>();
				regions.setSize(1);
				
				for(int i=1; i<=1; i++){
					regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), (i-1)*32, 0*32, 32, 8));
				}
				sprite = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));

				return sprite;
			default:
				regions = new Array<TextureRegion>();
				regions.setSize(1);
				
				for(int i=1; i<=1; i++){
					regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), (i-1)*32, 0*32, 32, 8));
				}
				sprite = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));

				return sprite;
		}
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
}
