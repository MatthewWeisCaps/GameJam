package com.jam.game.components;

import com.badlogic.ashley.core.Component;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class AnimationComponent implements Component {
	
	public int animLayer;
	float stateTime;
	
//	AnimatedSprite animSprite = new AnimatedSprite(null));
//	AnimatedBox2DSprite sprite = new AnimatedBox2DSprite(animSprite);
	
	public AnimationComponent() {
//		sprite.draw(batch, body);
	}
	
	public AnimatedBox2DSprite sprite;
	
	
}
