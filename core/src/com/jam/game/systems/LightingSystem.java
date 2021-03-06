package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.jam.game.components.PlayerComponent;
import com.jam.game.screens.GameScreen;
import com.jam.game.utils.Mappers;
import com.jam.game.utils.enums.Category;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class LightingSystem extends EntitySystem implements Disposable {
	
	PointLight light;
	Entity player;
	
	public static RayHandler lightRayHandler;
	OrthographicCamera camera;
	
	float dist = 40.0f;
	
	
	public LightingSystem(Entity player, World world, OrthographicCamera camera) {
		super(Priority.POST_RENDER.PRIORITY);
		this.camera = camera;
		this.player =  player;
		lightRayHandler = new RayHandler(world);
		
		light = new PointLight(lightRayHandler, 500 /* num rays */, new Color(212.0f, 235.0f, 255.0f, 0.7f) /* color */,//new Color(1.0f, 1.0f, 1.0f, 0.7f)
				Mappers.playerMap.get(this.player).getDist() /* distance */, 0 /* x */, 0 /* y */);

		light.attachToBody(Mappers.bodyMap.get(player).b2dBody); // attach to player
		light.setXray(true);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		lightRayHandler.setCombinedMatrix(camera);
		light.setDistance(Mappers.playerMap.get(this.player).getDist());
		lightRayHandler.updateAndRender();
	}

	@Override
	public void dispose() {
		lightRayHandler.dispose();
	}
	
	public RayHandler getRayHandler() {
		return this.lightRayHandler;
	}
}
