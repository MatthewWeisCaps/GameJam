package com.jam.game.utils;

public class FileMapper {
	
	public static final Tuple[] sounds = {
			//new Tuple("title_scroll_music", "sounds/title_screen.mp3"),
			new Tuple("title_music", "sounds/title_music.mp3"),
			new Tuple("start_sound", "sounds/start_sound.mp3"),
			new Tuple("gameplay_music", "sounds/gameplay_music.mp3"),
			new Tuple("death_sound", "sounds/death_sound.mp3"),
			new Tuple("death_music", "sounds/death_music.mp3")
	};
	
	public static final Tuple[] screens = {
			new Tuple("intro_scroll", "screens/intro_scroll.png"),
			new Tuple("title_screen", "screens/title_screen.png"),
			new Tuple("after_press_start", "screens/after_press_start.png"),
			new Tuple("game_over", "screens/game_over.png")
	};
	
	public static final Tuple[] powerups = {
			new Tuple("powerups", "powerup/powerups_ALL.png")
	};
	
	public static final Tuple[] platforms = {
			new Tuple("platforms", "platform/platforms_ALL.png")
	};
	
	public static final Tuple[] UI = {
			new Tuple("numbers", "UI/numbers.png")
	};
	
}
