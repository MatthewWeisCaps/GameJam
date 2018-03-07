package com.jam.game.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jam.game.Game;
import com.jam.game.managers.FileManager;
import com.jam.game.utils.enums.ScreenType;

import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class StartScreen implements CustomScreen{
	private Game game;
	
	public final static int VIRTUAL_WIDTH = 32;
	public final static int VIRTUAL_HEIGHT = 32;
	
	private FileManager fileManager;
	
	private Music song;
	private Music soundEfct;
	
	OrthographicCamera camera;
    FitViewport viewport;
    SpriteBatch batch;
    AnimatedSprite sprite;
    ArrayList<Animation<TextureRegion>> animationRegions;
    
    int onScreen = 0;
    
    public StartScreen(Game game){
    	this.game = game;
    }
    
	@Override
	public void show() {
		
		song = this.fileManager.getMusicFile("title_music"); //Gdx.audio.newMusic(Gdx.files.internal("sounds/title_music.mp3"));
		
		soundEfct = this.fileManager.getMusicFile("start_sound"); //Gdx.audio.newMusic(Gdx.files.internal("sounds/start_sound.mp3"));
		soundEfct.setVolume(soundEfct.getVolume()/2);
		
		song.setVolume(song.getVolume()/3);
		song.play();
		song.setLooping(true);
		
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		batch = new SpriteBatch();
		
		animationRegions = getAnimationRegions();
		sprite = new AnimatedSprite(animationRegions.get(onScreen));
	}

	@Override
	public void render(float delta) {
		camera.update();
		batch.begin();
		
		batch.setProjectionMatrix(camera.combined);
		sprite.update(delta);
		sprite.draw(batch);
		
		batch.end();
		
		if (sprite.isAnimationFinished()) {
			if(onScreen == 0){
				sprite = new AnimatedSprite(animationRegions.get(++onScreen));
			} else if(onScreen == 1){
				if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
					sprite = new AnimatedSprite(animationRegions.get(++onScreen));
					song.setVolume(song.getVolume()/2);
					soundEfct.play();
				}
			}else if(onScreen >= 2){
				song.pause();
				this.game.moveToNextScreen(ScreenType.PLAY);
			}
			
		}
	}
	
	public ArrayList<Animation<TextureRegion>> getAnimationRegions() {
		ArrayList<Animation<TextureRegion>> regions = new ArrayList<Animation<TextureRegion>>();
		Texture t = this.fileManager.getTextureFile("intro_scroll");//new Texture("screens/intro_scroll.png");
		
		//Intro
		Array<TextureRegion> r = new Array<TextureRegion>(43);
		r.setSize(43);
		for(int i=0; i<43; i++) {
			r.set(i, new TextureRegion(t, i*32, 0, 32, 32));
		}
		
		regions.add(new Animation<TextureRegion>(0.20f, r, PlayMode.NORMAL));
		
		//Start Screen
		r = new Array<TextureRegion>(8);
		r.setSize(8);
		t = this.fileManager.getTextureFile("title_screen");//new Texture("screens/title_screen.png");
		for(int i=0; i<8; i++) {
			r.set(i, new TextureRegion(t, i*32, 0, 32, 32));
		}
		
		regions.add(new Animation<TextureRegion>(0.20f, r, PlayMode.LOOP));
		 
		//Pressed Start Screen
		r = new Array<TextureRegion>(20);
		r.setSize(20);
		t = this.fileManager.getTextureFile("after_press_start");//new Texture("screens/after_press_start.png");
		for(int i=0; i<20; i++) {
			r.set(i, new TextureRegion(t, i*32, 0, 32, 32));
		}
		 
		regions.add(new Animation<TextureRegion>(0.20f, r, PlayMode.NORMAL));
		 
		return regions;
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
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
		batch.dispose();
//		song.dispose();
//		soundEfct.dispose();
		this.fileManager.clearAssets();
	}

	@Override
	public void loadAssets(FileManager manager) {
		String[] music = new String[]{
			//"title_scroll_music", //wtf is this one??
			"title_music",
			"start_sound"
		};
		
		String[] textures = new String[]{
			"title_screen",
			"intro_scroll",
			"after_press_start"
		};
		
		this.fileManager = manager;
		
		this.fileManager.loadAssets(textures, music);
	}

}
