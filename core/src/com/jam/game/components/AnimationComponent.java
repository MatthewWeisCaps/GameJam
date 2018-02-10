package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class AnimationComponent implements Component {
	
	public ArrayMap<String, AnimatedBox2DSprite> animations = new ArrayMap<String, AnimatedBox2DSprite>();
	public float stateTime = 0.0f;
	public String currentAnimation;
	
}
