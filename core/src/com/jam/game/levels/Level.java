package com.jam.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import com.jam.game.b2d.Box2dPlatformBuilder;
import com.jam.game.powerup.Powerup;
import com.jam.game.screens.GameScreen;
import com.jam.game.utils.Rando;
import com.jam.game.utils.enums.PlatformType;
import com.jam.game.utils.enums.PowerupType;

public class Level {
	
	private static final float SCALE = 1.0f;
	
	public static int NUB_COUNT = 0;
	
	public static final float MIN_WIDTH = 3.50f / SCALE;
	public static final float MAX_WIDTH = 4.75f / SCALE;
	
	public static final float MIN_HEIGHT = 0.40f / SCALE;
	public static final float MAX_HEIGHT = 0.41f / SCALE;
	
	public static final float MIN_Y_INC = 7.0f / SCALE;
	public static final float MAX_Y_INC = 7.1f / SCALE;
	
//	public static final float MIN_Y_INC = 7.0f / SCALE;
//	public static final float MAX_Y_INC = 10.1f / SCALE;
	
	public static final float MIN_X = 5.0f;
	
	public Entity[] walls;
	
	public float wallHeight = 10.0f;
	private float wallWidth = 2.0f;
	
	private float maxPlatDistToSpawnNub = 25.0f;
	private int maxNubs = 5;
	private float nubSize = 0.5f;
	
	private float slickPlatformChance = 0.75f; // 25% chance
	
	private float chanceToSpawnDoublePlatform = 0.75f;
	
	private final float SCREEN_SEG = GameScreen.VIRTUAL_WIDTH/3;
	
	private StateMachine sm;
	private Queue<Platform> queue;
	private World world;
	
	public Level(World world) {
		this.queue = new Queue<Platform>();
		this.world = world;
		this.sm = new StateMachine();
	
		this.walls = new Entity[2];
	}
	
