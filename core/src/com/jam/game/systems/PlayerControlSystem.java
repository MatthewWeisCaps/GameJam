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
	
	@SuppressWarnings("unchecked")
	public PlayerControlSystem(KeyboardController keybrd) {
		super(Family.all(PlayerComponent.class).get());
		controller = keybrd;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent body = Mappers.bodyMap.get(entity);
		StateComponent state = Mappers.stateMap.get(entity);
		
		AnimationComponent anim = Mappers.animationMap.get(entity);
		
		controller.checkInput();
		
		if(body.b2dBody.getLinearVelocity().y > 0) {
			state.set(StateComponent.STATE_FALLING);
		}
		
		if(body.b2dBody.getLinearVelocity().y == 0) {
			jumpCount = 0;
			if(state.get() == StateComponent.STATE_FALLING) {
				state.set(StateComponent.STATE_NORMAL);
			}
			if(body.b2dBody.getLinearVelocity().x != 0) {
				state.set(StateComponent.STATE_MOVING);
			}else{
				state.set(StateComponent.STATE_NORMAL);
			}
		}
		
		if(controller.left) {
			lastXDir = -1;
			body.b2dBody.setLinearVelocity(MathUtils.lerp(body.b2dBody.getLinearVelocity().x, -5, 0.2f), body.b2dBody.getLinearVelocity().y);
		}
		if(controller.right) {
			lastXDir = 1;
			body.b2dBody.setLinearVelocity(MathUtils.lerp(body.b2dBody.getLinearVelocity().x, 5, 0.2f), body.b2dBody.getLinearVelocity().y);
		}
		
		if(controller.jump && (state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_MOVING || jumpCount < MAX_JUMPS)) {
			body.b2dBody.setLinearVelocity(body.b2dBody.getLinearVelocity().x, 0);
			body.b2dBody.applyLinearImpulse(0, 7.5f, body.b2dBody.getWorldCenter().x,body.b2dBody.getWorldCenter().y, true);
			jumpCount++;
			state.set(StateComponent.STATE_FALLING);
		}
		
		switch(state.get()) {
			case StateComponent.STATE_FALLING:
				anim.currentAnimation = lastXDir > 0 ? PlayerAnims.JUMP_RIGHT : PlayerAnims.JUMP_LEFT;
				break;
			case StateComponent.STATE_JUMPING:
				anim.currentAnimation = lastXDir > 0 ? PlayerAnims.JUMP_RIGHT : PlayerAnims.JUMP_LEFT;
				break;
			case StateComponent.STATE_MOVING:
				anim.currentAnimation = lastXDir > 0 ? PlayerAnims.WALK_RIGHT : PlayerAnims.WALK_LEFT;
				break;
			default:
				anim.currentAnimation = lastXDir > 0 ? PlayerAnims.IDLE_RIGHT : PlayerAnims.IDLE_LEFT;
				break;
		}
	}
}
