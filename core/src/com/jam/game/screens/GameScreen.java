package com.jam.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jam.game.Game;
import com.jam.game.b2d.Box2DContactListener;
import com.jam.game.b2d.Box2dPlatformBuilder;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.CollisionComponent;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.components.TextureComponent;
import com.jam.game.components.TransformComponent;
import com.jam.game.components.TypeComponent;
import com.jam.game.levels.Level;
import com.jam.game.systems.AnimationSystem;
import com.jam.game.systems.CollisionSystem;
import com.jam.game.systems.LevelSystem;
import com.jam.game.systems.LightingSystem;
import com.jam.game.systems.PhysicsSystem;
import com.jam.game.systems.PlayerControlSystem;
import com.jam.game.systems.RenderingSystem;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import utils.Mappers;
import utils.PlayerAnims;
import controllers.KeyboardController;
import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;


public class GameScreen implements Screen {
	
	private Game game;
	public static boolean doneLoading = false;
	public static boolean finishedDeathStuff = false;
	
	public static final int VIRTUAL_WIDTH = 480/10;//380/8;//480
	public static final int VIRTUAL_HEIGHT = 800/10;//300/8; //320
	public static final int UNIT = 2;
	
	public boolean playerDeath = false;
	
	public final static Texture TEXTURE = new Texture("full_sheet.png");
	
