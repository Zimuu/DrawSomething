package server.ui;

import model.ImageLoader;
import model.Player;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


import server.control.ServerController;

public class ServerListUI extends BasicGameState {

	private int stateID = 1;
	
	private static final int x = 69;
	private static final int y = 40;
	
	private Image cancel;
	private Image back;
	
	private int selected;
	
	private TextField send;
	
	private boolean display;
	
	private int xPos = 600;
	private int yPos = 650;
	
	private int refresher;
	
	private ServerController sc;
	
	public ServerListUI(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		init();
		setImages();
		send = new TextField(gc, gc.getDefaultFont(), xPos, yPos, 200, 20);		
	}
	
	private void init() {
		sc = ServerController.getInstance();
		selected = -1;
	}
	
	private void setImages() throws SlickException {
		Image options = ImageLoader.optionsServerUI();
		cancel = options.getSubImage(155, 140, 19, 19);
		back = options.getSubImage(0, 0, 30, 30);
		back.rotate(-90);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.setColor(Color.green);
		for (int i = 0, j = 0; i < sc.getPlayers().size(); i++, j++) {
			Player player = sc.getPlayer(i);
			try {
				g.drawString(sc.getIp(player).toString(), x + 325, y + 20 + (j * 80));
				g.drawString(player.display(), x, y + (j * 80));
			} catch (NullPointerException e) {
				j--;
			}
		}

		g.setColor(Color.white);
		back.draw(15, 30);
		
		g.drawString("Send to: " + (selected == -1 ? "all" : sc.getPlayerAt(selected).getId()), 
				xPos, yPos - 25);
		
		send.setLocation(xPos, yPos);
		send.render(gc, g);
		cancel.draw(xPos + 220, yPos - 25);
		showDialog();
	}
	
	private void showDialog() {
		refresher++;
		if (refresher > 5) {
			if (display) 
				if (yPos <= 500) return;
				else yPos--;
			else 
				if(yPos >= 650) return;
				else yPos++;
			refresher = 0;
		}		
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		Input input = gc.getInput();
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();

		if (sc.getPlayerAt(selected) == null) selected = -1;
		if (mouseX >= 680 && mouseX <= 790 && mouseY >= 475 && mouseY <= 495
				&& input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
			selected = (selected == sc.getPlayers().size() - 1) ? -1 : selected + 1;
		
		if (mouseX >= 820 && mouseX <= 842 && mouseY >= 470 && mouseY <= 492
				&& input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			send.setText("");
			display = false;
		}
		if (display && input.isKeyPressed(Input.KEY_ENTER)) 
			sendText();
		if (input.isKeyPressed(Input.KEY_ENTER)) 
			display = true;
		if (input.isKeyPressed(Input.KEY_ESCAPE))
			display = false;
		if (mouseX >= 15 && mouseX <= 45 && mouseY >= 30 && mouseY <= 60 && input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
			sbg.enterState(ServerMainUI.LOGGER);
	}
	
	private void sendText() {
		String text = send.getText();
		if (text.trim().length() == 0) return;
		send.setText("");
		display = false;
		sc.sendTo(selected, text);
	}

	@Override
	public int getID() {
		return stateID;
	}
}