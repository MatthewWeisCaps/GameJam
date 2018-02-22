package com.jam.game.powerup;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jam.game.systems.LightingSystem;
import com.jam.game.utils.enums.PowerupType;

import box2dLight.PointLight;
import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class Powerup implements Component, Poolable {
	public float x, y, width, height;
	public PowerupType type;
	public PointLight light;
	
	private Body body; // body associated w/ this platform
	
	public Powerup() {
		this(0.0f, 0.0f);
	}
	
	public Powerup(float x, float y) {
		set(x, y, 0.0f, 0.0f); 
	}
	
	public void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = PowerupType.LIGHT; //For now default to light
	}

	public void setType(PowerupType type){
		this.type = type;
	}
	
	public PowerupType getType(){
		return this.type;
	}
	
	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
	
	public void setLightSystem(){
		this.light = new PointLight(LightingSystem.lightRayHandler, 40 /* num rays */, new Color(0.89f, 0.35f, 0.13f, 0.75f) /* color */,
				5.0f /* distance */, 0 /* x */, 0 /* y */);
		
		this.light.attachToBody(this.getBody()); // attach to player
		this.light.setXray(true);
		this.light.setSoftnessLength(0.0f);
	}
	
	public void removeLightSystem(){
		this.light.remove();
	}
	
	public static AnimatedBox2DSprite getAnimation(PowerupType type){
		if(type == PowerupType.HELMET){
			return getHelmetAnimation();
		}else if(type == PowerupType.LIGHT){
			return getLightAnimation();
		}
		return null;
	}
	
	public static AnimatedBox2DSprite getHelmetAnimation(){
		float aniSpeed = 0.2f;
		Array<TextureRegion> regions = new Array<TextureRegion>(20);
		regions.setSize(20);
		
		for(int i=0; i<20; i++){
			regions.set(i, new TextureRegion(new Texture("helmet.png"), i*32, 0, 32, 32));
		}
		AnimatedBox2DSprite spin = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
			
		spin.setUseOrigin(false);
		spin.setScale(2.5f);
		spin.setPosition(0, 2f);
	
		return spin;
	}
	
	public static AnimatedBox2DSprite getLightAnimation(){
		float aniSpeed = 0.2f;
		Array<TextureRegion> regions = new Array<TextureRegion>(16);
		regions.setSize(16);
		
		for(int i=0; i<16; i++){
			regions.set(i, new TextureRegion(new Texture("light.png"), i*16, 0, 16, 16));
		}
		AnimatedBox2DSprite spin = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
			
		/*spin.setUseOrigin(false);
		spin.setScale(2.5f);
		spin.setPosition(0, 2f);*/
	
		spin.setUseOrigin(false);
		spin.setScale(1.5f);
		spin.setPosition(0, 1f);
		
		return spin;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
