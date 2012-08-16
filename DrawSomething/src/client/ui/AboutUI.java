package client.ui;

import model.ImageLoader;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class AboutUI extends BasicGameState {

	private Image background;
	
	private int stateID = 0;
	
	public AboutUI(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		background = ImageLoader.bgAboutUI();
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		background.draw(0, 0);
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		if (gc.getInput().isKeyDown(Input.KEY_B))
			sbg.enterState(MainUI.MENU_STATE);
	}

	@Override
	public int getID() {
		return stateID;
	}

}
