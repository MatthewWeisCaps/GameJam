package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PlayerComponent implements Component, Poolable {

	private final float MIN_DIST = 0;
	private final float MAX_DIST = 100;
	
	public boolean lightPowerupEnabled = false;
	public float max_powerupTime = 100.0f;
	public float powerupTime = 0;
	private float powerupBonusValue = 0.15f;
	public final float reduceTime = 0.25f;
	
	public float distSubValue = -0.05f;
	public float distAddValue = 0.30f; //was .40
	public static float dist = 40.0f;
	
	public void changeDist(float amount) {
		if(dist < MIN_DIST) dist = MIN_DIST;
		else if(dist > MAX_DIST) dist = MAX_DIST;
		else dist += amount;
	}
	
	public void enableLightPowerup(){
		powerupTime = max_powerupTime;
		lightPowerupEnabled = true;
		dist += 5;
		System.out.println("POWER UP ON");
	}
	
	public void tickLightPowerUp(){
		if(powerupTime <= 0){
			powerupTime = 0;
			lightPowerupEnabled = false;
			
			System.out.println("POWER UP OFF");
			return;
		}
		
		changeDist(powerupBonusValue);
	}
	
	
	@Override
	public void reset() {
		
	}
	
}
