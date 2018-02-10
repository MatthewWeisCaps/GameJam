package com.jam.game.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.jam.game.components.TransformComponent;

public class ZComparator implements Comparator<Entity>{
	private ComponentMapper<TransformComponent> cmTrans;

	public ZComparator() {
		cmTrans = ComponentMapper.getFor(TransformComponent.class);
	}
	
	@Override
	public int compare(Entity entityA, Entity entityB) {
		float az = cmTrans.get(entityA).pos.z;
		float bz = cmTrans.get(entityB).pos.z;
		int res = 0;
		if(az > bz){
			res = 1;
			}else if(az < bz){
				res = -1;
			}
		return res;
    }
}
