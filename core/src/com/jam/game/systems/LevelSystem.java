package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.physics.box2d.Body;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.TextureComponent;
import com.jam.game.components.TransformComponent;
import com.jam.game.levels.Level;
import com.jam.game.levels.Platform;
import com.jam.game.screens.GameScreen;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;

public class LevelSystem extends EntitySystem {
	
	private static final TextureRegion PLATFORM_TEXTURE = new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 32);
	
//	private final static float INTERVAL = 1.0f/15.0f;
	private Level level;
	private OrthographicCamera cam;
	private float yHeight = 0.0f;
	private Body playerBody;
	
//	private float ySpeedInitial = 20.0f;
//	private float ySpeedCoefficient = 0.8f;
	
	// height y = ySpeedCoefficient*(total time passed) + ySpeedInitial
	
	public LevelSystem(OrthographicCamera cam, Body playerBody, Level level) {
		this.level = level;
		this.cam = cam;
		this.playerBody = playerBody;
		
		// start by adding 10 platforms to level
		initPlatforms();
	}

//	protected void updateInterval() {
//		Platform platform = level.getTail();
//		if (cam.frustum.pointInFrustum(platform.getBody().getLocalCenter().x, platform.getBody().getLocalCenter().y, 0.0f)) {
//			level.getAndRemoveTail();
//		}
//	}
	
	private void initPlatforms() {		
		for (int i=0; i < 10; i++) {
			level.spawnNext((PooledEngine) this.getEngine());
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		// calc new y
		yHeight = Math.max(calculateYHeight(), playerBody.getPosition().y);
		
		// interpolate
		cam.position.y = Interpolation.smoother.apply(cam.position.y, yHeight, deltaTime);
				
//		// move camera
//		cam.position.set(cam.position.x, yHeight, cam.position.z);
		
		// check lowest platform for removal, if so remove its body and entity from engine and world
		Platform platform = level.getTail();
		if (platform != null && platform.getBody() != null && platform.getBody().getUserData() != null) {
			if (cam.frustum.pointInFrustum(platform.getBody().getLocalCenter().x, platform.getBody().getLocalCenter().y, 0.0f)) {
				Entity entity = (Entity) platform.getBody().getUserData();
				
				System.out.println(platform.getBody() + ", " + platform.getBody().getPosition());
				
				System.out.println(platform.getBody().getUserData() + ", " + entity + ", " + this.getEngine() + ", " + platform.getBody().getUserData());
				this.getEngine().removeEntity(entity); // remove entity from engine
				
				level.getAndRemoveTail(); // removes body from world, and platform from level queue
			}
		}
		
		// check if new platform needs to be spawned
		while (level.getPlatformQueue().size < 10) {
			PooledEngine engine = (PooledEngine)this.getEngine(); // ref engine
			
			Entity entity = engine.createEntity(); // make entity
			
			BodyComponent bodyC = engine.createComponent(BodyComponent.class); // make components
			TextureComponent texC = engine.createComponent(TextureComponent.class);
			TransformComponent transC = engine.createComponent(TransformComponent.class);
			
			bodyC.b2dBody = level.spawnNext((PooledEngine) this.getEngine()); // populate components
			bodyC.b2dBody.setUserData(entity);
			
			texC.region = LevelSystem.PLATFORM_TEXTURE;
			
			entity.add(bodyC);
			entity.add(texC);
			entity.add(transC);
		}
		
	}

	public float getYHeight() {
		return yHeight;
	}
	
	// test 1
	private float calculateYHeight() {
		return 20.0f * (float)Math.pow(GdxAI.getTimepiece().getTime(), 1.14f);
	}
	
}
