package com.jam.game.utils.enums;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.jam.game.screens.GameScreen;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public enum PlatformType {
	OIL, MOVE, DAMAGE, NUB,WALL,DEFAULT;
		
	//It might be dirty...but it feels so good! (This is probs temp?)
	public AnimatedBox2DSprite getNubAnimation(){
		float aniSpeed = 0.075f;
		Array<TextureRegion> regions = new Array<TextureRegion>(3);
		regions.setSize(3);
		
		for(int i=1; i<=3; i++){
			regions.set(i-1, new TextureRegion(GameScreen.fileManager.getTextureFile("platforms"), i*32, 3*32, 32, 32));
		}
		AnimatedBox2DSprite flap = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
			
		flap.setUseOrigin(false);
		flap.setScale(2.5f);
		flap.setPosition(0, 0);
	
		return flap;
	}
}
