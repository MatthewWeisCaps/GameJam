package com.jam.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jam.game.powerup.Powerup;
import com.jam.game.systems.LevelSystem;
import com.jam.game.utils.enums.PowerupType;

public class PowerupComponent implements Component, Poolable{

	public Powerup powerup;
	
	public void handleCollisions(PlayerComponent player){
		switch(this.powerup.getType()){
			case LIGHT:
				player.enableLightPowerup();
				break;
			case HELMET:
				LevelSystem.score += 10;
				break;
			default:
				System.out.println("UN-HANDLED POWERUP TYPE");
				break;
		}
		
	}
	
	@Override
	public void reset() {
		powerup = null;
	}
}
