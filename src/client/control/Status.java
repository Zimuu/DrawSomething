package client.control;

import model.ImageLoader;

import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

public class Status {

	//sound icon position
	public static final int X = 830;
	public static final int Y = 20;
	
	private Image bgImage;
	private Image bg;
	private Image orgbg;
	private Sound bgMusic;
	private boolean music;
	
	private static Status instance;
	
	private Status() {
		try {
			bgMusic = new Sound(getClass().getResource("huanqin.ogg"));
			bgMusic.loop();
			music = true;
			orgbg = ImageLoader.orgBGMenuUI();
			bg = ImageLoader.bgMenuUI();
			bgImage = orgbg;
		} catch (Exception e) {}
	}
	
	public static Status getInstance() {
		if (instance == null)
			instance = new Status();
		return instance;
	}
	
	public boolean isMusicOn() {
		return music;
	}
	
	public boolean isBGOn() {
		return bgImage == bg;
	}
	
	public void switchBg() {
		bgImage = isBGOn() ? orgbg : bg;
	}
	
	public void switchMusic() {
		music = !music;
		if (music)
			bgMusic.loop();
		else
			bgMusic.stop();		
	}
	
	public Image getBg() {
		return bgImage;
	}
}
