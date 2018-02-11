package com.jam.game.systems;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;

import controllers.KeyboardController;
import utils.Mappers;
import utils.PlayerAnims;

public class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller;
	int lastXDir = 1;
	int jumpCount = 0;
	
	int MAX_JUMPS = 2;
	
	int speed = 10;
	
	float distSubValue = -0.05f;
	float distAddValue = 0.50f;
	
	@SuppressWarnings("unchecked")
	public PlayerControlSystem(KeyboardController keybrd) {
		super(Family.all(PlayerComponent.class).get(), Priority.INPUT.PRIORITY);
		controller = keybrd;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent body = Mappers.bodyMap.get(entity);
		StateComponent state = Mappers.stateMap.get(entity);
		
		AnimationComponent anim = Mappers.animationMap.get(entity);
		PlayerComponent p = Mappers.playerMap.get(entity);
		
		body.b2dBody.setGravityScale(1);
		
		controller.checkInput();
		
		if(body.b2dBody.getLinearVelocity().y > 0) {
			state.set(StateComponent.STATE_INAIR);
		}else if(body.b2dBody.getLinearVelocity().y < 0){
			body.b2dBody.setGravityScale(3);
		}
		
		if(body.b2dBody.getLinearVelocity().y == 0) {
			jumpCount = 0;
			if(state.get() == StateComponent.STATE_INAIR) {
				state.set(StateComponent.STATE_NORMAL);
			}
			if(body.b2dBody.getLinearVelocity().x != 0) {
				state.set(StateComponent.STATE_MOVING);
			}else if(body.b2dBody.getLinearVelocity().x == 0 && body.b2dBody.getLinearVelocity().y == 0){
				state.set(StateComponent.STATE_NORMAL);
			}
		}
		
		int _physXDir = 0;
		if(controller.left && !controller.right) {
			lastXDir = -1;
			_physXDir = -1;
			body.b2dBody.setLinearVelocity(_physXDir*speed, body.b2dBody.getLinearVelocity().y);
		} else if(controller.right && !controller.left) {
			lastXDir = 1;
			_physXDir = 1;
			body.b2dBody.setLinearVelocity(_physXDir*speed, body.b2dBody.getLinearVelocity().y);
		} else {
			_physXDir = 0;
			body.b2dBody.setLinearVelocity(0.0f, body.b2dBody.getLinearVelocity().y);
		}
		
		
		if(controller.jump && (state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_MOVING || jumpCount < MAX_JUMPS)) {
			body.b2dBody.setLinearVelocity(body.b2dBody.getLinearVelocity().x, 0);
			body.b2dBody.applyLinearImpulse(0, 13.5f, body.b2dBody.getWorldCenter().x,body.b2dBody.getWorldCenter().y, true);
			jumpCount++;
			state.set(StateComponent.STATE_INAIR);
		}
		
		switch(state.get()) {
			case StateComponent.STATE_INAIR:
				p.changeDist(distSubValue);
				anim.currentAnimation = lastXDir > 0 ? PlayerAnims.JUMP_RIGHT : PlayerAnims.JUMP_LEFT;
				break;
			case StateComponent.STATE_MOVING:
				p.changeDist(distSubValue);
				anim.currentAnimation = lastXDir > 0 ? PlayerAnims.WALK_RIGHT : PlayerAnims.WALK_LEFT;
				break;
			case StateComponent.STATE_HIT:
				break;
			default:
				p.changeDist(distAddValue);
				anim.currentAnimation = lastXDir > 0 ? PlayerAnims.IDLE_RIGHT : PlayerAnims.IDLE_LEFT;
				break;
		}		
	}
}
