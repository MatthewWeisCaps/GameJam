package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class StateComponent implements Component, Poolable {
	public static final int STATE_NORMAL = 0;
	public static final int STATE_INAIR = 1;
	public static final int STATE_MOVING = 2;
	public static final int STATE_HIT = 3;
	
	private int state = 0;
	public float time = 0.0f;
	public boolean isLooping = false;
	
	public void set(int newState){
		this.state = newState;
		time = 0.0f;
	}
	
	public int get() {
		return this.state;
	}

	@Override
	public void reset() {
		state = 0;
		time = 0.0f;
		isLooping = false;
	}
	
}
