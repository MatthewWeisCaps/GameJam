package com.jam.game.systems;

enum Priority {
	INPUT(0), PHYSICS(1), POST_PHYSICS(2), PRE_RENDER(3) /* lighting */, RENDER(4), POST_RENDER(5);
	
	final int PRIORITY;
	
	Priority(int priority) {
		this.PRIORITY = priority;
	}
}
