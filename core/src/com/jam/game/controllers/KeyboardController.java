package com.jam.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class KeyboardController {
	// temp-ish
	public boolean left = false;
	public boolean right = false;
	public boolean jump = false;

	public void checkInput() {
		jump = Gdx.input.isKeyJustPressed(Keys.W)   || Gdx.input.isKeyJustPressed(Keys.SPACE);

		left = Gdx.input.isKeyPressed(Keys.A);

		right = Gdx.input.isKeyPressed(Keys.D);
	}
}
