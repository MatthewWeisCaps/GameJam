package com.jam.game.screens;

import java.awt.Rectangle;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.values.RectangleSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.systems.AnimationSystem;


public class GameScreen implements Screen {
	
	public static final int VIRTUAL_WIDTH = 480/8;//480
	public static final int VIRTUAL_HEIGHT = 320/8; //320
	public static final int UNIT = 2;
	
	World b2dWorld;
	Box2DDebugRenderer b2dRenderer;
	OrthographicCamera camera;
	
	Engine engine = new Engine();
	Entity playerEntity = new Entity();
	
	
	Body player;
	int speed = 50;
	
	@Override
	public void show() {
		b2dWorld = new World(new Vector2(0.0f, -9.8f), true);
		b2dRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
//		camera.lookAt(100.0f, 100.0f, 0.0f);
		camera.position.set(new Vector3(100.0f, 100.0f, 0.0f));
		
		//Temp Player
		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyDef.BodyType.DynamicBody;
		playerBodyDef.position.set(0, 0);
		
		player = b2dWorld.createBody(playerBodyDef);
		
		FixtureDef playerFixture = new FixtureDef();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(UNIT/2, UNIT/2);
		
		playerFixture.shape = boxShape;
		playerFixture.restitution = 0.4f;
		
		player.createFixture(playerFixture);
		
		boxShape.dispose();
		
		// test
		BodyDef b2dBodyDef = new BodyDef();
		b2dBodyDef.type = BodyDef.BodyType.StaticBody;
		b2dBodyDef.position.set(0, -VIRTUAL_HEIGHT/2);
		
		Body b2dBody = b2dWorld.createBody(b2dBodyDef);
		
		FixtureDef floorFixture = new FixtureDef();
		
		PolygonShape floor = new PolygonShape();
		floor.setAsBox(VIRTUAL_WIDTH, UNIT/2);
		
		floorFixture.shape = floor;
		floorFixture.restitution = 0.4f;
		
		b2dBody.createFixture(floorFixture);
		floor.dispose();
		
		setupInputHandler();
		
		//
		engine.addSystem(new AnimationSystem(new SpriteBatch()));
		
		playerEntity.add(new AnimationComponent());
		playerEntity.add(new BodyComponent(player));
		
		engine.addEntity(playerEntity);
		
	}

	@Override
	public void render(float delta) {
		// logic
		checkInput();
		
		b2dWorld.step(delta, 8, 3);
		engine.update(delta);
		
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
	
	void checkInput() {
		float xDir = 0;
		float yDir = 0;
		
		if(Gdx.input.isKeyPressed(Keys.W)) {
			yDir = 1;
		} 
		if(Gdx.input.isKeyPressed(Keys.S)) {
			yDir = -1;
		}
		
		if(Gdx.input.isKeyPressed(Keys.A)) {
			xDir = -1;
		} 
		if(Gdx.input.isKeyPressed(Keys.D)) {
			xDir = 1;
		}
		
		
		player.setLinearVelocity(new Vector2(xDir * speed, yDir * speed));
		
		
	}
	
	void setupInputHandler() {
		Gdx.input.setInputProcessor(new InputProcessor() {

			
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
					case Keys.ESCAPE:
						Gdx.app.exit();
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				camera.zoom += 0.05 * amount;
				
//				camera.position.set(camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0.0f)));
				
				camera.update();
				return false;
			}
			
		});
	}

}
