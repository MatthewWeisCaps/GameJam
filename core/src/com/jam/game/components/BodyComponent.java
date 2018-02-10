package com.jam.game.components;

import com.badlogic.ashley.core.Component;

import com.badlogic.gdx.physics.box2d.Body;

public class BodyComponent implements Component {
	public Body b2dBody;
	
	public BodyComponent() {
		
	}
	
	public BodyComponent(Body body) {
		b2dBody = body;
	}
}
