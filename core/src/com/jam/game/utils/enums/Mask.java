package com.jam.game.utils.enums;

public enum Mask {
	PLAYER((short) Category.ALL.getValue()),
	WALL((short) (Category.ALL.getValue() | ~(Category.POWERUP.getValue()))),
	POWERUP((short) Category.PLAYER.getValue()),
	ROPE((short) ~(Category.POWERUP.getValue()));
	
	private final short mask;
	
	Mask(short mask){
		this.mask = mask;
	}
	
	public short getValue() { return mask; }
}
