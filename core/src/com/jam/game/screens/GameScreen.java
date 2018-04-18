package com.jam.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.jam.game.Game;
import com.jam.game.UI.UI;
import com.jam.game.b2d.Box2DContactListener;
import com.jam.game.b2d.Box2dPlatformBuilder;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.PlatformComponent;
import com.jam.game.components.PlayerComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.components.TransformComponent;
import com.jam.game.components.TypeComponent;
import com.jam.game.controllers.KeyboardController;
import com.jam.game.levels.Level;
import com.jam.game.managers.FileManager;
import com.jam.game.systems.DelayedRemovalSystem;
import com.jam.game.systems.LevelSystem;
import com.jam.game.systems.LightingSystem;
import com.jam.game.systems.PhysicsSystem;
import com.jam.game.systems.PlayerControlSystem;
import com.jam.game.systems.PlayerSystem;
import com.jam.game.systems.RenderingSystem;
import com.jam.game.utils.Mappers;
import com.jam.game.utils.PlayerAnims;
import com.jam.game.utils.Rando;
import com.jam.game.utils.enums.Category;
import com.jam.game.utils.enums.Mask;
import com.jam.game.utils.enums.PlatformType;
import com.jam.game.utils.enums.ScreenType;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;


public class GameScreen implements CustomScreen {
	
	public static final int VIRTUAL_WIDTH = 480/8;//480/10
	public static final int VIRTUAL_HEIGHT = 800/10;//
	public static final int UNIT = 2;
		
	public static FileManager fileManager;

	public static DelayedRemovalSystem removalSystem;
	
	public static boolean endGame  = false;
	
	private Game game;
	private UI ui;
		
	public boolean playerDeath = false;
	
	public final static Texture TEXTURE = new Texture("Character_Walk_Cycle.png"); //TODO: Replace with respective stuff (Character sheet)
	
	//public final static Texture PLATFORM_TEXTURE = new Texture("platform/platforms_ALL.png");
	
	Music gameMusic = Rando.coinFlip() ? Gdx.audio.newMusic(Gdx.files.internal("sounds/gameplay_music.mp3")) : Gdx.audio.newMusic(Gdx.files.internal("sounds/gameplay_music_2.mp3"));
	
	World world;
	PooledEngine engine;
	Box2DDebugRenderer b2dRenderer;
	private OrthographicCamera camera; // link to renderingsystem cam
	KeyboardController controller;
	SpriteBatch sb;
	RenderingSystem renderingSystem;
	Entity player;
	
	public GameScreen(Game game){
    	this.game = game;
    }
	
	@Override
	public void show() {
		playerDeath = false;
		gameMusic.setVolume(gameMusic.getVolume()/3);
		gameMusic.play();
		gameMusic.setLooping(true);
		
		world = new World(new Vector2(0, -20), true);
				
		sb = new SpriteBatch();
		//Create our rendering system
		renderingSystem = new RenderingSystem(sb);
		camera = renderingSystem.getCamera();
		
		engine = new PooledEngine();
		
		//Add all relevant systems
		engine.addSystem(renderingSystem);
		engine.addSystem(new PhysicsSystem(world));
//		engine.addSystem(new CollisionSystem());
		
		controller = new KeyboardController();
		engine.addSystem(new PlayerControlSystem(controller, world, camera));
		
		
		b2dRenderer = new Box2DDebugRenderer(true, true, false, false, false, false);
		b2dRenderer.JOINT_COLOR.set(Color.TAN);
		
		player = createPlayer();
		createFloor();

		world.setContactListener(new Box2DContactListener(engine.getSystem(PhysicsSystem.class)));
		
		removalSystem = new DelayedRemovalSystem(world);
		engine.addSystem(removalSystem);
		
		engine.addSystem(new LevelSystem(camera, Mappers.bodyMap.get(player).b2dBody, new Level(world)));
		engine.addSystem(new LightingSystem(player, world, camera));
		
		engine.addSystem(new PlayerSystem(player));
		
		ui = new UI(new SpriteBatch(), engine.getSystem(LevelSystem.class));
	}
		
	@Override
	public void render(float delta) {
		if(!Game.getScreenHandler().isPaused){
			engine.update(delta);
		}else{
			engine.getSystem(RenderingSystem.class).update(delta);
			engine.getSystem(LightingSystem.class).update(delta);
		}
//		b2dRenderer.render(world, camera.combined);
		
		this.ui.draw();
	}

	@Override
	public void resize(int width, int height) {
		renderingSystem.getViewport().update(width, height, true);
		engine.getSystem(LightingSystem.class).getRayHandler().useCustomViewport(
				renderingSystem.getViewport().getScreenX(), renderingSystem.getViewport().getScreenY(),
				renderingSystem.getViewport().getScreenWidth(), renderingSystem.getViewport().getScreenHeight());
	}
	
