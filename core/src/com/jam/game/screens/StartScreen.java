package com.jam.game.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;
import utils.PlayerAnims;

public class StartScreen implements Screen{
	
	public final static int VIRTUAL_WIDTH = 32;
	public final static int VIRTUAL_HEIGHT = 32;
	
	private Music music;
	
	OrthographicCamera camera;
    FitViewport viewport;
    SpriteBatch batch;
    AnimatedSprite sprite;
    ArrayList<Animation<TextureRegion>> animationRegions;
    
    int onScreen = 0;
	@Override
	public void show() {
		music = Gdx.audio.newMusic(Gdx.files.internal("title_music.mp3"));
		music.play();
		music.setLooping(true);
		
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
		
		if (sprite.isAnimationFinished()) {
			if(onScreen == 0)
				sprite = new AnimatedSprite(animationRegions.get(++onScreen));
		}
		
		batch.end();
	}
	
	public void checkStartGameOnEnter() {
		if(sprite.isAnimationFinished() && onScreen == 1) {
			sprite = new AnimatedSprite(animationRegions.get(++onScreen));
		}
	}
	
	public boolean canStartGame() {
		if(onScreen >= 2 && sprite.isAnimationFinished()) {
			music.pause();
			//TODO Play start sound here
			return true;
		}
		return false;
	}
	
	public ArrayList<Animation<TextureRegion>> getAnimationRegions() {
		ArrayList<Animation<TextureRegion>> regions = new ArrayList<Animation<TextureRegion>>();
		Texture t = new Texture("intro_scroll.png");
		
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
		t = new Texture("title_screen.png");
		for(int i=0; i<8; i++) {
			r.set(i, new TextureRegion(t, i*32, 0, 32, 32));
		}
		
		regions.add(new Animation<TextureRegion>(0.20f, r, PlayMode.LOOP));
		 
		//Pressed Start Screen
		r = new Array<TextureRegion>(20);
		r.setSize(20);
		t = new Texture("after_press_start.png");
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
		music.dispose();
	}

}
