package com.jam.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;


public class GameScreen implements Screen {
	
	public static final int VIRTUAL_WIDTH = 480;
	public static final int VIRTUAL_HEIGHT = 320;
	public static final int UNIT = 32;
	
	World b2dWorld;
	Box2DDebugRenderer b2dRenderer;
	OrthographicCamera camera;
	
	
	@Override
	public void show() {
		b2dWorld = new World(new Vector2(0.0f, -9.8f), true);
		b2dRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
//		camera.lookAt(100.0f, 100.0f, 0.0f);
		camera.position.set(new Vector3(100.0f, 100.0f, 0.0f));
		
		// test
		BodyDef b2dBodyDef = new BodyDef();
		b2dBodyDef.type = BodyDef.BodyType.StaticBody;
		b2dBodyDef.position.set(100, 100);
		
		Body b2dBody = b2dWorld.createBody(b2dBodyDef);
		
		FixtureDef b2dFixture = new FixtureDef();
		
		CircleShape circle = new CircleShape();
		circle.setRadius(6.0f);
		b2dFixture.shape = new CircleShape();
		b2dFixture.density = 0.5f;
		
		b2dBody.createFixture(b2dFixture);
		
		circle.dispose();
		
	}

	@Override
	public void render(float delta) {
		// logic
		
		b2dWorld.step(delta, 8, 3);
		
		// render
		b2dRenderer.render(b2dWorld, camera.combined);
		
		camera.lookAt(100.0f, 100.0f, 0.0f);
//		camera.position.set(new Vector3(100.0f, 100.0f, 0.0f));
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		b2dWorld.dispose();
		b2dRenderer.dispose();
	}

}
