package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.TransformComponent;

import Utils.Mappers;

public class PhysicsSystem extends IteratingSystem{
	private static final float MAX_STEP_TIME = 1/60f;
    private static float accumulator = 0f;
    
    private World world;
    private Array<Entity> bodiesQueue;
    
    @SuppressWarnings("unchecked")
   	public PhysicsSystem(World world) {
    	super(Family.all(BodyComponent.class, TransformComponent.class).get());
    	this.world = world;
    	this.bodiesQueue = new Array<Entity>();
    }
    
    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	float frameTime = Math.min(deltaTime, 0.25f);
    	accumulator += frameTime;
    	
    	if(accumulator >= MAX_STEP_TIME) {
    		world.step(MAX_STEP_TIME, 6, 2);
    		accumulator -= MAX_STEP_TIME;
    		
    		//entity queue
    		for(Entity entity : bodiesQueue) {
    			TransformComponent tfm = Mappers.transformMap.get(entity);
    			BodyComponent bodyComp = Mappers.bodyMap.get(entity);
    			Vector2 pos = bodyComp.b2dBody.getPosition();
    			tfm.pos.x = pos.x;
    			tfm.pos.y = pos.y;
    		}
    	}
    	bodiesQueue.clear();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    	bodiesQueue.add(entity);
    }
}
