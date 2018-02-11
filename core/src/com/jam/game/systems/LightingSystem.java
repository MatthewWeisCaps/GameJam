package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import utils.Mappers;

public class LightingSystem extends EntitySystem implements Disposable {
	
	RayHandler lightRayHandler;
	OrthographicCamera camera;
	
	public LightingSystem(Entity player, World world, OrthographicCamera camera) {
		super(Priority.POST_RENDER.PRIORITY);
		this.camera = camera;
		this.lightRayHandler = new RayHandler(world);
//		lightRayHandler.setBlur(true);
//		lightRayHandler.setBlurNum(3);
//		lightRayHandler.setAmbientLight(0.2f, 0.0f, 0.2f, 0.2f);
		
		PointLight light = new PointLight(lightRayHandler, 40 /* num rays */, new Color(1.0f, 1.0f, 1.0f, 0.8f) /* color */,
				40.0f /* distance */, 0 /* x */, 0 /* y */);
		
		light.attachToBody(Mappers.bodyMap.get(player).b2dBody); // attach to player
//		light.setIgnoreAttachedBody(true); // ignore player
//		light.setSoft(true);
		light.setXray(true);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		lightRayHandler.setCombinedMatrix(camera);
		lightRayHandler.updateAndRender();
	}

	@Override
	public void dispose() {
		lightRayHandler.dispose();
	}
}
