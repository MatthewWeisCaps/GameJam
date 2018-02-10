package com.jam.game.components;

import com.badlogic.ashley.core.Component;

public class TypeComponent implements Component{
	public static final int PLAYER = 0;
	public static final int WALL = 1;
	public static final int DANGER = 3;
	public static final int OTHER = 4;
	
	public int type = OTHER;
}
