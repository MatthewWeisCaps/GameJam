package com.jam.game.levels;

import java.util.Random;
import java.util.stream.DoubleStream;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.utils.CircularBuffer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import com.jam.game.b2d.Box2dPlatformBuilder;
import com.jam.game.components.TextureComponent;
import com.jam.game.screens.GameScreen;

public class Level {
	
	private static final float SCALE = 1.0f;
	
	public static final float MIN_WIDTH = 3.0f / (SCALE*2);
	public static final float MAX_WIDTH = 6.0f / (SCALE*2);
	
	public static final float MIN_HEIGHT = 0.45f / SCALE;
	public static final float MAX_HEIGHT = 1.25f / SCALE;
	
	public static final float MIN_Y_INC = 5.50f / SCALE;
	public static final float MAX_Y_INC = 9.45f / SCALE;
	
	private Queue<Platform> queue;
	private Random random;
	private World world;
	
	public Level(World world) {
		this.queue = new Queue<Platform>(10);
		this.random = new Random();
		this.world = world;
	}
	
	
	public Body spawnNext(PooledEngine engine) {
		Platform newPlatform = new Platform();
		
		float xPos = randomFloatInRange(2.5f, GameScreen.VIRTUAL_WIDTH-2.5f); // needs to check for width?
		float width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
		float height = randomFloatInRange(Level.MIN_HEIGHT, Level.MAX_HEIGHT);
		float yPos = 2.0f; // assign based on if we have lastPlatform, otherwise 6.0 for 1st
		
		if (queue.size > 0) {
			int i=0;
			while (queue.first().inRange(xPos, width)) {
				xPos = randomFloatInRange(2.5f, GameScreen.VIRTUAL_WIDTH-2.5f); // needs to check for width?
				width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
				System.out.println("forced to iter: " + (i+1) + " times on new platform.");
			}
//			
			yPos = queue.first().y + randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC);
		}
		
		newPlatform.set(xPos, yPos, width, height);
		
		Body body = Box2dPlatformBuilder.DEFAULT(newPlatform).buildAndDispose(world); // add body to world and retrieve it
		newPlatform.setBody(body);
		
		queue.addFirst(newPlatform);
		
		return body;
	}

	public Platform getHead() {
		
		if (queue.size == 0) {
			return null;
		}
		
		return queue.first();
	}
	
	public Platform getTail() {
		
		if (queue.size == 0) {
			return null;
		}
		
		return queue.last();
	}
	
	public Platform getAndRemoveHead() {
		
		if (queue.size == 0) {
			return null;
		}
		
		world.destroyBody(queue.first().getBody());
		return queue.removeFirst();
	}
	
	public Platform getAndRemoveTail() {
		
		if (queue.size == 0) {
			return null;
		}
		
		world.destroyBody(queue.last().getBody());
		return queue.removeLast();
	}

	
	private float randomFloatInRange(float start, float end) {
		return (random.nextFloat()* (end - start))+start;
	}
	
	public Queue<Platform> getPlatformQueue() {
		return queue;
	}
	
}
