package com.jam.game.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;

public class EntityManager {
	public static List<Entity> entitiesToRemove = new ArrayList<Entity>();
	
	public static void remove(Entity entity){
		if(!entitiesToRemove.contains(entity)){
			entitiesToRemove.add(entity);
		}
	}
}
