package com.jam.game.utils;

public class Tuple {
	public String key;
	public String value;
	
	public Tuple(String key, String value){
		this.key = key;
		this.value = value;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getValue(){
		return this.value;
	}
}
