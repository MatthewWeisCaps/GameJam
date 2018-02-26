package com.jam.game.b2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.systems.PhysicsSystem;
import com.jam.game.utils.EntityManager;
import com.jam.game.utils.Mappers;

public class Box2DContactListener implements ContactListener {

	/*
	 * Body holds Entity
	 * Fixture holds ___
	 */
	
	private World world;
	private PhysicsSystem physicsSystem;;
	
	public Box2DContactListener(World world, PhysicsSystem physicsSystem) {
		this.world = world;
		this.physicsSystem = physicsSystem;
	}
	
	@Override
	public void beginContact(Contact contact) {
		
		Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();
		
		if(e1 != null && e2 != null){
			if(Mappers.powerupMap.has(e1) && Mappers.playerMap.has(e2)){
				EntityManager.remove(e1);
				
				PlayerComponent pc = Mappers.playerMap.get(e2);
				pc.enableLightPowerup();
				
			}else if(Mappers.powerupMap.has(e2) && Mappers.playerMap.has(e1)){
				EntityManager.remove(e2);
				
				PlayerComponent pc = Mappers.playerMap.get(e1);
				pc.enableLightPowerup();
				
			}
		}
		
		for (JointEdge jA : contact.getFixtureA().getBody().getJointList()) {
			for (JointEdge jB : contact.getFixtureB().getBody().getJointList()) {
				if (jA.joint == jB.joint) {
					return; // don't let colls happen on platform being grabbed
				}
			}
		}
		
		
		StateComponent state = null;
		Entity player = null;
		if (e1 != null && Mappers.stateMap.has(e1)) {
			state = Mappers.stateMap.get(e1);
			player = e1;
		} else if (e2 != null && Mappers.stateMap.has(e2)) {
			state = Mappers.stateMap.get(e2);
			player = e2;
		}
		if (state != null) {
			if (state.isSwinging) {
				
				if (state.ropeJoint != null) {
					System.out.println("end 1");
					physicsSystem.safelyMarkJointForDestruction(state);
				} else {
					System.out.println("end 2");
					state.isSwinging = false;
				}
			}
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
