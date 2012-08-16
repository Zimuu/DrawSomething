package server.ui;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import server.control.ServerController;


public class ServerMainUI extends StateBasedGame {

	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;
	
	public static final int LOGGER = 0;
	public static final int LIST = 1;
	
	private static ServerController controller;

	public ServerMainUI(String title) {
		super(title);
		this.addState(new ServerLoggerUI(LOGGER));
		this.addState(new ServerListUI(LIST));
		this.enterState(LOGGER);
	}
	
	@Override
	public boolean closeRequested() {
		controller.stopServer();
		return super.closeRequested();
	}

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		controller = ServerController.getInstance();
		controller.startServer();
		new Thread(controller).start();
		gc.setShowFPS(false);
		this.getState(LOGGER).init(gc, this);
		this.getState(LIST).init(gc, this);
	}
	
	public static void main(String[] args) throws Exception {
		AppGameContainer app = new AppGameContainer(new ServerMainUI("Server"));
		app.setDisplayMode(WIDTH, HEIGHT, false);
		app.setIcon("data/cursor.png");
		app.start();
	}
}
