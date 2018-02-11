package com.jam.game.components;

import com.badlogic.ashley.core.Component;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BodyComponent implements Component, Poolable {
	public Body b2dBody;
	
	public BodyComponent() {
		
	}
	
	public BodyComponent(Body body) {
		b2dBody = body;
	}

	@Override
	public void reset() {
		b2dBody = null;
	}
}
