package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.utils.Mappers;


public class PlayerSystem extends EntitySystem{
	
	private Entity player;
	
	public PlayerSystem(Entity player){
		this.player = player;
	}
	
	@Override
    public void update(float deltaTime) {
    	super.update(deltaTime);

    	PlayerComponent pc = Mappers.playerMap.get(this.player);
    	StateComponent st = Mappers.stateMap.get(this.player);
    	
    	int state = st.get();
    	
    	if(state == StateComponent.STATE_MOVING || state == StateComponent.STATE_INAIR){
    		pc.changeDist(pc.distSubValue);
    	}else if(state == StateComponent.STATE_NORMAL){
    		pc.changeDist(pc.distAddValue);
    	}
    	
    	if(pc.lightPowerupEnabled){
    		pc.powerupTime -= pc.reduceTime;
    		pc.tickLightPowerUp();
    	}
    	
    }

}
