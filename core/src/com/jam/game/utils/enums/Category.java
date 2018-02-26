package com.jam.game.utils.enums;

public enum Category {
	PLAYER((short) 0x0001),
	WALL((short) 0x0002),
	WALL_NUB((short) 0x0003),
	POWERUP((short) 0x0004),
	ALL((short) 0xFFFFF);
	
	private final short category;
	
	Category(short cat){
		this.category = cat;
	}
	
	public short getValue() { return category; }
	
}
