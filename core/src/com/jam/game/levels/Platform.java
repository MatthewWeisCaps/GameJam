package com.jam.game.levels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.jam.game.utils.enums.PlatformType;

public class Platform {
	
	public float x, y, width, height;
	private Body body; // body associated w/ this platform
	private PlatformType type;
	
	private int screenSegment;
	
	public Platform() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public Platform(float x, float y, float width, float height) {
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
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
	
	public void setSegment(int seg){
		this.screenSegment = seg;
	}
	
	public int getSegment(){
		return this.screenSegment;
	}
	
	public PlatformType getType(){
		return this.type;
	}
	
	public TextureRegion getTextureRegion(){
		return this.type.getTextureRegion();
	}
	
}
