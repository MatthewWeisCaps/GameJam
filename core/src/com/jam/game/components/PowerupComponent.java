package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jam.game.powerup.Powerup;

public class PowerupComponent implements Component, Poolable{

	public Powerup powerup;
	
	@Override
	public void reset() {
		powerup = null;
	}
}
