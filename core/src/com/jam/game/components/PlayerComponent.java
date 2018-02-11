package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PlayerComponent implements Component, Poolable {

	private final float MIN_DIST = 0;
	private final float MAX_DIST = 100;
	
	public static float dist = 40.0f;
	
	public void changeDist(float amount) {
		if(dist < MIN_DIST) dist = MIN_DIST;
		else if(dist > MAX_DIST) dist = MAX_DIST;
		else dist += amount;
	}
	
	@Override
	public void reset() {
		
	}
	
}