	public void gameOver(Body playerBody) {
		
		if (playerDeath) return;
		
		Music deathSound = fileManager.getMusicFile("death_sound"); //Gdx.audio.newMusic(Gdx.files.internal("sounds/death_sound.mp3"));
		deathSound.setVolume(deathSound.getVolume()/4);
		deathSound.play();
		
		gameMusic.setVolume(gameMusic.getVolume()/4);
		
		playerDeath = true;
		
		deathSound.setOnCompletionListener(new Music.OnCompletionListener() {
		    @Override
		    public void onCompletion(Music aMusic) {  
		    	gameMusic.pause();
		    	game.moveToNextScreen(ScreenType.DEATH);
		    }
		});		
		
	}
	
	Entity createPlayer() {
		Entity entity =  engine.createEntity();
		BodyComponent body = engine.createComponent(BodyComponent.class);
		TransformComponent pos = engine.createComponent(TransformComponent.class);
		AnimationComponent anim = engine.createComponent(AnimationComponent.class);
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		StateComponent st = engine.createComponent(StateComponent.class);
		
		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyDef.BodyType.DynamicBody;
		playerBodyDef.position.set(VIRTUAL_WIDTH/2.0f, 4.0f);
		playerBodyDef.fixedRotation = true;
		
		body.b2dBody = world.createBody(playerBodyDef);
		
		float sidePanelWOffset = 0.00003f; // on both sides (NOTE: This may be too small and cause errors)
		float sidePanelHOffset = 0.3f; // off ground only
		
		// make main body of player
		FixtureDef playerFixture = new FixtureDef();
		PolygonShape boxShape = new PolygonShape();
		Rectangle r = new Rectangle(sidePanelWOffset, 0, (2.0f*(UNIT/2.5f))-(2*sidePanelWOffset), 2.0f*(UNIT/1.25f));
//		Vector2 outVec = new Vector2(0, 0);
		
		boxShape.setAsBox(r.getWidth()/2.0f, r.getHeight()/2.0f); 
		playerFixture.shape = boxShape;
		playerFixture.restitution = 0.0f;
		playerFixture.friction = 1.0f;
		playerFixture.filter.categoryBits = Category.PLAYER.getValue();
		playerFixture.filter.maskBits = Mask.PLAYER.getValue();
		body.b2dBody.createFixture(playerFixture);
		
		// side panels
		FixtureDef leftSidePanel = new FixtureDef();
		r = new Rectangle(0, sidePanelHOffset, sidePanelWOffset, 2.0f*(UNIT/1.25f)-sidePanelHOffset);
		boxShape.setAsBox(r.getWidth()/2.0f, r.getHeight()/2.0f, new Vector2(-1.0f, 0.0f), 0.0f);
		leftSidePanel.shape = boxShape;
		leftSidePanel.restitution = 0.1f;
		leftSidePanel.friction = 0.0f;
		leftSidePanel.filter.categoryBits = Category.PLAYER.getValue();
		leftSidePanel.filter.maskBits = Mask.PLAYER.getValue();
		body.b2dBody.createFixture(leftSidePanel);
		
		FixtureDef rightSidePanel = new FixtureDef();
		//r = new Rectangle((2.0f*(UNIT/2.5f))-sidePanelWOffset, sidePanelHOffset, sidePanelWOffset, 2.0f*(UNIT/1.25f)-sidePanelHOffset);
		r = new Rectangle(-sidePanelWOffset, sidePanelHOffset, sidePanelWOffset, 2.0f*(UNIT/1.25f)-sidePanelHOffset);
		boxShape.setAsBox(r.getWidth()/2.0f, r.getHeight()/2.0f, new Vector2(1.0f, 0.0f), 0.0f);
		rightSidePanel.shape = boxShape;
		rightSidePanel.restitution = 0.1f;
		rightSidePanel.friction = 0.0f;
		rightSidePanel.filter.categoryBits = Category.PLAYER.getValue();
		rightSidePanel.filter.maskBits = Mask.PLAYER.getValue();
		body.b2dBody.createFixture(rightSidePanel);
		
		pos.pos.set(0,0,0);
		st.set(StateComponent.STATE_NORMAL);
				
		setPlayerAnimations(anim);
		
		anim.currentAnimation = PlayerAnims.WALK_RIGHT;
		
		entity.add(body);
		entity.add(pos);
		entity.add(anim);
		entity.add(player);
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
		
		regions.set(0, getRegion(TEXTURE, 0, 0, false, false));
		regions.set(1, getRegion(TEXTURE, 1, 0, false, false));
		regions.set(2, getRegion(TEXTURE, 2, 0, false, false));
		regions.set(1, getRegion(TEXTURE, 3, 0, false, false));
		
		AnimatedBox2DSprite rightWalk = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP_PINGPONG)));
		anim.animations.put(PlayerAnims.WALK_RIGHT, rightWalk);
		
		//Walk Left
		regions = new Array<TextureRegion>(3);
		regions.setSize(3);
		
		regions.set(0, getRegion(TEXTURE, 0, 0, true, false));
		regions.set(1, getRegion(TEXTURE, 1, 0, true, false));
		regions.set(2, getRegion(TEXTURE, 2, 0, true, false));
		regions.set(1, getRegion(TEXTURE, 3, 0, true, false));
		
		AnimatedBox2DSprite leftWalk = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP_PINGPONG)));
		anim.animations.put(PlayerAnims.WALK_LEFT, leftWalk);
		
		//Idle Right
		regions = new Array<TextureRegion>(2);
		regions.setSize(2);
		regions.set(0, getRegion(TEXTURE, 0, 1, false, false));
		regions.set(1, getRegion(TEXTURE, 1, 1, false, false));
		//regions.set(2, getRegion(TEXTURE, 2, 0, false));
		//regions.set(3, getRegion(TEXTURE, 3, 0, false));
			//blink
		//regions.set(4, getRegion(TEXTURE, 2, 0, false));
		//regions.set(5, getRegion(TEXTURE, 5, 0, false));
		AnimatedBox2DSprite idleRight = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
		anim.animations.put(PlayerAnims.IDLE_RIGHT, idleRight);
		
		//Idle Left
		regions = new Array<TextureRegion>(2);
		regions.setSize(2);
		regions.set(0, getRegion(TEXTURE, 0, 1, true, false));
		regions.set(1, getRegion(TEXTURE, 1, 1, true, false));
