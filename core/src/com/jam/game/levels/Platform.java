package com.jam.game.levels;

public class Platform {
	
	public float x, y, width, height;
	
	public Platform() {
		this(0.0f, 0.0f, 0.0f);
		this.height = 1.5f;
	}
	
	public Platform(float x, float y, float width) {
		set(x, y, width);
	}
	
	public boolean inRange(float _x, float _width) {
		return !(_x + _width < x || x + width < _x);
	}
	
	public void set(float x, float y, float width) {
		this.x = x;
		this.y = y;
		this.width = width;
	}
	
}
