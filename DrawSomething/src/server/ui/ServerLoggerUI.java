package server.ui;


import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import server.control.ServerController;
import server.model.Log;
import server.model.Logger;

public class ServerLoggerUI extends BasicGameState {

	private Image back;
	
	private int stateID = 0;
	
	private static final int x = 10;
	private static final int y = 10;
	
	private ServerController sc;
	
	private Logger logger;
	
	public ServerLoggerUI(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		back = new Image(
					getClass().getResource("data/gameoptions.jpg").toString().substring(5)).
					getSubImage(0, 0, 30, 30);
		back.rotate(90);
		sc = ServerController.getInstance();
		logger = Logger.getInstance();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.setColor(Color.white);
		back.draw(860, 30);
		for (int i = sc.getPos(), j = 0; j <= 7 && i < logger.size(); i++, j++) {
			Log log = logger.getLogAt(i);
			g.setColor(log.level());
			g.drawString(log.toString(), x, y + (j * 75));
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		Input input = gc.getInput();
		int x = input.getMouseX();
		int y = input.getMouseY();
		if (x >= 860 && x <= 890 && y >= 30 && y <= 60 && input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
			sbg.enterState(ServerMainUI.LIST);
	}
	
	@Override
	public void mouseWheelMoved(int value) {
		int movement = - value / 120;
		int size = logger.size();
		int pos = sc.getPos();
		
		if (movement < 0 && pos > 0)
			movement = 0 - movement;
		sc.posChange(movement);
		if (size - pos < 8) 
			sc.setPos(size - 8);
		if (pos < 0) sc.setPos(0);
	}

	@Override
	public int getID() {
		return stateID;
	}
}