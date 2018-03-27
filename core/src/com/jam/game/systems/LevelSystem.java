package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.PlatformComponent;
import com.jam.game.components.PowerupComponent;
import com.jam.game.components.TransformComponent;
import com.jam.game.levels.Level;
import com.jam.game.powerup.Powerup;
import com.jam.game.utils.Mappers;
import com.jam.game.utils.Rando;
import com.jam.game.utils.enums.PlatformType;

import box2dLight.RayHandler;
import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class LevelSystem extends EntitySystem {	
	RayHandler lightRayHandler;
	
	private float camSpeed = 1.60f;
	private float camSpeedMax = 2.75f;
	private float camSpeedIncrease = 0.00001f;
	private Level level;
	
	private Entity[] walls;
	
	private OrthographicCamera cam;
	private float yHeight = 0.0f;
	
	private final float ITEM_CHANCE = 0.50f; //must be larger than
	
	public LevelSystem(OrthographicCamera cam, Body playerBody, Level level) {
		super(Priority.POST_RENDER.PRIORITY);
		this.level = level;
		this.cam = cam;
		
		this.walls = new Entity[2];
	}
		
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		// calc new y
		yHeight = calculateYHeight();//Math.max(calculateYHeight(), playerBody.getPosition().y);
		
		if(this.walls[0] == null){
			this.walls = this.createInitialWalls(yHeight);
		}else{
			this.updateWalls(yHeight);
		}
				
		// move camera
		cam.position.set(cam.position.x, yHeight, cam.position.z);
		
		/*!cam.frustum.pointInFrustum(platform.getBody().getLocalCenter().x, platform.getBody().getLocalCenter().y, 0.0f) */
		// check lowest platform for removal, if so remove its body and entity from engine and world
		PlatformComponent platform = level.getTail();
		if (platform != null && platform.getBody() != null && platform.getBody().getUserData() != null) {
			
			FitViewport viewport = this.getEngine().getSystem(RenderingSystem.class).getViewport();
			Camera cam = viewport.getCamera();
			cam.update();
			Vector3 coords3D = cam.project(new Vector3(platform.getBody().getPosition(), 0.0f), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
				
			if (coords3D.y < -5.0f) {
									
				Entity entity = (Entity) platform.getBody().getUserData();
								
				this.getEngine().removeEntity(entity); // remove entity from engine
				
				level.getAndRemoveTail(); // removes body from world, and platform from level queue
				
			}
		}
		
		for(int i=0; i<level.getPlatformQueue().size; i++){
			PlatformComponent pc = level.getPlatformQueue().get(i);
			
			if(pc.getType() == PlatformType.MOVE){

				Body body = pc.getBody();
				
				body.setGravityScale(0);
				body.setLinearVelocity(new Vector2(0,0));
				body.setLinearVelocity(pc.getDir() * pc.SPEED, Math.abs(body.getLinearVelocity().y));
			}
		}
		
		// check if new platform needs to be spawned
		while (level.getPlatformQueue().size < 30) {
			PooledEngine engine = (PooledEngine)this.getEngine(); // ref engine
			
			
			for (PlatformComponent plat : level.spawnNextWithRow(engine)) {
				if(plat == null) continue;
				
				Entity entity = engine.createEntity(); // make entity
				
				BodyComponent bodyC = engine.createComponent(BodyComponent.class); // make components
				AnimationComponent animC = engine.createComponent(AnimationComponent.class);
				TransformComponent transC = engine.createComponent(TransformComponent.class);
				PlatformComponent platC = engine.createComponent(PlatformComponent.class);
				
				bodyC.b2dBody = plat.getBody();
				bodyC.b2dBody.setUserData(entity);
				
				final String def = "DEFAULT";
				
//				if(plat.getType() != PlatformType.NUB){
//					Array<TextureRegion> one = new Array<TextureRegion>();
//					one.setSize(1);
//					one.set(0, plat.getTextureRegion());
//					
//					one.add(plat.getTextureRegion());
//					
//					animC.animations.put(def, new AnimatedBox2DSprite(new AnimatedSprite(
//							new Animation<TextureRegion>(0.0f, one, PlayMode.NORMAL))));
//				}else{
//					animC.animations.put(def, plat.getPlatformAnimation());
//				}
				
				animC.animations.put(def, plat.getPlatformAnimation());
				animC.currentAnimation = def;
				
				platC = plat;
				
				entity.add(bodyC);
				entity.add(animC);
				entity.add(transC);
				entity.add(platC);
				
				engine.addEntity(entity);
								
				//Roll for powerups on the platform
				if(Rando.getRandomNumber() > this.ITEM_CHANCE && plat.getType() != PlatformType.NUB){
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
				}
				
			}
		} //end of spawning platforms		
		
	}
	
	private Entity[] createInitialWalls(float y){
		PooledEngine engine = (PooledEngine)this.getEngine(); // ref engine
		
		Entity[] entities = new Entity[2];
		entities[0] = engine.createEntity();
		entities[1] = engine.createEntity();
		
		BodyComponent bodyC1 = engine.createComponent(BodyComponent.class);
		BodyComponent bodyC2 = engine.createComponent(BodyComponent.class); 

		AnimationComponent animC = engine.createComponent(AnimationComponent.class);
		TransformComponent transC = engine.createComponent(TransformComponent.class);
		
		PlatformComponent platC1 = engine.createComponent(PlatformComponent.class);
		PlatformComponent platC2 = engine.createComponent(PlatformComponent.class);

		PlatformComponent[] walls = this.level.spawnLeftAndRightWalls(y);
		
		platC1 = walls[0];
		platC2 = walls[1];
		
		bodyC1.b2dBody = platC1.getBody();
		bodyC2.b2dBody = platC2.getBody();
		
		bodyC1.b2dBody.setUserData(entities[0]);
		bodyC2.b2dBody.setUserData(entities[1]);
				
		final String def = "DEFAULT";
		
		animC.animations.put(def, platC1.getPlatformAnimation());
		animC.currentAnimation = def;
		
		
		entities[0].add(bodyC1);
		entities[0].add(animC);
		entities[0].add(transC);
		entities[0].add(platC1);
		
		entities[1].add(bodyC2);
		entities[1].add(animC);
		entities[1].add(transC);
		entities[1].add(platC2);
		
		engine.addEntity(entities[0]);
		engine.addEntity(entities[1]);
		
		return entities;
	}
	
	private void updateWalls(float y){
		FitViewport viewport = this.getEngine().getSystem(RenderingSystem.class).getViewport();

		PlatformComponent lWallPC = Mappers.platformMap.get(this.walls[0]);
		PlatformComponent rWallPC = Mappers.platformMap.get(this.walls[1]);
		
		Body b1 = lWallPC.getBody();
		Body b2 = rWallPC.getBody();
		
		Vector3 coords3D = cam.project(new Vector3(b1.getPosition(), 0.0f), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
		
		b1.setGravityScale(0);
		b1.setLinearVelocity(new Vector2(0,0));
		
		b2.setGravityScale(0);
		b2.setLinearVelocity(new Vector2(0,0));
		
		if(coords3D.y - lWallPC.height/2 < y){
			b1.setTransform(0, y, 0);
			b2.setTransform(rWallPC.x, y, 0);
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
