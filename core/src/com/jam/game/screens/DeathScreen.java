package com.jam.game.screens;

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

public class DeathScreen implements Screen{
	public final static int VIRTUAL_WIDTH = 32;
	public final static int VIRTUAL_HEIGHT = 32;

	private Game game;
	
	private Music music;
		
	OrthographicCamera camera;
    FitViewport viewport;
    SpriteBatch batch;
    AnimatedSprite sprite;
    Animation<TextureRegion> animationRegion;
    
    public DeathScreen(Game game){
    	this.game = game;
    }
    
	@Override
	public void show() {
		int old_Width = Gdx.graphics.getWidth();
		int old_Height = Gdx.graphics.getHeight();
		Gdx.graphics.setWindowedMode(old_Width - 1, old_Height - 1);
		Gdx.graphics.setWindowedMode(old_Width + 1, old_Height + 1);
				
		music = Gdx.audio.newMusic(Gdx.files.internal("death_music_2_cut.mp3"));
		music.setVolume(music.getVolume()/3);
		music.play();
		music.setLooping(false);
		
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
		
		if(Gdx.input.isKeyJustPressed(Keys.ANY_KEY)) {
			music.pause();
			this.game.moveToNextScreen(ScreenType.START); 
		}
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
