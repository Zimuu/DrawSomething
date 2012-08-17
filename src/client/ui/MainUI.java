package client.ui;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import client.control.ClientController;

public class MainUI extends StateBasedGame {

	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;
	public static final String TITLE = "Draw Something";
	public static final float SCALESTEP = 0.0001f;

	public static final int MENU_STATE = 0;
	public static final int GAME_PLAY_STATE = 1;
	public static final int ABOUT_STATE = 2;
	
	private static ClientController clientController;
	
	private static MainUI instance;
	
	//tester
	public MainUI(String id) {
		super(TITLE);
		this.addState(new MenuUI(MENU_STATE));
		this.addState(new GamePlayUI(GAME_PLAY_STATE));
		this.addState(new AboutUI(ABOUT_STATE));
		this.enterState(MENU_STATE);
	}

	private MainUI() {
		super(TITLE);
		this.addState(new MenuUI(MENU_STATE));
		this.addState(new GamePlayUI(GAME_PLAY_STATE));
		this.addState(new AboutUI(ABOUT_STATE));
		this.enterState(MENU_STATE);
	}
	
	public static MainUI getInstance() {
		if (instance == null)
			instance = new MainUI();
		return instance;
	}

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		clientController = ClientController.getInstance();
		this.getState(MENU_STATE).init(gc, this);
		this.getState(ABOUT_STATE).init(gc, this);
		this.getState(GAME_PLAY_STATE).init(gc, this);
	}

	@Override
	public boolean closeRequested() {
		clientController.stop();
		return super.closeRequested();
	}

	public static void main(String[] args) throws Exception {
		AppGameContainer app = new AppGameContainer(MainUI.getInstance());
		app.setIcon(MainUI.getInstance().getClass().getResource("cursor.png").toString().substring(5));
		app.setDisplayMode(WIDTH, HEIGHT, false);
		app.start();
	}
}
