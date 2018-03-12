package com.jam.game.systems;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;

/*
 * Class which couples entity with a potential action.
 * Also overrides hashCode and equals, so that regardless of the action, no two entities can be queued together
 * (Note that the methods equals/hashCode are how a Set determines if a set already contains a given object)
 */
class EntityActionTuple {
	Entity entity;
	Consumer<Entity> consumer; // can be null
	
	EntityActionTuple(Entity entity, Consumer<Entity> consumer) {
		this.entity = entity;
		this.consumer = consumer;
	}
	
	EntityActionTuple(Entity entity) {
		this(entity, null);
	}
	
	@Override
	public int hashCode() {
		return entity.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityActionTuple) {
			return ((EntityActionTuple)obj).entity.equals(entity);
		}
		return false;
	}
}

public class DelayedRemovalSystem extends EntitySystem {
	
	private World world;
	
	// objectset is libgdx's Set, which is just a faster (but more annoying to use) version of Java's set.
	private ObjectSet<EntityActionTuple> pendingEntityRemovals = new ObjectSet<EntityActionTuple>();
	private ObjectSet<Body> pendingBodyRemovals = new ObjectSet<Body>();
	private ObjectSet<Joint> pendingJointRemovals = new ObjectSet<Joint>();
	
	public DelayedRemovalSystem(World world) {
		super(Priority.POST_PHYSICS.PRIORITY); // this should go off after the physics engine but before rendering
		this.world = world;
	}
	
	/*
	 * Might be better way than using .first() method, but this makes sure that this System is only processed on frames where something actually
	 * needs to be removed.
	 */
	@Override
	public boolean checkProcessing() {
		return (pendingEntityRemovals.first() == null && pendingBodyRemovals.first() == null && pendingJointRemovals.first() == null);
	}
	
	@Override
	public void update(float deltaTime) {
		
		// destroy pending joints
		Iterator<Joint> jointIter = pendingJointRemovals.iterator();
		while (jointIter.hasNext()) {
			world.destroyJoint(jointIter.next());
		}
		
		// destroy pending bodies
		Iterator<Body> bodyIter = pendingBodyRemovals.iterator();
		while (bodyIter.hasNext()) {
			world.destroyBody(bodyIter.next());
		}
		
		// destroy pending entities
		Iterator<EntityActionTuple> entityIter = pendingEntityRemovals.iterator();
		while (entityIter.hasNext()) {
			// using this tuple lets user send any custom function to be executed right before the entity is disposed of
			// this shouldn't be used often but might be useful.
			EntityActionTuple tuple = entityIter.next();
			
			// run custom function, if one was provided
			if (tuple.consumer != null) {
				tuple.consumer.accept(tuple.entity);
			}
			
			for (Component component : tuple.entity.getComponents()) {
				if (component instanceof Poolable) {
					((Poolable)component).reset(); // do we need to reset pooled components??
				}
				if (component instanceof Disposable) {
					((Disposable)component).dispose(); // do we always to dispose() components if they are also pooled?
				}
				if (component instanceof Closeable) {
					try {
						((Closeable)component).close();  // this case should like never happen :P
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// remove all pending removals
		pendingEntityRemovals.clear();
		pendingBodyRemovals.clear();
		pendingJointRemovals.clear();
	}
	
	public boolean scheduleForRemoval(Entity entity) {
		return pendingEntityRemovals.add(new EntityActionTuple(entity));
	}
	
	/*
	 * action is a custom function which is called right before removing this entity
	 */
	public boolean scheduleForRemoval(Entity entity, Consumer<Entity> action) {
		return pendingEntityRemovals.add(new EntityActionTuple(entity, action));
	}
	
	public boolean scheduleForRemoval(Body body) {
		return pendingBodyRemovals.add(body);
	}
	
	public boolean scheduleForRemoval(Joint joint) {
		return pendingJointRemovals.add(joint);
	}
}
