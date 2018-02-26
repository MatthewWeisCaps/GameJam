package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.utils.Mappers;


public class PlayerSystem extends EntitySystem{
	
	private Entity player;
	private PlayerComponent pc;
	
	public PlayerSystem(Entity player){
		this.player = player;
		this.pc = Mappers.playerMap.get(this.player);
	}
	
	@Override
    public void update(float deltaTime) {
    	super.update(deltaTime);

    	StateComponent st = Mappers.stateMap.get(this.player);
    	
    	int state = st.get();
    	
    	if(state == StateComponent.STATE_MOVING || state == StateComponent.STATE_INAIR){
    		this.changeDist(this.pc.distSubValue);
    	}else if(state == StateComponent.STATE_NORMAL){
    		this.changeDist(this.pc.distAddValue);
    	}
    	
//    	this.changeDist(this.pc.distSubValue);
    	
    	if(this.pc.lightPowerupEnabled){
    		this.pc.powerupTime -= pc.reduceTime;
    		this.tickLightPowerUp();
    	}
    	
    }
	
	public void changeDist(float amount) {
		float dist = this.pc.getDist();
		if(dist < this.pc.getMinDist()) this.pc.setDistToMin();
		else if(dist > this.pc.getMaxDist()) this.pc.setDistToMax();
		else this.pc.setDist(dist + amount);
	}
	
	public void tickLightPowerUp(){
		if(this.pc.powerupTime <= 0){
			this.pc.powerupTime = 0;
			this.pc.lightPowerupEnabled = false;
			
			return;
		}
		
		this.changeDist(this.pc.powerupBonusValue);
	}

}
