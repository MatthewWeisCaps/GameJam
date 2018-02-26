package com.jam.game.levels;

import java.util.HashMap;

import com.jam.game.utils.enums.PlatformType;

public class StateMachine {
	
	public String current_state;
			
	private HashMap<PlatformType, Integer> typeConverter = new HashMap<PlatformType, Integer>();
	
	private HashMap<String, State> states = new HashMap<String, State>();
	
	public StateMachine(){
		this.typeConverter.put(PlatformType.DEFAULT, 1);
		this.typeConverter.put(PlatformType.NUB, 2);
		this.typeConverter.put(PlatformType.DAMAGE, 3);
		
		this.createStates();
	}
	
	public int[][] moveToNextStateAndReturn(){
		if(this.current_state == null){
			this.current_state = "A";
		}else{
			this.current_state = this.states.get(current_state).getNextState();
		}
		
		return this.states.get(current_state).getPlacements();
	}
	
	public void createStates(){
		int[][] placements = new int[3][3];
		State s;
		
		s = new State("A");
		placements = new int[][]{
				{0,0,0},
				{1,0,1},
				{0,1,0}};
		s.setPlacements(placements);
		s.setNextState(new String[] {"B", "C"});
		this.states.put(s.getStateName(), s);
		
		s = new State("B");
		placements = new int[][]{
				{1,0,1},
				{0,0,1},
				{0,0,0}};
		s.setPlacements(placements);
		s.setNextState(new String[] {"C"});	
		this.states.put(s.getStateName(), s);
		
		s = new State("C");
		placements = new int[][]{
				{1,0,0},
				{0,1,0},
				{1,0,0}};
		s.setPlacements(placements);
		s.setNextState(new String[] {"D"});	
		this.states.put(s.getStateName(), s);
		
		s = new State("D");
		placements = new int[][]{
				{1,0,1},
				{0,1,0},
				{0,0,0}};
		s.setPlacements(placements);
		s.setNextState(new String[] {"E", "F"});	
		this.states.put(s.getStateName(), s);
		
		s = new State("E");
		placements = new int[][]{
				{1,0,0},
				{0,1,0},
				{0,0,1}};
		s.setPlacements(placements);
		s.setNextState(new String[] {"A"});	
		this.states.put(s.getStateName(), s);
		
		s = new State("F");
		placements = new int[][]{
				{0,0,1},
				{0,1,0},
				{1,0,0}};
		s.setPlacements(placements);
		s.setNextState(new String[] {"B"});	
		this.states.put(s.getStateName(), s);
		
		
		
	}
	
}