	public Platform[] spawnNext2(PooledEngine engine) {
		
		int spacers = Rando.getRandomBetweenInt(100); // 0 to 99
		
		float height = 0.5f;
		
			if (spacers < 100) { // 25% chance to have 2 platform
			
			float yPos = 2.0f;
			Platform[] newPlatform = new Platform[2];
			
			int nubCount = 0;
			
			for (int i=0; i < newPlatform.length; i++) newPlatform[i] = new Platform();
			
			{
				float xPos, width;
				
				xPos = randomFloatInRange(MIN_X, (GameScreen.VIRTUAL_WIDTH/2.5f));
				width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
				
				if (queue.size > 0) {
					yPos = queue.first().y + randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC);
				}
				
				if(Rando.getRandomNumber() <= this.slickPlatformChance){
					newPlatform[0].set(xPos, yPos, width, height, PlatformType.OIL);
				}else{
					newPlatform[0].set(xPos, yPos, width, height); //Default Platform
				}
				
				newPlatform[0].setSegment(this.getScreenSeg(xPos));
				
				Body body = Box2dPlatformBuilder.DEFAULT(newPlatform[0]).buildAndDispose(world); // add body to world and retrieve it
				
				newPlatform[0].setBody(body);
				
				queue.addFirst(newPlatform[0]);
			}
			
			float spacer = randomFloatInRange(6.0f, 7.6f); //3.8 - 7.6
			
			{
				float xPos, width;
				
				xPos = randomFloatInRange((GameScreen.VIRTUAL_WIDTH/2.0f)+spacer, GameScreen.VIRTUAL_WIDTH);
				width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
				
				//Spawning into wall...thats no good
				if(xPos + width > GameScreen.VIRTUAL_WIDTH){
					xPos = GameScreen.VIRTUAL_WIDTH - width - MIN_X; //Make it so that it is always the mix dist away from the wall
				}
			
				// use same yPos as last...
				
				newPlatform[1].set(xPos, yPos, width, height);
				
				Body body = Box2dPlatformBuilder.DEFAULT(newPlatform[1]).buildAndDispose(world); // add body to world and retrieve it
				newPlatform[1].setBody(body);
				
				queue.addFirst(newPlatform[1]);
			}
			
			float distBetween = newPlatform[1].x - (newPlatform[0].x);
			
			Platform[] potPlatforms = new Platform[3];
			if(distBetween > this.maxPlatDistToSpawnNub && NUB_COUNT < this.maxNubs){
				System.out.println("Spawning nub: " + nubCount);
				potPlatforms[0] = newPlatform[0];
				potPlatforms[1] = newPlatform[1];
				
				potPlatforms[2] = new Platform();
				potPlatforms[2].set(distBetween, potPlatforms[0].y + MAX_Y_INC/2, nubSize, nubSize, PlatformType.NUB);
				
				Body body = Box2dPlatformBuilder.DEFAULT(potPlatforms[2]).build(world);
				potPlatforms[2].setBody(body);
				NUB_COUNT++;
				
				return potPlatforms;
			}
			return newPlatform;
			//return new Body[] { newPlatform[0].getBody(), newPlatform[1].getBody() };
			
		} else { // 15% chance to have 3 spacers
			// can't happen. want to do 3 spacer floor
			return null;
		}
		
	}
	
	public Platform[] spawnNextWithStates(PooledEngine engine) {
		Platform[] platforms = new Platform[4];
		
		int[][] currentStateValues = this.sm.moveToNextStateAndReturn();
		
		int onPlat = 0;
		float yPos, xPos, width;
		float height = 0.5f;
		
		yPos = 10.0f;//randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC);
		
		if (queue.size > 0) {
			yPos = queue.first().y; //+ randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC);
		}
		
		System.out.println("ON STATE: " + this.sm.current_state);
		
		for(int i=currentStateValues.length-1; i>=0; i--){
			yPos += randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC) * ((currentStateValues.length-1) - i);
			for(int j=0; j<currentStateValues[i].length; j++){
				int val = currentStateValues[i][j];
				if(val == 0) continue;
				
				if(val == 2){
					platforms[onPlat] = new Platform();
					xPos = getNubPosBasedOnCurrentSeg(j);
					platforms[onPlat].set(xPos, yPos - MAX_Y_INC/3, nubSize, nubSize, PlatformType.NUB);
					
					Body nubbody = Box2dPlatformBuilder.DEFAULT(platforms[onPlat]).build(world);
					platforms[onPlat].setBody(nubbody);
				}else{
					platforms[onPlat] = new Platform();

					width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
					
					
					xPos = getXPosBasedOnCurrentSeg(j, width);
					
					if(Rando.getRandomNumber() <= this.slickPlatformChance){
						platforms[onPlat].set(xPos, yPos, width, height, PlatformType.OIL);
					}else{
						platforms[onPlat].set(xPos, yPos, width, height); //Default Platform
					}
					
					platforms[onPlat].setSegment(this.getScreenSeg(xPos));
					
					Body body = Box2dPlatformBuilder.DEFAULT(platforms[onPlat]).buildAndDispose(world); // add body to world and retrieve it
					
					platforms[onPlat].setBody(body);
					
					queue.addFirst(platforms[onPlat]);
				}
				onPlat++;
			}
		}

		return platforms;
	}
	
	//TODO: FIX CODE DUPE!
	public Platform[] spawnNext(PooledEngine engine) {
		Platform[] platforms = new Platform[3]; //Left, Right, Nub
				
		float height = 0.5f;

		float yPos = 10.0f;
		int lastSeg = -1;

		
		//Get the ypos of the highest platform
		if (queue.size > 0) {
			yPos = queue.first().y + randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC); //May need to change
			lastSeg = queue.first().getSegment() - 1;
		}
		
		
