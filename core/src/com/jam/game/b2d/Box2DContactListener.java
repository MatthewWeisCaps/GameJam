package com.jam.game.b2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.jam.game.components.PlatformComponent;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.PowerupComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.systems.PhysicsSystem;
import com.jam.game.utils.EntityManager;
import com.jam.game.utils.Mappers;

public class Box2DContactListener implements ContactListener {

	/*
	 * Body holds Entity
	 * Fixture holds ___
	 */
	
	private PhysicsSystem physicsSystem;;
	
	public Box2DContactListener(PhysicsSystem physicsSystem) {
		this.physicsSystem = physicsSystem;
	}
	
	@Override
	public void beginContact(Contact contact) {
		
		Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();
		
		if(e1 != null && e2 != null){
			if(Mappers.powerupMap.has(e1) && Mappers.playerMap.has(e2)){	
				PowerupComponent puc = Mappers.powerupMap.get(e1);
				PlayerComponent pc = Mappers.playerMap.get(e2);
				
				puc.handleCollisions(pc);
				
				EntityManager.remove(e1);
				
			}else if(Mappers.powerupMap.has(e2) && Mappers.playerMap.has(e1)){
				PowerupComponent puc = Mappers.powerupMap.get(e2);
				PlayerComponent pc = Mappers.playerMap.get(e1);
				
				puc.handleCollisions(pc);
				
				EntityManager.remove(e2);
				
			}else if(Mappers.platformMap.has(e1) && Mappers.platformMap.has(e2)){
				PlatformComponent pc1 = Mappers.platformMap.get(e1);
				PlatformComponent pc2 = Mappers.platformMap.get(e2);
				
				pc1.changeDir();
				pc2.changeDir();
			}else if((Mappers.platformMap.has(e1) && Mappers.playerMap.has(e2)) || (Mappers.playerMap.has(e1) && Mappers.platformMap.has(e2))){
				PlatformComponent pc;
				PlayerComponent player;
				
				if(Mappers.platformMap.get(e1) != null){
					pc = Mappers.platformMap.get(e1);
					player = Mappers.playerMap.get(e2);
				}else{
					pc = Mappers.platformMap.get(e2);
					player = Mappers.playerMap.get(e1);
				}
				
				player.setOnPlatType(pc.getType());
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
					physicsSystem.safelyMarkJointForDestruction(state);
				} else {
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
