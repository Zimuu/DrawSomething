package client.ui;

import model.ImageLoader;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import client.control.ClientController;
import client.control.Status;

public class MenuUI extends BasicGameState {
	
	private final int menuX = 60;
	private final int menuY = 150;

	private float startScale = 1;
	private float exitScale = 1;
	
	private Image menu;
	private Image orgMenu;
	private Image start;
	private Image exit;
	private Image soundOn;
	private Image soundOff;
	
	private Status status;
		
	private int stateID = 0;
	
	public MenuUI(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		loadImages();
		setSubImages();
		status = Status.getInstance();
	}
	
	private void loadImages() throws SlickException {
		menu = ImageLoader.menuMainUI();
		orgMenu = ImageLoader.orgMenuMainUI();
	}
	
	private void setSubImages() {
		start = orgMenu.getSubImage(0, 2, 220, 100);
		exit = orgMenu.getSubImage(0, 235, 220, 100);
		soundOn = orgMenu.getSubImage(234, 10, 55, 55);
		soundOff = orgMenu.getSubImage(234, 66, 55, 55);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		status.getBg().draw(0, 0);
		start.draw(menuX, menuY, startScale);
		exit.draw(menuX, menuY + 200, exitScale);
		if (!status.isMusicOn())
			soundOff.draw(Status.X, Status.Y);
		else
			soundOn.draw(Status.X, Status.Y);
	}

	@Override
	public void update(GameContainer gc , StateBasedGame sbg, int delta)
			throws SlickException {
		Input input = gc.getInput();
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();
		boolean inStart = false;
		boolean inExit = false;
		
		if (mouseX >= menuX && mouseX <= menuX + start.getWidth()) 
			if (mouseY >= menuY && mouseY <= menuY + start.getHeight())
				inStart = true;
			else if (mouseY >= menuY + 200 && mouseY <= menuY + 200 + start.getHeight())
				inExit = true;
		
		if ((mouseX >= Status.X && mouseX <= Status.X + 55) 
				&& (mouseY >= Status.Y && mouseY <= Status.Y + 55)
				&& (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)))
			status.switchMusic();
		
		
		if (inStart) {
			if (startScale < 1.05f)
				startScale += MainUI.SCALESTEP * delta;
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
				ClientController.getInstance().start();
				sbg.enterState(MainUI.GAME_PLAY_STATE);
			}
		} else if (!inStart && startScale > 1.0f)
			startScale -= MainUI.SCALESTEP * delta;
		
		if (inExit) {
			if (exitScale < 1.05f) 
				exitScale += MainUI.SCALESTEP * delta;
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
				gc.exit();
		} else if (!inExit && exitScale > 1.0f)
			exitScale -= MainUI.SCALESTEP * delta;
			
		if (input.isKeyPressed(Input.KEY_A))
			sbg.enterState(MainUI.ABOUT_STATE);
		else if (input.isKeyPressed(Input.KEY_SPACE)) 
			switchMenuItem();
		else if (input.isKeyPressed(Input.KEY_S))
			sbg.enterState(MainUI.GAME_PLAY_STATE);
		else if (input.isKeyPressed(Input.KEY_X))
			gc.exit();
	}
	
	private void switchMenuItem() {
		if (status.isBGOn()) {
			start = orgMenu.getSubImage(0, 2, 220, 100);
			exit = orgMenu.getSubImage(0, 235, 220, 100);
			soundOn = orgMenu.getSubImage(234, 10, 55, 55);
			soundOff = orgMenu.getSubImage(234, 66, 55, 55);
		} else {
			start = menu.getSubImage(5, 0, 190, 65);
			exit = menu.getSubImage(5, 170, 190, 65);
			soundOn = orgMenu.getSubImage(291, 14, 52, 55);
			soundOff = orgMenu.getSubImage(291, 70, 52, 52);
		}
		status.switchBg();
	}

	@Override
	public int getID() {
		return stateID;
	}

}
