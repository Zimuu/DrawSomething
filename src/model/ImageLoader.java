package model;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import server.ui.ServerListUI;

import client.control.Status;
import client.ui.AboutUI;
import client.ui.GamePlayUI;
import client.ui.MenuUI;

public class ImageLoader {
	
	public static Image bgMenuUI() throws SlickException {
		return loadImage(Status.class, "background.jpg");
	}
	
	public static Image orgBGMenuUI() throws SlickException {
		return loadImage(Status.class, "orgBackground.jpg");
	}
	
	public static Image menuMainUI() throws SlickException {
		return loadImage(MenuUI.class, "menu.gif");
	}
	
	public static Image orgMenuMainUI() throws SlickException {
		return loadImage(MenuUI.class, "menu.jpg");
	}
	
	public static Image bgAboutUI() throws SlickException {
		return loadImage(AboutUI.class, "about.jpg");
	}
	
	public static Image bgGPUI() throws SlickException {
		return loadImage(GamePlayUI.class, "whiteboard.jpg");
	}
	
	public static Image canvasGPUI() throws SlickException {
		return loadImage(GamePlayUI.class, "canvas.jpg");
	}
	
	public static Image picGPUI() throws SlickException {
		return loadImage(GamePlayUI.class, "picture.jpg");
	}
	
	public static Image nullGPUI() throws SlickException {
		return loadImage(GamePlayUI.class, "nullpicture.jpg");
	}
	
	public static Image optionsGPUI() throws SlickException {
		return loadImage(GamePlayUI.class, "gameoptions.jpg");
	}
	
	public static Image optionsServerUI() throws SlickException {
		return loadImage(ServerListUI.class, "gameoptions.jpg");
	}
	
	@SuppressWarnings("rawtypes")
	private static Image loadImage(Class c, String url) throws SlickException {
		return new Image(c.getResource(url).toString().substring(5));
	}
}
