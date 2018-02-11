package com.jam.game.systems;

enum Priority {
	INPUT(0), PHYSICS(1), PRE_RENDER(2) /* lighting */, RENDER(3), POST_RENDER(4);
	
	final int PRIORITY;
	
	Priority(int priority) {
		this.PRIORITY = priority;
	}
}
