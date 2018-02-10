package com.jam.game.b2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.jam.game.levels.Platform;

/*
 * Builder for platforms.
 * Currently supports only 1 shape/fixture per body.
 */
public class Box2dPlatformBuilder implements Disposable {
	
	private Body b2dBody;
	private BodyDef b2dBodyDef;
	private Shape b2dShape;
	private FixtureDef b2dFixture;
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder setBodyType(BodyType type) {
		b2dBodyDef.type = type;
		return this;
	}
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder setBodyPosition(Vector2 position) {
		b2dBodyDef.position.set(position);
		return this;
	}
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder setBodyPosition(float x, float y) {
		b2dBodyDef.position.set(x, y);
		return this;
	}
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder setShape(Shape shape) {
		b2dShape = shape;
		b2dFixture.shape = b2dShape;
		return this;
	}
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder setDensity(float density) {
		b2dFixture.density = density;
		return this;
	}
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder setFilter(Filter filter) {
		b2dFixture.filter.categoryBits = filter.categoryBits;
		b2dFixture.filter.maskBits = filter.maskBits;
		b2dFixture.filter.groupIndex = filter.groupIndex;
		return this;
	}
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder setFriction(float friction) {
		b2dFixture.friction = friction;
		return this;
	}
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder setRestitution(float restitution) {
		b2dFixture.restitution = restitution;
		return this;
	}
	
	/*
	 * Returns the builder for chaining.
	 */
	public Box2dPlatformBuilder isSensor(boolean isSensor) {
		b2dFixture.isSensor = isSensor;
		return this;
	}
	
	/*
	 * Build method. Disposes of shape, not body.
	 */
	public Body buildAndDispose(World world) {
		Body body = this.build(world);
		this.dispose();
		return body;
	}
	
	/*
	 * Clear all options.
	 */
	public Box2dPlatformBuilder clear() {
		this.dispose();
		return new Box2dPlatformBuilder();
	}
	
	/*
	 * Build method. Does not dispose of shape.
	 */
	public Body build(World world) {
		b2dBody = world.createBody(b2dBodyDef);
		b2dBody.createFixture(b2dFixture);
		return b2dBody;
	}
	
	public Box2dPlatformBuilder() {
		b2dBodyDef = new BodyDef();
		b2dFixture = new FixtureDef();
	}
	
	public static Box2dPlatformBuilder DEFAULT() {
		return Box2dPlatformBuilder.DEFAULT(1.5f, 0.5f);
	}
	
	public static Box2dPlatformBuilder DEFAULT(float width, float height) {
		Box2dPlatformBuilder builder = new Box2dPlatformBuilder();
		
		builder.setBodyType(BodyType.StaticBody);
		builder.setBodyPosition(0, 0);
		builder.setDensity(0.8f);
		builder.setFriction(0.08f);
		builder.setRestitution(0.9f);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);
		builder.setShape(shape);
		
		return builder;
	}
	
	public static Box2dPlatformBuilder DEFAULT(Platform platform) {
		Box2dPlatformBuilder builder = Box2dPlatformBuilder.DEFAULT(platform.width, platform.height);
		builder.setBodyPosition(platform.x, platform.y); 	
		return builder;
	}

	@Override
	public void dispose() {
		b2dShape.dispose();
	}
	
}
