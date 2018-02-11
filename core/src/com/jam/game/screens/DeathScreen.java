package com.jam.game.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class DeathScreen implements Screen{
	public final static int VIRTUAL_WIDTH = 32;
	public final static int VIRTUAL_HEIGHT = 32;
	
	private Music music;
	
	OrthographicCamera camera;
    FitViewport viewport;
    SpriteBatch batch;
    AnimatedSprite sprite;
    Animation<TextureRegion> animationRegion;
    
    Random r = new Random();
    
	@Override
	public void show() {
		float songNum = r.nextFloat();
		
		music = songNum > .5 ? Gdx.audio.newMusic(Gdx.files.internal("death_music_1.mp3")) : Gdx.audio.newMusic(Gdx.files.internal("death_music_2.mp3"));
		music.play();
		music.setLooping(true);
		
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		batch = new SpriteBatch();
		
		animationRegion = getAnimationRegions();
		sprite = new AnimatedSprite(animationRegion);
	}

	@Override
	public void render(float delta) {
		camera.update();
		batch.begin();
		
		batch.setProjectionMatrix(camera.combined);
		sprite.update(delta);
		sprite.draw(batch);
		
		batch.end();
	}
	
	public Animation<TextureRegion> getAnimationRegions() {
		Animation<TextureRegion> region;
		Texture t = new Texture("game_over.png");
		
		//Death Animation
		Array<TextureRegion> r = new Array<TextureRegion>(2);
		r.setSize(2);
		for(int i=0; i<2; i++) {
			r.set(i, new TextureRegion(t, i*32, 0, 32, 32));
		}
		region = new Animation<TextureRegion>(0.25f, r, PlayMode.LOOP);
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
		music.dispose();
	}
}
