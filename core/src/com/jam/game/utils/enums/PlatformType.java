package com.jam.game.utils.enums;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jam.game.screens.GameScreen;

public enum PlatformType {
	OIL((TextureRegion) new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 7)), 
	MOVE((TextureRegion) new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 7)), 
	DAMAGE((TextureRegion) new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 7)), 
	NUB((TextureRegion) new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 7)),
	DEFAULT((TextureRegion) new TextureRegion(GameScreen.TEXTURE, 0, 6*32, 32, 7));
	
	private final TextureRegion texture;
	
	PlatformType(TextureRegion texture){
		this.texture = texture;
	}
	
	public TextureRegion getTextureRegion() { return texture; }
}
