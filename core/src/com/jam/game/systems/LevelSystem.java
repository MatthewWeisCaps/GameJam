package com.jam.game.systems;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.PowerupComponent;
import com.jam.game.components.TransformComponent;
import com.jam.game.levels.Level;
import com.jam.game.levels.Platform;
import com.jam.game.powerup.Powerup;
import com.jam.game.screens.GameScreen;
import com.jam.game.utils.Rando;

import box2dLight.RayHandler;
import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class LevelSystem extends EntitySystem {
	
	private static final TextureRegion PLATFORM_TEXTURE = new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 7);
	
	RayHandler lightRayHandler;
	
	private float camSpeed = 1.60f;
	private float camSpeedMax = 2.75f;
	private float camSpeedIncrease = 0.00001f;
//	private final static float INTERVAL = 1.0f/15.0f;
	private Level level;
	
	private OrthographicCamera cam;
	private float yHeight = 0.0f;
	
//	private float ySpeedInitial = 20.0f;
//	private float ySpeedCoefficient = 0.8f;
	
	// height y = ySpeedCoefficient*(total time passed) + ySpeedInitial
	
	private final float ITEM_CHANCE = 0.80f; //must be larger than
	
	public LevelSystem(OrthographicCamera cam, Body playerBody, Level level) {
		super(Priority.PHYSICS.PRIORITY);
		this.level = level;
		this.cam = cam;
	}
	
	Random random = new Random();
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		// calc new y
		yHeight = calculateYHeight();//Math.max(calculateYHeight(), playerBody.getPosition().y);
		
		// interpolate
//		cam.position.y = Interpolation.smooth.apply(cam.position.y, yHeight, delta);
		
//		// move camera
		cam.position.set(cam.position.x, yHeight, cam.position.z);
		
		/*!cam.frustum.pointInFrustum(platform.getBody().getLocalCenter().x, platform.getBody().getLocalCenter().y, 0.0f) */
		// check lowest platform for removal, if so remove its body and entity from engine and world
		Platform platform = level.getTail();
		if (platform != null && platform.getBody() != null && platform.getBody().getUserData() != null) {
			
			FitViewport viewport = this.getEngine().getSystem(RenderingSystem.class).getViewport();
			Camera cam = viewport.getCamera();
			cam.update();
			Vector3 coords3D = cam.project(new Vector3(platform.getBody().getPosition(), 0.0f), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
			
//			Vector2 coords = new Vector2(coords3D.x, coords3D.y);
			

			
			if (coords3D.y < -5.0f) {
				
//				System.out.println(cam.project(new Vector3(platform.getBody().getPosition(), 0.0f)).y);
						
				Entity entity = (Entity) platform.getBody().getUserData();
								
				this.getEngine().removeEntity(entity); // remove entity from engine
				
				level.getAndRemoveTail(); // removes body from world, and platform from level queue
				
			}
		}
		
		// check if new platform needs to be spawned
		while (level.getPlatformQueue().size < 30) {
			PooledEngine engine = (PooledEngine)this.getEngine(); // ref engine
			
			
			for (Body b : level.spawnNext2(engine)) {
				
				Entity entity = engine.createEntity(); // make entity
				
				BodyComponent bodyC = engine.createComponent(BodyComponent.class); // make components
				AnimationComponent animC = engine.createComponent(AnimationComponent.class);
				TransformComponent transC = engine.createComponent(TransformComponent.class);
				
				bodyC.b2dBody = b;
				bodyC.b2dBody.setUserData(entity);
				
				Array<TextureRegion> one = new Array<TextureRegion>();
				one.setSize(1);
				one.set(0, PLATFORM_TEXTURE);
				
				one.add(LevelSystem.PLATFORM_TEXTURE);
				
				final String def = "DEFAULT";
				animC.animations.put(def, new AnimatedBox2DSprite(new AnimatedSprite(
						new Animation<TextureRegion>(0.0f, one, PlayMode.NORMAL))));
				animC.currentAnimation = def;
								
				entity.add(bodyC);
				entity.add(animC);
				entity.add(transC);
				
				engine.addEntity(entity);
				
				//Roll for powerups on the platform
				if(Rando.getRandomNumber() > this.ITEM_CHANCE){
					Entity powerUpEntity = engine.createEntity();
		
					BodyComponent bodyPUC = engine.createComponent(BodyComponent.class); // make components
					AnimationComponent animPUC = engine.createComponent(AnimationComponent.class);
					TransformComponent transPUC = engine.createComponent(TransformComponent.class);
					PowerupComponent powerupPUC = engine.createComponent(PowerupComponent.class);
					
					Powerup p = this.level.spawnPowerUp(bodyC.b2dBody.getPosition().x, bodyC.b2dBody.getPosition().y, engine);
					
					bodyPUC.b2dBody = p.getBody();
					bodyPUC.b2dBody.setUserData(powerUpEntity);
					
					animPUC.animations.put(def, Powerup.getAnimation(p.type));
					animPUC.currentAnimation = def;
					
					powerupPUC.powerup = p;
										
					powerUpEntity.add(bodyPUC);
					powerUpEntity.add(animPUC);
					powerUpEntity.add(transPUC);
					powerUpEntity.add(powerupPUC);
					
					engine.addEntity(powerUpEntity);
					System.out.println("Added powerup!");
				}
				
			}
			
////			bodyC.b2dBody = level.spawnNext((PooledEngine) this.getEngine()); // populate components
//			bodyC.b2dBody.setUserData(entity);
//			
////			texC.region = LevelSystem.PLATFORM_TEXTURE;
//			Array<TextureRegion> one = new Array<TextureRegion>();
//			one.setSize(1);
//			one.set(0, PLATFORM_TEXTURE);
//			
////			one.add(LevelSystem.PLATFORM_TEXTURE);
//			
//			System.out.println(one.toString(", "));
//			final String def = "DEFAULT";
//			animC.animations.put(def, new AnimatedBox2DSprite(new AnimatedSprite(
//					new Animation<TextureRegion>(0.0f, one, PlayMode.NORMAL))));
//			animC.currentAnimation = def;
//			
//			entity.add(bodyC);
////			entity.add(texC);
//			entity.add(animC);
//			entity.add(transC);
//			
//			engine.addEntity(entity);
//			
//			System.out.println("spawned platform.");
		}
		
	}

	public float getYHeight() {
		return yHeight;
	}
	
	// test 1
	private float calculateYHeight() {
		camSpeed += camSpeedIncrease;
		
		if(camSpeed > camSpeedMax) camSpeed = camSpeedMax;
		
		return camSpeed * (float)Math.pow(GdxAI.getTimepiece().getTime(), 1.14f);
	}
}
