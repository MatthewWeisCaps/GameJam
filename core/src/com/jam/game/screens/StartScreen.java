package com.jam.game.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class StartScreen implements Screen{
	private Game game;
	
	public final static int VIRTUAL_WIDTHSS = 32;
	public final static int VIRTUAL_HEIGHTSS = 32;
	
	private Music music;
	
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
		int old_Width = Gdx.graphics.getWidth();
		int old_Height = Gdx.graphics.getHeight();
		Gdx.graphics.setWindowedMode(old_Width - 1, old_Height - 1);
		Gdx.graphics.setWindowedMode(old_Width + 1, old_Height + 1);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("title_music.mp3"));
		
		music.setVolume(music.getVolume()/3);
		music.play();
		music.setLooping(true);
		
		camera = new OrthographicCamera(VIRTUAL_WIDTHSS, VIRTUAL_HEIGHTSS);
		viewport = new FitViewport(VIRTUAL_WIDTHSS, VIRTUAL_HEIGHTSS, camera);
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
				}
			}else if(onScreen >= 2){
				music.pause();
				this.game.moveToNextScreen(ScreenType.PLAY);
			}
			
		}
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