	static Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal("gameplay_music.mp3"));
	
	World world;
	PooledEngine engine;
	Box2DDebugRenderer b2dRenderer;
	private OrthographicCamera camera; // link to renderingsystem cam
	KeyboardController controller;
	SpriteBatch sb;
	RenderingSystem renderingSystem;
	Entity player;
	
	public GameScreen(Game game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		playerDeath = false;
		finishedDeathStuff = false;
		gameMusic.play();
		gameMusic.setLooping(true);
		
		//Gross hack 
		int old_Width = Gdx.graphics.getWidth();
		int old_Height = Gdx.graphics.getHeight();
		Gdx.graphics.setWindowedMode(old_Width - 1, old_Height - 1);
		Gdx.graphics.setWindowedMode(old_Width + 1, old_Height + 1);
		
		world = new World(new Vector2(0, -20f), true);
		
		sb = new SpriteBatch();
		//Create our rendering system
		renderingSystem = new RenderingSystem(sb);
		camera = renderingSystem.getCamera();
//		sb.setProjectionMatrix(camera.combined);
				
		engine = new PooledEngine();
		
		//Add all relevant systems
		engine.addSystem(renderingSystem);
		engine.addSystem(new PhysicsSystem(world));
		engine.addSystem(new CollisionSystem());
		
		controller = new KeyboardController();
		engine.addSystem(new PlayerControlSystem(controller, world, camera));
		
		
		b2dRenderer = new Box2DDebugRenderer(false, true, false, false, false, false);
		b2dRenderer.JOINT_COLOR.set(Color.TAN);
		
		player = createPlayer();
		createFloor();

		world.setContactListener(new Box2DContactListener(world, engine.getSystem(PhysicsSystem.class)));
		
		engine.addSystem(new LevelSystem(camera, Mappers.bodyMap.get(player).b2dBody, new Level(world)));
		engine.addSystem(new LightingSystem(player, world, camera));
		
		//safe
		doneLoading = true;
		
		font.getData().setScale(0.40f);
	}
	
	private BitmapFont font = new BitmapFont();
	
	@Override
	public void render(float delta) {
		if(!doneLoading) { 
			resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			System.out.println("RETURNING NOT DONE YET");
			return;
		}
		// logic
		
		//world.step(delta, 8, 3);
		
		engine.update(delta);
		
		if(!playerDeath && doneLoading) {
			Vector3 player3D = camera.project(new Vector3(Mappers.bodyMap.get(player).b2dBody.getPosition(), 0.0f), renderingSystem.getViewport().getScreenX(), renderingSystem.getViewport().getScreenY(), renderingSystem.getViewport().getScreenWidth(), renderingSystem.getViewport().getScreenHeight());
			
			if(player3D.y < -32.0f) {
				playerDeath = true;
				death();
			}
		}

		SpriteBatch sb = engine.getSystem(RenderingSystem.class).getBatch();
		sb.begin();
		font.draw(sb, "move = wsad\nthrow rope = q\nrelease rope = e\nstand still for light", 5, 3);
		sb.end();

		//if(Gdx.input.isKeyJustPressed(Keys.V)) playDeathSound();
		
		// render
//		b2dRenderer.render(world, camera.combined);
		
		
		
//		engine.getSystem(RenderingSystem.class).getBatch().begin();
//		
//		Mappers.animationMap.get(player).animations.get(Mappers.animationMap.get(player).currentAnimation)
//			.draw(engine.getSystem(RenderingSystem.class).getBatch(), Mappers.bodyMap.get(player).b2dBody);
//		engine.getSystem(RenderingSystem.class).getBatch().end();
//		
		Gdx.graphics.setTitle("Lantern");
		//camera.lookAt(100.0f, 100.0f, 0.0f);
//		camera.position.set(new Vector3(100.0f, 100.0f, 0.0f));
	}

	@Override
	public void resize(int width, int height) {
		renderingSystem.getViewport().update(width, height, true);
		engine.getSystem(LightingSystem.class).getRayHandler().useCustomViewport(
				renderingSystem.getViewport().getScreenX(), renderingSystem.getViewport().getScreenY(),
				renderingSystem.getViewport().getScreenWidth(), renderingSystem.getViewport().getScreenHeight());
	}

	
	public void death() {
		Music deathSound =  Gdx.audio.newMusic(Gdx.files.internal("death_effect_cut.mp3"));
		deathSound.play();
		
		gameMusic.setVolume(gameMusic.getVolume()/4);
		
		deathSound.setOnCompletionListener(new Music.OnCompletionListener() {
		    @Override
		    public void onCompletion(Music aMusic) {  
		        finishedDeathStuff = true;
		    }
		});		
	}

	public void listener() {
		
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
		gameMusic.dispose();
		engine.removeAllEntities();
	}
	
	Entity createPlayer() {
		Entity entity =  engine.createEntity();
		BodyComponent body = engine.createComponent(BodyComponent.class);
		TransformComponent pos = engine.createComponent(TransformComponent.class);
		AnimationComponent anim = engine.createComponent(AnimationComponent.class);
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		CollisionComponent col = engine.createComponent(CollisionComponent.class);
		TypeComponent t = engine.createComponent(TypeComponent.class);
		StateComponent st = engine.createComponent(StateComponent.class);
		
		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyDef.BodyType.DynamicBody;
		playerBodyDef.position.set(VIRTUAL_WIDTH/2.0f, 4.0f);
		
		body.b2dBody = world.createBody(playerBodyDef);
		FixtureDef playerFixture = new FixtureDef();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(UNIT/2.5f, UNIT/1.25f);
		
		
		playerFixture.shape = boxShape;
		playerFixture.restitution = 0.0f;
		playerFixture.friction = 0.0f;
		
		body.b2dBody.createFixture(playerFixture);
		
		pos.pos.set(0,0,0);
		t.type = TypeComponent.PLAYER;
		st.set(StateComponent.STATE_NORMAL);
				
		setPlayerAnimations(anim);
		
		anim.currentAnimation = PlayerAnims.WALK_RIGHT;
		
		entity.add(body);
		entity.add(pos);
		entity.add(anim);
		entity.add(player);
		entity.add(col);
		entity.add(t);
		entity.add(st);
		
		engine.addEntity(entity);
		boxShape.dispose();
		
		// set user data
		body.b2dBody.setUserData(entity);
		
		return entity;
	}
	
	void setPlayerAnimations(AnimationComponent anim) {
		float aniSpeed = 0.2f;
		
		//Walk Right
		Array<TextureRegion> regions = new Array<TextureRegion>(3);
		regions.setSize(3);
		
		regions.set(0, getRegion(TEXTURE, 6, 0, false));
		regions.set(1, getRegion(TEXTURE, 2, 0, false));
		regions.set(2, getRegion(TEXTURE, 7, 0, false));
		regions.set(1, getRegion(TEXTURE, 2, 0, false));
		
		AnimatedBox2DSprite rightWalk = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP_PINGPONG)));
		anim.animations.put(PlayerAnims.WALK_RIGHT, rightWalk);
		
		//Walk Left
		regions = new Array<TextureRegion>(3);
		regions.setSize(3);
		
		regions.set(0, getRegion(TEXTURE, 6, 0, true));
		regions.set(1, getRegion(TEXTURE, 2, 0, true));
		regions.set(2, getRegion(TEXTURE, 7, 0, true));
		regions.set(1, getRegion(TEXTURE, 2, 0, true));
		
		AnimatedBox2DSprite leftWalk = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP_PINGPONG)));
		anim.animations.put(PlayerAnims.WALK_LEFT, leftWalk);
		
		//Idle Right
		regions = new Array<TextureRegion>(6);
		regions.setSize(6);
		regions.set(0, getRegion(TEXTURE, 2, 0, false));
		regions.set(1, getRegion(TEXTURE, 3, 0, false));
		regions.set(2, getRegion(TEXTURE, 2, 0, false));
		regions.set(3, getRegion(TEXTURE, 3, 0, false));
			//blink
		regions.set(4, getRegion(TEXTURE, 2, 0, false));
		regions.set(5, getRegion(TEXTURE, 5, 0, false));
		AnimatedBox2DSprite idleRight = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
		anim.animations.put(PlayerAnims.IDLE_RIGHT, idleRight);
		
		//Idle Left
		regions = new Array<TextureRegion>(6);
		regions.setSize(6);
		regions.set(0, getRegion(TEXTURE, 2, 0, true));
		regions.set(1, getRegion(TEXTURE, 3, 0, true));
		regions.set(2, getRegion(TEXTURE, 2, 0, true));
		regions.set(3, getRegion(TEXTURE, 3, 0, true));
			//blink
		regions.set(4, getRegion(TEXTURE, 2, 0, true));
		regions.set(5, getRegion(TEXTURE, 5, 0, true));

		
		AnimatedBox2DSprite idleLeft = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
		anim.animations.put(PlayerAnims.IDLE_LEFT, idleLeft);
		
		//Jump Right
		regions = new Array<TextureRegion>(2);
		regions.setSize(2);
		regions.set(0, getRegion(TEXTURE, 0, 1, false));
		regions.set(1, getRegion(TEXTURE, 1, 1, false));
		AnimatedBox2DSprite jumpRight = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.NORMAL)));
		anim.animations.put(PlayerAnims.JUMP_RIGHT, jumpRight);
		
		//Jump Left
		regions = new Array<TextureRegion>(2);
		regions.setSize(2);
		regions.set(0, getRegion(TEXTURE, 0, 1, true));
		regions.set(1, getRegion(TEXTURE, 1, 1, true));
		AnimatedBox2DSprite jumpLeft = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.NORMAL)));
		anim.animations.put(PlayerAnims.JUMP_LEFT, jumpLeft);
				
		for (Entry<String, AnimatedBox2DSprite> se : anim.animations.entries()) {
			AnimatedBox2DSprite s = se.value;
//			s.setAdjustHeight(false);
//			s.setAdjustWidth(false);
			s.setUseOrigin(false);
//			s.setScale(0.15f);
			s.setScale(1.65f, 1.20f);
			s.setPosition(0, 0.25f);
//			s.setPosition(-UNIT/0.95f, -UNIT/1.25f);
		}
	}
	
