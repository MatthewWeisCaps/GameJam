package com.jam.game.b2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class Box2DRaycastCallback implements RayCastCallback {
	
//	private final float ropeLengthSquared;
	
	public boolean didCollide = false;
	public Vector2 point, normal;
	public float fraction;
	public Fixture fixture;
	
//	public Box2DRaycastCallback(float ropeLength) {
//		ropeLengthSquared = ropeLength*ropeLength;
//	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {		
		didCollide = true;
		this.fixture = fixture;
		this.point = point;
		this.normal = normal;
		this.fraction = fraction;
		
		return fraction;
	}

}
