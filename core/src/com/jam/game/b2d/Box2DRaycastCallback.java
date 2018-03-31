package com.jam.game.b2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class Box2DRaycastCallback implements RayCastCallback {
	
//	private final float ropeLengthSquared;
	
	public boolean didCollide = false;
	public Vector2 point, normal;
	public float fraction;
	public Fixture fixture;
	
	private Body ignoredBody;
	
	// call with no body ignore (except defaults from World.rayCast)
	public Box2DRaycastCallback() {
		this(null);
	}
	
	// call with body to ignore, must check body's fixtures in linear time
	public Box2DRaycastCallback(Body ignoredBody) {
		this.ignoredBody = ignoredBody;
	}
	
	/*
	 * from super:
	 * 
	 * Called for each fixture found in the query.
	 * You control how the ray cast proceeds by returning a float:
	 * 		return -1: ignore this fixture and continue
	 * 		return 0: terminate the ray cast return fraction: clip the ray to this point
	 * 		return 1: don't clip the ray and continue.
	 * The Vector2 instances passed to the callback will be reused for future calls so make a copy of them!
	 */
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		
		if (ignoredBody != null) {
			if (ignoredBody.getFixtureList().contains(fixture, true)) {
				return -1;
			}
		}
		
		didCollide = true;
		this.fixture = fixture;
		this.point = point;
		this.normal = normal;
		this.fraction = fraction;
		
		return fraction;
	}

}