//		float doubleYpos = lastSeg >= 0 ? yPos + (randomFloatInRange(Level.MIN_Y_INC, Level.MAX_Y_INC) * 4.5f) : yPos;
		
		//First Platform:
		platforms[0] = new Platform();
		
		float xPos, width;
		
		width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
		xPos = getXPosBasedOnLastSeg(lastSeg, width);
		
		if(Rando.getRandomNumber() <= this.slickPlatformChance){
			platforms[0].set(xPos, yPos, width, height, PlatformType.OIL);
		}else{
			platforms[0].set(xPos, yPos, width, height); //Default Platform
		}
		
		platforms[0].setSegment(this.getScreenSeg(xPos));
		
		Body body = Box2dPlatformBuilder.DEFAULT(platforms[0]).buildAndDispose(world); // add body to world and retrieve it
		
		platforms[0].setBody(body);
		
		queue.addFirst(platforms[0]);
		
		//NUB
		boolean[] takenSeg = new boolean[3];
		for(int i=1; i < takenSeg.length+1; i++){
			takenSeg[i-1] = platforms[0].getSegment() == i || lastSeg == i;
		}
		
		if(takenSeg[0] || takenSeg[2]){
			System.out.println("MAKING NUB");
			platforms[2] = new Platform();
			xPos = randomFloatInRange(this.SCREEN_SEG + MIN_X, this.SCREEN_SEG*2 - MIN_X);
			platforms[2].set(xPos, platforms[0].y + MAX_Y_INC, nubSize, nubSize, PlatformType.NUB);
			
			Body nubbody = Box2dPlatformBuilder.DEFAULT(platforms[2]).build(world);
			platforms[2].setBody(nubbody);
		}
		
		//SECOND PLATFORM
		platforms[1] = new Platform();
		
		width = randomFloatInRange(Level.MIN_WIDTH, Level.MAX_WIDTH);
		xPos = getXPosBasedOnLastSeg(platforms[0].getSegment() - 1, width);
		
		while(takenSeg[this.getScreenSeg(xPos) - 1]){
			xPos = getXPosBasedOnLastSeg(platforms[0].getSegment() - 1, width);
		}
		
		if(Rando.getRandomNumber() <= this.slickPlatformChance){
			platforms[1].set(xPos, yPos, width, height, PlatformType.OIL);
		}else{
			platforms[1].set(xPos, yPos, width, height); //Default Platform
		}
		
		platforms[1].setSegment(this.getScreenSeg(xPos));
		
		body = Box2dPlatformBuilder.DEFAULT(platforms[1]).buildAndDispose(world); // add body to world and retrieve it
		
		platforms[1].setBody(body);
		
		queue.addFirst(platforms[1]);
		
		return platforms;
	}
	
	
	public Powerup spawnPowerUp(float platformX, float platformY, PooledEngine engine){
		Powerup p = new Powerup();
		
		float xPos = platformX + MIN_WIDTH/7;
		float yPos = platformY + MAX_HEIGHT*4;
		
		p.set(xPos, yPos, 1f, 1f);
		
		Body body = Box2dPlatformBuilder.DEFAULT(p).build(world);
		p.setBody(body);
		
		PowerupType puT = Rando.getRandomNumber() > 0.0f ? PowerupType.LIGHT : PowerupType.HELMET; 
		p.setType(puT);
		if(p.getType() == PowerupType.LIGHT) p.setLightSystem();
		
		return p;
	}
	
	public Body[] spawnLeftAndRightWalls(float y){
		Platform[] walls = new Platform[2];
		
		//Left Wall:
		walls[0] = new Platform();
		walls[0].set(0, y, this.wallWidth, this.wallHeight);		
		Body lBody = Box2dPlatformBuilder.DEFAULT(walls[0]).buildAndDispose(world); // add body to world and retrieve it
		walls[0].setBody(lBody);
		
		//Right Wall:
		walls[1] = new Platform();
		walls[1].set(GameScreen.VIRTUAL_WIDTH, y, this.wallWidth, this.wallHeight);		
		Body rBody = Box2dPlatformBuilder.DEFAULT(walls[1]).buildAndDispose(world); // add body to world and retrieve it
		walls[1].setBody(rBody);
				
		return new Body[] {walls[0].getBody(), walls[1].getBody()};
	}
	
	private float getXPosBasedOnLastSeg(int lastSeg, float width){
		if(lastSeg < 0){
			return randomFloatInRange(MIN_X, GameScreen.VIRTUAL_WIDTH - MIN_X - width);
		}else if(lastSeg == 0){ //Left => M || R
			return randomFloatInRange(this.SCREEN_SEG + MIN_X, GameScreen.VIRTUAL_WIDTH - MIN_X - width);
		}else if(lastSeg == 1){//Middle ==> L || R
			return Rando.getRandomNumber() > 0.5f ? 
					randomFloatInRange(MIN_X, this.SCREEN_SEG - MIN_X - width) : //Left
					randomFloatInRange(this.SCREEN_SEG * 2, GameScreen.VIRTUAL_WIDTH - MIN_X - width); //Right
		}else{//Right => L || M
			return randomFloatInRange(MIN_X, this.SCREEN_SEG*2 - MIN_X - width);
		}
	}
	
	private float getNubPosBasedOnCurrentSeg(int seg){
		if(seg == 0){
			return this.SCREEN_SEG;
		}else if(seg == 1){
			return randomFloatInRange(this.SCREEN_SEG, this.SCREEN_SEG*2);
		}else{
			return this.SCREEN_SEG*2;
		}
	}
	
	private float getXPosBasedOnCurrentSeg(int seg, float width){
		if(seg == 0){ //Left
			return randomFloatInRange(MIN_X, this.SCREEN_SEG - MIN_X - width);
		}else if(seg == 1){//Middle
			return randomFloatInRange(this.SCREEN_SEG, this.SCREEN_SEG*2 - MIN_X - width);
		}else{//Right
			return randomFloatInRange(this.SCREEN_SEG*2, GameScreen.VIRTUAL_WIDTH - MIN_X - width);
		}
	}
	
	private int getScreenSeg(float x){
		int seg = 0;
		
		for(int i=1; i<4; i++){
			float testSeg = this.SCREEN_SEG * i;
			
			if(x <= testSeg){
				seg = i;
				return seg;
			}
		}
		
		return seg;
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
		return (Rando.getRandomNumber() * (end - start))+start;
	}
	
	public Queue<Platform> getPlatformQueue() {
		return queue;
	}
	
}
