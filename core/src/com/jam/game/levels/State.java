package com.jam.game.levels;

import com.jam.game.utils.Rando;

public class State {
	private String name;
	
	private int[][] placements = new int[3][3];
	
	private String[] nextState;
	
	public State(String name){
		this.name = name;
	}
	
	public void setPlacements(int[][] placements){
		this.placements = placements;
	}
	
	public int[][] getPlacements(){
		return this.placements;
	}
	
	public void setNextState(String[] state){
		this.nextState = state;
	}
	
	public String getNextState(){
		if(this.nextState.length == 1) return this.nextState[0];
		
		if(Rando.getRandomNumber() > 0.5f)
			return this.nextState[0];
		
		return this.nextState[1];
	}
	
	public String getStateName(){
		return this.name;
	}
}
