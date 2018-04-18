package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector3;
import com.jam.game.Game;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.screens.GameScreen;
import com.jam.game.utils.Mappers;


public class PlayerSystem extends EntitySystem{
	
	private Entity player;
	private PlayerComponent pc;
	
	public PlayerSystem(Entity player){
		super(Priority.POST_PHYSICS.PRIORITY);
		this.player = player;
		this.pc = Mappers.playerMap.get(this.player);
	}
	
	@Override
    public void update(float deltaTime) {
    	super.update(deltaTime);

    	this.checkDeath();
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
			this.pc.disableLightPowerup();
			return;
		}
		
		this.changeDist(this.pc.powerupBonusValue);
	}
	
	private void checkDeath(){
		RenderingSystem rs = super.getEngine().getSystem(RenderingSystem.class);
		Vector3 player3D = rs.getCamera().project(new Vector3(Mappers.bodyMap.get(player).b2dBody.getPosition(), 0.0f), rs.getViewport().getScreenX(), rs.getViewport().getScreenY(), rs.getViewport().getScreenWidth(), rs.getViewport().getScreenHeight());
		
		if(player3D.y < -40.0f) {
			StateComponent sc = Mappers.stateMap.get(player);
			sc.isSwinging = false;
			death();
		}
	}
	
	private void death(){
		((GameScreen)Game.getScreenHandler().getCurrentScreen()).gameOver(Mappers.bodyMap.get(this.player).b2dBody);
	}

}
