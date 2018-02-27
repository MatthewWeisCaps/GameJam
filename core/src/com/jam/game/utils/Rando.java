package com.jam.game.utils;

import java.util.Random;

public class Rando {
	public static Random r = new Random();
	
	public static float getRandomNumber(){
		return r.nextFloat();
	}
	
	public static int getRandomBetweenInt(int max){
		return r.nextInt(max);
	}
	
	public static boolean coinFlip(){
		return Rando.getRandomNumber() > 0.5f;
	}
}
