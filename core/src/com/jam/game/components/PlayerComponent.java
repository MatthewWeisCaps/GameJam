package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jam.game.controllers.KeyboardController;

public class PlayerComponent implements Component, Poolable {

	public KeyboardController controller = new KeyboardController();
	
	private final float MIN_DIST = 10;
	private final float MAX_DIST = 75; //Was 100
	
	public boolean lightPowerupEnabled = false;
	public float max_powerupTime = 100.0f;
	public float powerupTime = 0;
	public float powerupBonusValue = 0.15f;
	public final float reduceTime = 0.25f;
	
	public float distSubValue = -0.1f; //was -0.05
	public float distAddValue = 0.30f; //was .40
	public float dist = 40.0f;
	
	public boolean onMovingPlatform = false;
	
	public void enableLightPowerup(){
		powerupTime = max_powerupTime;
		lightPowerupEnabled = true;
		dist += 5;
	}
	
	public float getDist(){
		return this.dist;
	}
	
	public void setDist(float dist){
		this.dist = dist;
	}
	
	public void setDistToMin(){
		this.dist = this.MIN_DIST;
	}
	
	public void setDistToMax(){
		this.dist = this.MAX_DIST;
	}
	
	public float getMinDist(){
		return this.MIN_DIST;
	}
	
	public float getMaxDist(){
		return this.MAX_DIST;
	}
	
	@Override
	public void reset() {
		
	}
	
}
