package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TransformComponent implements Component, Poolable {
	public Vector3 pos = new Vector3();
	public Vector2 scale = new Vector2(1.0f, 1.0f);
	
	@Override
	public void reset() {
		pos = new Vector3();
		scale = new Vector2(1.0f, 1.0f);
	}
}
