package controllers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class KeyboardController {
	//temp-ish
	public boolean left = false;
	public boolean right = false;
	public boolean jump = false;
	
	public void checkInput() {
		jump = Gdx.input.isKeyPressed(Keys.W);
		
		left = Gdx.input.isKeyPressed(Keys.A);
		
		right = Gdx.input.isKeyPressed(Keys.D);
		
		//temp
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}
}
