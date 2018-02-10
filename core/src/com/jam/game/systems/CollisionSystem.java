package com.jam.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jam.game.components.CollisionComponent;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.TypeComponent;

public class CollisionSystem extends IteratingSystem{

	ComponentMapper<CollisionComponent> cm;
	ComponentMapper<PlayerComponent> pm;
	
	@SuppressWarnings("unchecked")
	public CollisionSystem() {
		super(Family.all(CollisionComponent.class, PlayerComponent.class).get());
		
		cm = ComponentMapper.getFor(CollisionComponent.class);
		pm = ComponentMapper.getFor(PlayerComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CollisionComponent cc = cm.get(entity);
		
		Entity collidedEntity = cc.collisionEntity;
		if(collidedEntity != null) {
			TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
			if(type != null) {
				switch(type.type) {
				case TypeComponent.DANGER:
					System.out.println("PLAYER SHOULD DIE");
					break;
				case TypeComponent.WALL:
					System.out.println("HIT WALL");
					break;
				case TypeComponent.OTHER:
					System.out.println("HIT OTHER");
					break;
				}
				cc.collisionEntity = null;
			}
		}
	}
	
}
