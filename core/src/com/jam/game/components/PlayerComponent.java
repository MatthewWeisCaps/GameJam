package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jam.game.controllers.KeyboardController;
import com.jam.game.utils.Mappers;
import com.jam.game.utils.enums.PlatformType;

public class PlayerComponent implements Component, Poolable {

	public KeyboardController controller = new KeyboardController();
	
	private final float MIN_DIST = 15; //Was 10
	private final float MAX_DIST = 55; //Was 90 //Was 100
	
	public boolean lightPowerupEnabled = false;
	public float max_powerupTime = 100.0f;
	public float powerupTime = 0;
	public float powerupBonusValue = 0.05f; //was 0.15
	public final float reduceTime = 0.25f;
	
	public float distSubValue = -0.075f; //was -0.05
	public float distAddValue = 0.30f; //was .40
	public float dist = 0.0f;
	
	public boolean onMovingPlatform = false;
	
	public PlatformType onPlatType = PlatformType.DEFAULT;
	
	public void enableLightPowerup(){
		powerupTime = max_powerupTime;
		lightPowerupEnabled = true;
		dist += 5;
	}
	
	public void disableLightPowerup(){
		powerupTime = 0.0f;
		lightPowerupEnabled = false;
	}
	
	public void setOnPlatType(PlatformType type){
		this.onPlatType = type;
	}
	
	public void doMovementBasedOnPlat(Entity entity, int dir, int speed){
		BodyComponent body = Mappers.bodyMap.get(entity);
				
		switch(this.onPlatType){
			case DEFAULT:
				body.b2dBody.setLinearVelocity(dir*speed, body.b2dBody.getLinearVelocity().y);
				break;
			case MOVE:
				body.b2dBody.setLinearVelocity(dir*speed, body.b2dBody.getLinearVelocity().y);
				break;
			case OIL:
				body.b2dBody.setLinearVelocity(MathUtils.lerp(body.b2dBody.getLinearVelocity().x, dir * speed, 0.2f), body.b2dBody.getLinearVelocity().y);
				break;
			default:
				body.b2dBody.setLinearVelocity(dir*speed, body.b2dBody.getLinearVelocity().y);
				break;
		}
	}
	
	public void resetPlayerX(Entity entity){
		BodyComponent body = Mappers.bodyMap.get(entity);
		
		if(this.onPlatType != PlatformType.OIL){
			body.b2dBody.setLinearVelocity(0.0f, body.b2dBody.getLinearVelocity().y);
		}
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
