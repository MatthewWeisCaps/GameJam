package com.jam.game.levels;

import java.util.Random;
import java.util.stream.DoubleStream;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.utils.CircularBuffer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.jam.game.b2d.Box2dPlatformBuilder;
import com.jam.game.components.TextureComponent;
import com.jam.game.screens.GameScreen;

public class Level {
	
	public static final float MIN_WIDTH = 5.0f;
	public static final float MAX_WIDTH = 15.0f;
	
	public static final float MIN_HEIGHT_INC = 5.0f;
	public static final float MAX_HEIGHT_INC = 8.0f;

	
	private CircularBuffer<Platform> buf;
	private Platform lastPlatform;
	private Random random;
	
	public Level() {
		buf = new CircularBuffer<Platform>(10, false);
		random = new Random();
	}
	
	
	public void spawnNext(PooledEngine engine, World world) {
		Platform newPlatform = null;
		if (buf.isFull()) {
			newPlatform = buf.read();
		} else {
			newPlatform = new Platform();
			buf.store(newPlatform);
		}
		
		float xPos = randomFloatInRange(10.0f, GameScreen.VIRTUAL_WIDTH-10.0f); // needs to check for width?
		float width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
		float yPos = 6.0f; // assign based on if we have lastPlatform, otherwise 6.0 for 1st
		
		if (lastPlatform != null) {
			int i=0;
			while (lastPlatform.inRange(xPos, width)) {
				xPos = randomFloatInRange(10.0f, GameScreen.VIRTUAL_WIDTH-10.0f); // needs to check for width?
				width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
				System.out.println("forced to iter: " + (i+1) + " times on new platform.");
			}
			
			yPos = randomFloatInRange(Level.MIN_HEIGHT_INC, Level.MAX_HEIGHT_INC);
		}
		
		newPlatform.set(xPos, yPos, width);
		
		
		Body body = Box2dPlatformBuilder.DEFAULT(newPlatform).buildAndDispose(world); // add body to world and retrieve it
//		Entity platform = new Entity();
//		TextureComponent texC = engine.createComponent(TextureComponent.class);
//		texC.region.se
	}


	public Platform getLastPlatform() {
		return lastPlatform;
	}
	
	private float randomFloatInRange(float start, float end) {
		return (random.nextFloat()* (end - start))+start;
	}
	
}
