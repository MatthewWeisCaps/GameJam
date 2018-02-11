package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jam.game.b2d.Box2DRaycastCallback;
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
	
	public static final float MAX_ROPE_LENGTH = 10.0f;
	public static final float ROPE_CAST_TIME = 0.80f;
	
	private final float swingForce = 30.0f;
	private final Vector2 swingForce_left = new Vector2(-swingForce, 0.0f);
	private final Vector2 swingForce_right = new Vector2(swingForce, 0.0f);
	
	private World world;
	OrthographicCamera cam;
	
	@SuppressWarnings("unchecked")
	public PlayerControlSystem(KeyboardController keybrd, World world, OrthographicCamera cam) {
		super(Family.all(PlayerComponent.class).get(), Priority.INPUT.PRIORITY);
		controller = keybrd;
		this.world = world;
		this.cam = cam;
	}
	
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent body = Mappers.bodyMap.get(entity);
		StateComponent state = Mappers.stateMap.get(entity);
		
		AnimationComponent anim = Mappers.animationMap.get(entity);
		PlayerComponent p = Mappers.playerMap.get(entity);
		
		body.b2dBody.setGravityScale(1);
		
		controller.checkInput();
		
		// BEGIN HANDLING THROWING
		
		if (state.isThrowingRope) {
			
			if (state.beginThrowingRopeTime + ROPE_CAST_TIME < GdxAI.getTimepiece().getTime()) {
				
				if (state.validThrow) {
					// done throwing rope, we connect
					state.isThrowingRope = false;
					state.beginThrowingRopeTime = 0.0f;
					state.ropeJoint = (RopeJoint) world.createJoint(state.ropeJointDef);
					state.isSwinging = true;
				} else {
					// fail to connect
					state.isThrowingRope = false;
					state.beginThrowingRopeTime = 0.0f;
					state.isSwinging = false;
				}
				
				
			} else {
				return;
			}
			
			if (Math.abs(body.b2dBody.getLinearVelocity().y) <= 0.0000000000005f) { // if on ground
				body.b2dBody.setLinearVelocity(Vector2.Zero);
			}
		}
		
		// END HANDLING THROWING
		
		
		// BEGIN HANDLING SWINGING
		
		if (state.isSwinging && state.ropeJoint != null) { // swinging, and we have rope. Since ropeJoint will equal null after this branch, it is only called once per release
			
			body.b2dBody.setGravityScale(5.0f);
			
			if (Gdx.input.isKeyJustPressed(Keys.E)) { // cancel rope, but not isSwinging. isSwinging turns off upon landing, see contact listener
				
				System.out.println("end 1 (handled by playerController)");
				this.getEngine().getSystem(PhysicsSystem.class).safelyMarkJointForDestruction(state);
				
//				body.b2dBody.applyForceToCenter(body.b2dBody.getLinearVelocity().cpy().scl(3.0f), true);
				body.b2dBody.getLinearVelocity().scl(6.0f);
				
				
			} else if (controller.left && !controller.right) {
				body.b2dBody.applyForceToCenter(swingForce_left, true);
				p.changeDist(distSubValue);
				anim.currentAnimation = PlayerAnims.JUMP_LEFT;
			} else if(controller.right && !controller.left) {
				body.b2dBody.applyForceToCenter(swingForce_right, true);
				p.changeDist(distSubValue);
				anim.currentAnimation = PlayerAnims.JUMP_RIGHT;
			} else {
				
			}
			
			return;
		} else if (state.isSwinging && state.ropeJoint == null) { // released rope, yet to touch ground...
			
			body.b2dBody.setGravityScale(2.0f);
			
			if (Math.abs(body.b2dBody.getLinearVelocity().y) <= 0.0000000000005f) {
				// if not falling but isSwinging is true, then InvalidState!
				// this invalid state happens when the floor/platform breaks phase 1 of rope swing which player then..
				// .. stands on without jumping. This results in being unable to re-throw rope.
				System.out.println("end 2 (handled by playerController)");
				state.isSwinging = false;
				return;
			}
			
			if (controller.left && !controller.right) {
				body.b2dBody.setLinearVelocity(-speed*1.25f, body.b2dBody.getLinearVelocity().y);
				p.changeDist(distSubValue);
				anim.currentAnimation = PlayerAnims.JUMP_LEFT;
				return;
			} else if(controller.right && !controller.left) {
				body.b2dBody.setLinearVelocity(speed*1.25f, body.b2dBody.getLinearVelocity().y);
				p.changeDist(distSubValue);
				anim.currentAnimation = PlayerAnims.JUMP_RIGHT;
				return;
			}
		} else {
			body.b2dBody.setGravityScale(1.0f);
		}
		
		// END HANDLING SWINGING
		
		if(body.b2dBody.getLinearVelocity().y > 0) {
			state.set(StateComponent.STATE_INAIR);
			body.b2dBody.setGravityScale(1); // MATT: changed from 1 -> 2
		} else if(body.b2dBody.getLinearVelocity().y < 0){
			body.b2dBody.setGravityScale(3); // MATT: changed from 2 -> 4
		}
		
		
		if(Math.abs(body.b2dBody.getLinearVelocity().y) <= 0.0000000000005f) { // instead of comparing to 0, allow small room for error
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
		
		// INDIANA JONES STYLE
		
		if (Gdx.input.isKeyJustPressed(Keys.Q)) { // rope
			
			FitViewport viewport = this.getEngine().getSystem(RenderingSystem.class).getViewport();
			Camera cam = viewport.getCamera();
			cam.update();
			Vector3 coords3D = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
			Vector2 coords = new Vector2(coords3D.x, coords3D.y);
					
			Box2DRaycastCallback callback = new Box2DRaycastCallback();
			world.rayCast(callback, body.b2dBody.getPosition(), coords);
			
			
			// if we collided AND distance between player and coll is less than max rope length
			if (callback.didCollide) {
				
				// WAKE UP PLAYER OR HE WILL NOT MOVE!
				body.b2dBody.setAwake(true);
				
				RopeJointDef jointDef = new RopeJointDef();
				jointDef.bodyA = body.b2dBody;
				jointDef.bodyB = callback.fixture.getBody();
				jointDef.collideConnected = true;
				jointDef.localAnchorA.set(Vector2.Zero);
				jointDef.localAnchorB.set(callback.fixture.getBody().getLocalPoint(callback.point));

				
				// make length of rope equal to dist between player and fixture when rope is thrown (to avoid awkward drop-off)
				final float distance = jointDef.bodyA.getWorldPoint(jointDef.localAnchorA).dst(jointDef.bodyB.getWorldPoint(jointDef.localAnchorB));
				
//				System.out.println(distance + " vs. " + MAX_ROPE_LENGTH);
//				System.out.println("=> " + jointDef.maxLength);
				
				if (distance < MAX_ROPE_LENGTH) {
					
					// rope len between 3.0 to MAX_ROPE_LENGTH, but prefers user's grab distance when possible
					jointDef.maxLength = Math.max(3.0f, Math.min(distance, MAX_ROPE_LENGTH));
					
					
					// cut some length off rope if player grounded so they can still swing.
//					if (!(state.get() != StateComponent.STATE_INAIR)) {
//						jointDef.maxLength *= 0.90;
//					} else {
//						jointDef.maxLength *= 0.90;
//					}
					
//					state.ropeJoint = (RopeJoint) world.createJoint(jointDef);
//					state.isSwinging = true;
					
					state.validThrow = true;
				} else {
					jointDef.maxLength = MAX_ROPE_LENGTH;
					state.validThrow = false;
				}
				
				state.invalidThrowStart = null;
				state.invalidThrowEnd = null;
				
				state.beginThrowingRopeTime = GdxAI.getTimepiece().getTime();
				state.ropeJointDef = jointDef;
				state.isThrowingRope = true;
			} else {
//				float angle = body.b2dBody.getPosition().angle(coords);
//				float dx = Math.abs(body.b2dBody.getPosition().x - coords.x);
//				float dy = Math.abs(body.b2dBody.getPosition().y - coords.y);
//				
				float x = (body.b2dBody.getPosition().x+coords.x);//(float) Math.sqrt(MAX_ROPE_LENGTH*MAX_ROPE_LENGTH - dy*dy);
				float y = (body.b2dBody.getPosition().y+coords.y);//(float) Math.sqrt(MAX_ROPE_LENGTH*MAX_ROPE_LENGTH - dx*dx);
				
				Vector2 missCoords = new Vector2(x, y);
				
				
				float ratio = 0.5f;//MAX_ROPE_LENGTH / coords.dst(body.b2dBody.getPosition());
				
				missCoords.scl(ratio); // TODO fix len of missed ropes w/ no raycast return
				
				
//				Vector2 missCoords = coords.cpy().sub(body.b2dBody.getPosition()).rotate90(1); //body.b2dBody.getPosition().cpy().sub(coords);
				
//				RopeJointDef jointDef = new RopeJointDef(); // joint def not filled in, but ok because never used for creation in this instance
//				jointDef.maxLength = MAX_ROPE_LENGTH;
				state.validThrow = false;
				state.beginThrowingRopeTime = GdxAI.getTimepiece().getTime();
				state.ropeJointDef = null;
				state.invalidThrowStart = body.b2dBody.getPosition();
				state.invalidThrowEnd = missCoords;
				state.isThrowingRope = true;
			}
		}
		
	}
}
