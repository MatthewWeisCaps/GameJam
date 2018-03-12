package com.jam.game.utils;

/*
 * Tuple for any two types.
 */
public class GenericTuple<T1, T2> {
	
	public T1 one;
	public T2 two;
	
	public GenericTuple(T1 one, T2 two) {
		this.one = one;
		this.two = two;
	}
	
}
