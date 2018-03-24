package com.jam.game.managers;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.jam.game.utils.FileMapper;
import com.jam.game.utils.Tuple;


public class FileManager implements Disposable {
	
	private HashMap<String, String> fileMap = new HashMap<String, String>();
	public AssetManager manager;
	
	public FileManager(){
		this.manager = new AssetManager();
		
		this.setupMap();
	}
	
	public void loadAssets(String[] textures, String[] music){
		for(int i=0; i<music.length; i++){
			safeLoad(this.fileMap.get(music[i]), Music.class);
		}	
		
		for(int i=0; i<textures.length; i++){
			safeLoad(this.fileMap.get(textures[i]), Texture.class);
		}	
		
		this.manager.finishLoading();
	}
	
	private <T> void safeLoad(String filename, Class<T> type) {
		if (this.manager.getAssetNames().contains(filename, false)) {
			this.manager.unload(filename);
		}
		this.manager.load(filename, type);
	}
	
	public Music getMusicFile(String fileName){		
		return this.manager.get(fileMap.get(fileName));
	}
	
	public Texture getTextureFile(String fileName){
		return this.manager.get(fileMap.get(fileName));
	}
	
	public void clearAssets(){
		manager.clear();
	}
	
	@Override
	public void dispose(){
		manager.dispose();
	}
	
	//Note: this might be a super shit way of doing this...
	public void setupMap(){
		//Add Sounds:
		for(int i=0; i<FileMapper.sounds.length; i++){
			Tuple p = FileMapper.sounds[i];
			this.fileMap.put(p.getKey(), p.getValue());
		}
		
		//Add Screens:
		for(int i=0; i<FileMapper.screens.length; i++){
			Tuple p = FileMapper.screens[i];
			this.fileMap.put(p.getKey(), p.getValue());
		}
		
		//Add Platforms:
		for(int i=0; i<FileMapper.platforms.length; i++){
			Tuple p = FileMapper.platforms[i];
			this.fileMap.put(p.getKey(), p.getValue());
		}
		
		//Add Powerups:
		for(int i=0; i<FileMapper.powerups.length; i++){
			Tuple p = FileMapper.powerups[i];
			this.fileMap.put(p.getKey(), p.getValue());
		}
				
		//Add UI:
		for(int i=0; i<FileMapper.UI.length; i++){
			Tuple p = FileMapper.UI[i];
			this.fileMap.put(p.getKey(), p.getValue());
		}
	}
}