//		regions.set(2, getRegion(TEXTURE, 2, 0, true));
//		regions.set(3, getRegion(TEXTURE, 3, 0, true));
			//blink
//		regions.set(4, getRegion(TEXTURE, 2, 0, true));
//		regions.set(5, getRegion(TEXTURE, 5, 0, true));

		
		AnimatedBox2DSprite idleLeft = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.LOOP)));
		anim.animations.put(PlayerAnims.IDLE_LEFT, idleLeft);
		
		//Jump Right
		regions = new Array<TextureRegion>(1);
		regions.setSize(1);
		regions.set(0, getRegion(TEXTURE, 0, 2, false, true));
//		regions.set(1, getRegion(TEXTURE, 1, 1, false));
		AnimatedBox2DSprite jumpRight = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.NORMAL)));
		anim.animations.put(PlayerAnims.JUMP_RIGHT, jumpRight);
		
		//Jump Left
		regions = new Array<TextureRegion>(1);
		regions.setSize(1);
		regions.set(0, getRegion(TEXTURE, 0, 2, true, true));
//		regions.set(1, getRegion(TEXTURE, 1, 1, true));
		AnimatedBox2DSprite jumpLeft = new AnimatedBox2DSprite(new AnimatedSprite(new Animation<TextureRegion>(aniSpeed, regions, PlayMode.NORMAL)));
		anim.animations.put(PlayerAnims.JUMP_LEFT, jumpLeft);
				
		for (Entry<String, AnimatedBox2DSprite> se : anim.animations.entries()) {
			AnimatedBox2DSprite s = se.value;
			s.setUseOrigin(false);
			s.setScale(1.65f, 1.20f);
//			s.setScale(1.25f, 0.8f);
//			s.setScale(1.5f);
			s.setPosition(0, 0.25f);
		}
	}
	
	
	TextureRegion getRegion(Texture tex, int x, int y, boolean flipX, boolean isJump) {
		int width = isJump ? 20 : 16;
		if (flipX) {
			return new TextureRegion(tex, (x+1)*32 - 14, y*32 + 4, -18, 27);
		}
		
		return new TextureRegion(tex, x*32, y*32 + 4, 18, 27);
	}
	
	void createFloor() { //https://www.gamedevelopment.blog/full-libgdx-game-tutorial-entities-ashley/
		TextureRegion FLOOR_TEXTURE = new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 7);
		//TextureRegion WALL_TEXTURE = new TextureRegion(GameScreen.TEXTURE, 4*32, 6*32, 32, 7);
		
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
		floorFixture.friction = 1.0f;
		
		body.b2dBody.createFixture(floorFixture);
		
		pos.pos.set(0,0,0);
		
		
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
		engine.removeAllEntities();
		world.dispose();
		b2dRenderer.dispose();
		sb.dispose();
		gameMusic.dispose();
	}

	@Override
	public void loadAssets(FileManager manager) {
		String[] music = new String[]{
			"death_sound"
		};
		
		String[] textures = new String[]{
			"platforms",
			"powerups",
			"numbers",
			"game_over"
		};
		
		fileManager = manager;
		
		fileManager.loadAssets(textures, music);
		
//		for (PlatformType p : PlatformType.values()) {
//			p.reset(null);
//		}
	}

}
