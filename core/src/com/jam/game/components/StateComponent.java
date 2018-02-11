package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.Pool.Poolable;

public class StateComponent implements Component, Poolable {
	public static final int STATE_NORMAL = 0;
	public static final int STATE_INAIR = 1;
	public static final int STATE_MOVING = 2;
	public static final int STATE_HIT = 3;
//	public static final int STATE_SWINING = 4; // swinging from rope
//	public static final int STATE_LAUNCHING = 5; // time after rope release before touching group (where momentum carries)
	
	private int state = 0;
	public float time = 0.0f;
	public boolean isLooping = false;
	
	public boolean isSwinging = false, isThrowingRope = false;
	public float beginThrowingRopeTime = 0.0f;
	public RopeJointDef ropeJointDef = null; // for throw
	public RopeJoint ropeJoint = null;
	public boolean validThrow = false;
	public Vector2 invalidThrowStart = null, invalidThrowEnd = null;
	
	public float getRopeDistance() {
		return ropeJoint.getBodyA().getWorldPoint(ropeJoint.getLocalAnchorA()).dst(ropeJoint.getBodyB().getWorldPoint(ropeJoint.getLocalAnchorB()));
	}
	
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
		// TODO: clean ropeJoint...
		
		isSwinging = false;
		isThrowingRope = false;
		beginThrowingRopeTime = 0.0f;
		ropeJointDef = null; // for throw
		ropeJoint = null;
		validThrow = false;
		invalidThrowStart = null;
		invalidThrowEnd = null;
	}
	
}
