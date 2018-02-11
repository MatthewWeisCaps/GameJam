package com.jam.game.levels;

import com.badlogic.gdx.physics.box2d.Body;

public class Platform {
	
	public float x, y, width, height;
	private Body body; // body associated w/ this platform
	
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
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
	
}
