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
	
	public static final float MIN_WIDTH = 3.50f / SCALE;
	public static final float MAX_WIDTH = 4.75f / SCALE;
	
	public static final float MIN_HEIGHT = 0.40f / SCALE;
	public static final float MAX_HEIGHT = 0.41f / SCALE;
	
	public static final float MIN_Y_INC = 7.0f / SCALE;
	public static final float MAX_Y_INC = 7.1f / SCALE;
	
	private Queue<Platform> queue;
	private Random random;
	private World world;
	
	public Level(World world) {
		this.queue = new Queue<Platform>();
		this.random = new Random();
		this.world = world;
	}
	
	
//	public Body spawnNext(PooledEngine engine) {
//		Platform newPlatform = new Platform();
//		
//		float xPos = randomFloatInRange(2.5f, GameScreen.VIRTUAL_WIDTH-2.5f); // needs to check for width?
//		float width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
//		float height = randomFloatInRange(Level.MIN_HEIGHT, Level.MAX_HEIGHT);
//		float yPos = 2.0f; // assign based on if we have lastPlatform, otherwise 6.0 for 1st
//		
//		if (queue.size > 0) {
//			int i=0;
//			while (queue.first().inRange(xPos, width)) {
//				xPos = randomFloatInRange(2.5f, GameScreen.VIRTUAL_WIDTH-2.5f); // needs to check for width?
//				width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
//				System.out.println("forced to iter: " + (i+1) + " times on new platform.");
//			}
////			
//			yPos = queue.first().y + randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC);
//		}
//		
//		newPlatform.set(xPos, yPos, width, height);
//		
//		Body body = Box2dPlatformBuilder.DEFAULT(newPlatform).buildAndDispose(world); // add body to world and retrieve it
//		newPlatform.setBody(body);
//		
//		queue.addFirst(newPlatform);
//		
//		return body;
//	}
	
	public Body[] spawnNext2(PooledEngine engine) {
		
		int spacers = random.nextInt(100); // 0 to 99
		
		float height = 0.5f;
		
//		if (spacers < 75) { // 75% chance to have floor with one platform
//			
////			if (random.nextInt(100) < 80) { // 20% chance one of two floors isn't there
////				
////			}
//			
//			Platform[] newPlatform = new Platform[1];
//			for (int i=0; i < newPlatform.length; i++) newPlatform[i] = new Platform();
//			
//			float xPos, width;
//			float yPos = 2.0f;
//			
//			if (random.nextBoolean()) {
//				xPos = randomFloatInRange(0, GameScreen.VIRTUAL_WIDTH-MAX_WIDTH);
//				width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
//			} else {
//				xPos = randomFloatInRange(MAX_WIDTH, GameScreen.VIRTUAL_WIDTH);
//				width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
//				xPos -= width;
//			}
//			
//			if (queue.size > 0) {
//				yPos = queue.first().y + randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC);
//			}
//			
//			newPlatform[0].set(xPos, yPos, width, height);
//			
//			Body body = Box2dPlatformBuilder.DEFAULT(newPlatform[0]).buildAndDispose(world); // add body to world and retrieve it
//			newPlatform[0].setBody(body);
//			
//			queue.addFirst(newPlatform[0]);
//			
//			return new Body[] { newPlatform[0].getBody() };
//			
//		} else
			if (spacers < 100) { // 25% chance to have 2 platform
			
			float yPos = 2.0f;
			Platform[] newPlatform = new Platform[2];
			for (int i=0; i < newPlatform.length; i++) newPlatform[i] = new Platform();
			
			{
				float xPos, width;
				
//				if (random.nextBoolean()) {
					xPos = randomFloatInRange(5, (GameScreen.VIRTUAL_WIDTH/2.5f));
					width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
//				} else {
//					xPos = randomFloatInRange(MAX_WIDTH, GameScreen.VIRTUAL_WIDTH/2.0f);
//					width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
//					xPos -= width;
//				}
				
				if (queue.size > 0) {
					yPos = queue.first().y + randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC);
				}
				
				newPlatform[0].set(xPos, yPos, width, height);
				
				Body body = Box2dPlatformBuilder.DEFAULT(newPlatform[0]).buildAndDispose(world); // add body to world and retrieve it
				newPlatform[0].setBody(body);
				
				queue.addFirst(newPlatform[0]);
			}
			
			float spacer = randomFloatInRange(3.8f, 7.6f);
			
			{
				float xPos, width;
				
//				if (random.nextBoolean()) {
					xPos = randomFloatInRange((GameScreen.VIRTUAL_WIDTH/2.0f)+spacer, GameScreen.VIRTUAL_WIDTH);
					width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
//				} else {
//					xPos = randomFloatInRange((GameScreen.VIRTUAL_WIDTH/2.0f)-MAX_WIDTH+spacer, GameScreen.VIRTUAL_WIDTH);
//					width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
//					xPos -= width;
//				}
				
				// use same yPos as last...
				
				newPlatform[1].set(xPos, yPos, width, height);
				
				Body body = Box2dPlatformBuilder.DEFAULT(newPlatform[1]).buildAndDispose(world); // add body to world and retrieve it
				newPlatform[1].setBody(body);
				
				queue.addFirst(newPlatform[1]);
			}
			
			return new Body[] { newPlatform[0].getBody(), newPlatform[1].getBody() };
			
		} else { // 15% chance to have 3 spacers
			// can't happen. want to do 3 spacer floor
			return null;
		}
		
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
