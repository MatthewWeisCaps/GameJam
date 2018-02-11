package com.jam.game.b2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import utils.Mappers;

public class Box2DContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		
		boolean ret = true;
		
		if(ret) return;
		
		/*
		 * Body holds Entity
		 * Fixture holds ___
		 */
		
		Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();
		
		
		
		if (Mappers.playerMap.has(e1)) {
			
		} else if (Mappers.playerMap.has(e2)) {
			
		}
	}

	@Override
	public void endContact(Contact contact) {
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
	
}
