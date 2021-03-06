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
import com.jam.game.managers.FileManager;
import com.jam.game.utils.enums.ScreenType;

import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

public class PauseScreen implements CustomScreen{
	public final static int VIRTUAL_WIDTH = 32;
	public final static int VIRTUAL_HEIGHT = 32;

	private Game game;
	private FileManager fileManager;
			
	OrthographicCamera camera;
    FitViewport viewport;
    SpriteBatch batch;
    AnimatedSprite sprite;
    Animation<TextureRegion> animationRegion;
    
    public PauseScreen(Game game){
    	this.game = game;
    }
    
	@Override
	public void show() {		
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
			this.game.moveToNextScreen(ScreenType.START); 
		}
	}
	
	public Animation<TextureRegion> getAnimationRegions() {
		Animation<TextureRegion> region;
		Texture t = this.fileManager.getTextureFile("game_over");//new Texture("screens/game_over.png");
		
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
//		music.dispose();
		this.fileManager.clearAssets();
	}

	@Override
	public void loadAssets(FileManager manager) {
		String[] music = new String[]{
			"death_music"
		};
		
		String[] textures = new String[]{
			"game_over"
		};
		
		this.fileManager = manager;
		
		this.fileManager.loadAssets(textures, music);
	}
}