//	TextureRegion getRegion(Texture tex, int x, int y, boolean flipX) {
//		if (flipX) {
//			return new TextureRegion(tex, (x+1)*32 - 2, y*32, -32, 32);
//		}
//		return new TextureRegion(tex, x*32, y*32, 32, 32);		
//	}
	
	TextureRegion getRegion(Texture tex, int x, int y, boolean flipX) {
		if (flipX) {
			return new TextureRegion(tex, (x+1)*32 - 8, y*32 + 8, -16, 24);
		}
		
		return new TextureRegion(tex, x*32 + 9, y*32 + 8, 16, 24);
	}
	
	void createFloor() { //https://www.gamedevelopment.blog/full-libgdx-game-tutorial-entities-ashley/
		TextureRegion FLOOR_TEXTURE = new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 7);
		TextureRegion WALL_TEXTURE = new TextureRegion(GameScreen.TEXTURE, 4*32, 6*32, 32, 7);
		
		Entity entity = engine.createEntity();
		BodyComponent body = engine.createComponent(BodyComponent.class);
		AnimationComponent anim = engine.createComponent(AnimationComponent.class);
		TransformComponent pos = engine.createComponent(TransformComponent.class);


		BodyDef floorBodyDef = new BodyDef();
		floorBodyDef.type = BodyDef.BodyType.StaticBody;
		floorBodyDef.position.set(VIRTUAL_WIDTH/2.0f,0);
		
		body.b2dBody = world.createBody(floorBodyDef);
		
		FixtureDef floorFixture = new FixtureDef();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(VIRTUAL_WIDTH/2, UNIT);
		
		floorFixture.shape = boxShape;
		floorFixture.restitution = 0.0f;
		floorFixture.friction = 0.0f;
		
		body.b2dBody.createFixture(floorFixture);
		
		pos.pos.set(0,32,0);
		
		
		Array<TextureRegion> one = new Array<TextureRegion>();
		one.setSize(1);
		one.set(0, FLOOR_TEXTURE);
		
		one.add(FLOOR_TEXTURE);
		
		final String def = "DEFAULT";
		anim.animations.put(def, new AnimatedBox2DSprite(new AnimatedSprite(
				new Animation<TextureRegion>(0.0f, one, PlayMode.NORMAL))));
		anim.currentAnimation = def;
		
		entity.add(anim);
		entity.add(body);
		entity.add(pos);
		
		engine.addEntity(entity);
		boxShape.dispose();
		
		createLeftWall();
		createRightWall();
	}
	
	void createLeftWall() {
		TextureRegion WALL_TEXTURE = new TextureRegion(GameScreen.TEXTURE, 4*32, 6*32, 6, 32);
		
		Entity entity = engine.createEntity();
		BodyComponent body = engine.createComponent(BodyComponent.class);
		AnimationComponent anim = engine.createComponent(AnimationComponent.class);
		TransformComponent pos = engine.createComponent(TransformComponent.class);


		BodyDef floorBodyDef = new BodyDef();
		floorBodyDef.type = BodyDef.BodyType.StaticBody;
		floorBodyDef.position.set(0,VIRTUAL_HEIGHT*2);
		
		body.b2dBody = world.createBody(floorBodyDef);
		
		FixtureDef floorFixture = new FixtureDef();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(UNIT, VIRTUAL_HEIGHT*2);
		
		floorFixture.shape = boxShape;
		floorFixture.restitution = 0.0f;
		floorFixture.friction = 0.0f;
		
		body.b2dBody.createFixture(floorFixture);
		
		pos.pos.set(128,32,0);
		
		
		Array<TextureRegion> one = new Array<TextureRegion>();
		one.setSize(1);
		one.set(0, WALL_TEXTURE);
		
		one.add(WALL_TEXTURE);
		
		final String def = "DEFAULT";
		anim.animations.put(def, new AnimatedBox2DSprite(new AnimatedSprite(
				new Animation<TextureRegion>(0.0f, one, PlayMode.NORMAL))));
		anim.currentAnimation = def;
		
		entity.add(anim);
		entity.add(body);
		entity.add(pos);
		
		engine.addEntity(entity);
		boxShape.dispose();
	}
	
	void createRightWall() {
		TextureRegion WALL_TEXTURE = new TextureRegion(GameScreen.TEXTURE, 4*32, 6*32, 6, 32);
		//WALL_TEXTURE.flip(true, false);
		
		Entity entity = engine.createEntity();
		BodyComponent body = engine.createComponent(BodyComponent.class);
		AnimationComponent anim = engine.createComponent(AnimationComponent.class);
		TransformComponent pos = engine.createComponent(TransformComponent.class);


		BodyDef floorBodyDef = new BodyDef();
		floorBodyDef.type = BodyDef.BodyType.StaticBody;
		floorBodyDef.position.set(VIRTUAL_WIDTH,VIRTUAL_HEIGHT*2);
		
		body.b2dBody = world.createBody(floorBodyDef);
		
		FixtureDef floorFixture = new FixtureDef();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(UNIT, VIRTUAL_HEIGHT*2);
		
		floorFixture.shape = boxShape;
		floorFixture.restitution = 0.0f;
		floorFixture.friction = 0.0f;
		
		body.b2dBody.createFixture(floorFixture);
		
		pos.pos.set(128,32,0);
		
		
		Array<TextureRegion> one = new Array<TextureRegion>();
		one.setSize(1);
		one.set(0, WALL_TEXTURE);
		
		one.add(WALL_TEXTURE);
		
		final String def = "DEFAULT";
		anim.animations.put(def, new AnimatedBox2DSprite(new AnimatedSprite(
				new Animation<TextureRegion>(0.0f, one, PlayMode.NORMAL))));
		anim.currentAnimation = def;
		
		entity.add(anim);
		entity.add(body);
		entity.add(pos);
		
		engine.addEntity(entity);
		boxShape.dispose();
	}

}
