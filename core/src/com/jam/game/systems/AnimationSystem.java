package com.jam.game.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;

public class AnimationSystem extends SortedIteratingSystem {

	SpriteBatch batch;
	
	public AnimationSystem(SpriteBatch batch) {
		super(Family.all(AnimationComponent.class).get(), new Comparator<Entity>() {
			@Override
			public int compare(Entity e1, Entity e2) {
				int comp = ComponentMapper.getFor(AnimationComponent.class).get(e1).animLayer - ComponentMapper.getFor(AnimationComponent.class).get(e2).animLayer;
				if (comp < 0) return -1;
				else if (comp > 0) return 1;
				else return 0;
			}
		});
		this.batch = batch;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (ComponentMapper.getFor(BodyComponent.class).has(entity)) {
			Body body = ComponentMapper.getFor(BodyComponent.class).get(entity).b2dBody;
			ComponentMapper.getFor(AnimationComponent.class).get(entity).sprite.draw(batch, body);
		} else {
			ComponentMapper.getFor(AnimationComponent.class).get(entity).sprite.draw(batch);
		}
	}
}
