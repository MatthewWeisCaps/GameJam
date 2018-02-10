package com.jam.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.CollisionComponent;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.components.TextureComponent;
import com.jam.game.components.TransformComponent;
import com.jam.game.components.TypeComponent;
import com.jam.game.systems.AnimationSystem;
import com.jam.game.systems.CollisionSystem;
import com.jam.game.systems.PhysicsSystem;
import com.jam.game.systems.PlayerControlSystem;
import com.jam.game.systems.RenderingSystem;

import Utils.PlayerAnims;
import controllers.KeyboardController;
import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;


public class GameScreen implements Screen {
	
	public static final int VIRTUAL_WIDTH = 480/8;//480
	public static final int VIRTUAL_HEIGHT = 320/8; //320
	public static final int UNIT = 2;
	
	World world;
	PooledEngine engine;
	Box2DDebugRenderer b2dRenderer;
	private static OrthographicCamera camera;
	KeyboardController controller;
	SpriteBatch sb;
	
	@Override
	public void show() {
		world = new World(new Vector2(0, -10f), true);
		
		sb = new SpriteBatch();
		//Create our rendering system
		RenderingSystem renderingSystem = new RenderingSystem(sb);
		camera = RenderingSystem.getCamera();
		sb.setProjectionMatrix(camera.combined);
		
		engine = new PooledEngine();
		
		//Add all relevant systems
		engine.addSystem(renderingSystem);
		engine.addSystem(new PhysicsSystem(world));
		engine.addSystem(new CollisionSystem());
		
		controller = new KeyboardController();
		engine.addSystem(new PlayerControlSystem(controller));
		
		b2dRenderer = new Box2DDebugRenderer();
		
		createPlayer();
		createFloor();		
	}

	@Override
	public void render(float delta) {

		// logic
		
		//world.step(delta, 8, 3);
		engine.update(delta);
		
		// render
		b2dRenderer.render(world, camera.combined);
		
		//camera.lookAt(100.0f, 100.0f, 0.0f);
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
		world.dispose();
		b2dRenderer.dispose();
		sb.dispose();
	}
	
	void createPlayer() {
		Entity entity =  engine.createEntity();
		BodyComponent body = engine.createComponent(BodyComponent.class);
		TransformComponent pos = engine.createComponent(TransformComponent.class);
//		TextureComponent tex = engine.createComponent(TextureComponent.class);
		AnimationComponent anim = engine.createComponent(AnimationComponent.class);
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		CollisionComponent col = engine.createComponent(CollisionComponent.class);
		TypeComponent t = engine.createComponent(TypeComponent.class);
		StateComponent st = engine.createComponent(StateComponent.class);
		
		//sorry bro
		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyDef.BodyType.DynamicBody;
		playerBodyDef.position.set(10, 10);
		
		body.b2dBody = world.createBody(playerBodyDef);
		FixtureDef playerFixture = new FixtureDef();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(UNIT/2, UNIT/2);
		
		
		playerFixture.shape = boxShape;
		playerFixture.restitution = 0.0f;
		
		body.b2dBody.createFixture(playerFixture);
		
		pos.pos.set(0,0,0);
		t.type = TypeComponent.PLAYER;
		st.set(StateComponent.STATE_NORMAL);
		
		Texture playerTexture = new Texture("player_anim.png");
//		TextureRegion playerRegion = new TextureRegion(playerTexture);		
//		tex.region = playerRegion;
		
		
		// anim setup
		
		Array<TextureRegion> regions = new Array<TextureRegion>(8);
		regions.setSize(8);
		for (int i=0; i < 8; i++) {
			int y = 0;
			int x = i*32;
			regions.set(i, new TextureRegion(playerTexture, x, y, 32, 32));
		}
		System.out.println(regions.size);
		AnimatedBox2DSprite rightWalk = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(0.6f, regions, PlayMode.LOOP)));
		anim.animations.put(PlayerAnims.WALK_RIGHT, rightWalk);
		anim.currentAnimation = PlayerAnims.WALK_RIGHT;
//		anim.animations.get(PlayerAnims.WALK_RIGHT).
		
		entity.add(body);
		entity.add(pos);
//		entity.add(tex);
		entity.add(anim);
		entity.add(player);
		entity.add(col);
		entity.add(t);
		entity.add(st);
		
		engine.addEntity(entity);
		boxShape.dispose();
		
	}
	
	TextureRegion getRegion(Texture tex, int x, int y) {
		return new TextureRegion(tex, x*32, y*32, 32, 32);
	}
	
	void createFloor() { //https://www.gamedevelopment.blog/full-libgdx-game-tutorial-entities-ashley/
		Entity entity = engine.createEntity();
		BodyComponent body = engine.createComponent(BodyComponent.class);
		
		BodyDef floorBodyDef = new BodyDef();
		floorBodyDef.type = BodyDef.BodyType.StaticBody;
		floorBodyDef.position.set(0,0);
		
		body.b2dBody = world.createBody(floorBodyDef);
		
		FixtureDef floorFixture = new FixtureDef();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(VIRTUAL_WIDTH/2, UNIT/2);
		
		floorFixture.shape = boxShape;
		floorFixture.restitution = 0.0f;
		floorFixture.friction = 1.0f;
		
		body.b2dBody.createFixture(floorFixture);
		
		TypeComponent type = engine.createComponent(TypeComponent.class);
		type.type = TypeComponent.WALL;
		
		entity.add(body);
		entity.add(type);
		
		engine.addEntity(entity);
		
				
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
