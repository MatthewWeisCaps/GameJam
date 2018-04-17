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

public class SplashScreen implements CustomScreen{
	private Game game;
	
	public final static int VIRTUAL_WIDTH = 96;
	public final static int VIRTUAL_HEIGHT = 96;
	
	private FileManager fileManager;
	
//	private Music song;
	private Music soundEfct;
	
	OrthographicCamera camera;
    FitViewport viewport;
    SpriteBatch batch;
    AnimatedSprite sprite;
    Animation<TextureRegion> animationRegions;
    
    int onScreen = 0;
    
    public SplashScreen(Game game){
    	this.game = game;
    }
    
	@Override
	public void show() {
				
		soundEfct = this.fileManager.getMusicFile("splash_screen"); //Gdx.audio.newMusic(Gdx.files.internal("sounds/start_sound.mp3"));
		soundEfct.setVolume(soundEfct.getVolume()/2);
		soundEfct.setLooping(false);
		soundEfct.play();
		
//		song.setVolume(song.getVolume()/3);
//		song.play();
//		song.setLooping(true);
		
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		batch = new SpriteBatch();
		
		animationRegions = getAnimationRegions();
		sprite = new AnimatedSprite(animationRegions);
	}

	@Override
	public void render(float delta) {
		if((sprite.isAnimationFinished() && !this.soundEfct.isPlaying()) || (sprite.isAnimationFinished() && Gdx.input.isKeyJustPressed(Keys.ANY_KEY))){
			this.soundEfct.pause();
			this.game.moveToNextScreen(ScreenType.START);
		}
		camera.update();
		batch.begin();
		
		batch.setProjectionMatrix(camera.combined);
		sprite.update(delta);
		sprite.draw(batch);
		
		batch.end();
		
//		song.setVolume(song.getVolume()/2);
//		soundEfct.play();
	}
	
	public Animation<TextureRegion> getAnimationRegions() {
		Animation<TextureRegion> region;
		
		Texture t = this.fileManager.getTextureFile("intro_splash");//new Texture("screens/intro_scroll.png");

		//Pressed Start Screen
		Array<TextureRegion> r = new Array<TextureRegion>(25);
		r.setSize(25);
		t = this.fileManager.getTextureFile("intro_splash");//new Texture("screens/after_press_start.png");
		for(int i=0; i<25; i++) {
			r.set(i, new TextureRegion(t, i*96, 0, 96, 96));
		}
		 
		region = new Animation<TextureRegion>(0.20f, r, PlayMode.NORMAL);
		 
		return region;
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
			"splash_screen"
		};
		
		String[] textures = new String[]{
			"intro_splash"
		};
		
		this.fileManager = manager;
		
		this.fileManager.loadAssets(textures, music);
	}

}
