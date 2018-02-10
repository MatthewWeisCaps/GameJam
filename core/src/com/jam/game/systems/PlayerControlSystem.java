package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;

import Utils.Mappers;
import controllers.KeyboardController;

public class PlayerControlSystem extends IteratingSystem{
	KeyboardController controller;
	
	@SuppressWarnings("unchecked")
	public PlayerControlSystem(KeyboardController keybrd) {
		super(Family.all(PlayerComponent.class).get());
		controller = keybrd;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent body = Mappers.bodyMap.get(entity);
		StateComponent state = Mappers.stateMap.get(entity);
		
		controller.checkInput();
		if(body.b2dBody.getLinearVelocity().y > 0) {
			state.set(StateComponent.STATE_FALLING);
		}
		
		if(body.b2dBody.getLinearVelocity().y == 0) {
			if(state.get() == StateComponent.STATE_FALLING)
				state.set(StateComponent.STATE_NORMAL);
			if(body.b2dBody.getLinearVelocity().x != 0)
				state.set(StateComponent.STATE_MOVING);
		}
		
		if(controller.left) {
			body.b2dBody.setLinearVelocity(MathUtils.lerp(body.b2dBody.getLinearVelocity().x, -5, 0.2f), body.b2dBody.getLinearVelocity().y);
		}
		if(controller.right) {
			body.b2dBody.setLinearVelocity(MathUtils.lerp(body.b2dBody.getLinearVelocity().x, 5, 0.2f), body.b2dBody.getLinearVelocity().y);
		}
		
		if(controller.jump && (state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_MOVING)) {
			body.b2dBody.applyLinearImpulse(0, 7.5f, body.b2dBody.getWorldCenter().x,body.b2dBody.getWorldCenter().y, true);
			state.set(StateComponent.STATE_FALLING);
		}
	}
}
