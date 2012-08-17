package client.ui;

import java.util.Observable;
import java.util.Observer;

import model.Circle;
import model.ImageLoader;
import model.Message;
import model.Player;

import org.lwjgl.input.Cursor;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import client.control.ClientController;
import client.control.GameController;
import client.control.Status;
import client.model.CanvasDisplay;

public class GamePlayUI extends BasicGameState implements Observer {
	private Image background;
	private Image pic;
	private Image nullPic;
	private Image bgOptions;
	
	private Image upArrow;
	private boolean upOn;
	private Image downArrow;
	private boolean downOn;
	private Image currentPlayer;
	private Image send;
	private boolean sendOn;
	
	private Image soundOn;
	private Image soundOff;
	
	private Cursor cursor;
	private boolean inWhiteboard;
	
	private Image canvas;
	private String clear = "Clear Canvas";
	
	private TextField inputField;

	private int currMsgPos;
	
	private boolean erasing;
	private boolean drawing;
	
	private int mouseX;
	private int mouseY;
	
	private CanvasDisplay cd;
	private Circle circle;
	
	private Status status;
	private ClientController cc;
	private GameController gc;
	
	private int stateID = -1;
	
	public GamePlayUI(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		loadImages();
		setSubImages();
		init();
		
		inputField = new TextField(gc, gc.getDefaultFont(), 666, 425, 155, 28);
		inputField.setAcceptingInput(true);
		inputField.setBorderColor(Color.black);
		inputField.setText("Text here");
	}
	
	private void loadImages() throws SlickException {
		background = ImageLoader.bgGPUI();
		canvas = ImageLoader.canvasGPUI();
		pic = ImageLoader.picGPUI();
		nullPic = ImageLoader.nullGPUI();
		bgOptions = ImageLoader.optionsGPUI();	
	}
	
	private void setSubImages() {
		upArrow = bgOptions.getSubImage(0, 2, 30, 30);
		downArrow = bgOptions.getSubImage(0, 50, 30, 30);
		send = bgOptions.getSubImage(1, 139, 73, 31);
		currentPlayer = bgOptions.getSubImage(31, 0, 149, 138);
		soundOn = bgOptions.getSubImage(78, 140, 35, 31);
		soundOff = bgOptions.getSubImage(115, 140, 35, 31);
		try {
			cursor = CursorLoader.get().getCursor("data/cursor.png", 0, 0);
		} catch (Exception e) {}
	}
	
	private void init() {
		status = Status.getInstance();
		cc = ClientController.getInstance();	
		gc = new GameController();	
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		background.draw(0, 0);
		
		canvas.draw(3, 3);
		
		if (sendOn) send.draw(824, 421);
		if (upOn) upArrow.draw(858, 142);
		else if (downOn) downArrow.draw(857, 369);

		if (status.isBGOn()) soundOn.draw(600, 420);
			else soundOff.draw(600, 420);
		if (inputField.hasFocus()) inputField.render(gc, g);
		for (int i = 0; i < cc.getPlayers().size(); i++)
			if (cc.getPlayer(i).isDrawing())
				currentPlayer.draw(i * 150, 460);

		Color orgColor = g.getColor();
		g.setColor(Color.black);
		for (int i = 0; i < 6; i++) {
			Player player = cc.getPlayer(i);
			if (player != null) {
				pic.draw(26 + i * 150, 470);
				String blank = "  ";
				for (int j = player.getId().length(); j < 9; j+=2)
					blank += " ";
				g.setColor(Color.black);
				g.drawString(blank + player.getId(), 20 + i * 150, 570);
			} else {
				nullPic.draw(26 + i * 150, 480);
			}
		}
		
		g.fillOval(30 - (cd.getCurrRadius() / 2),
				439 - (cd.getCurrRadius() / 2),
				cd.getCurrRadius(),
				cd.getCurrRadius());
		g.drawLine(50, 430, 50, 450);
		g.drawLine(95, 430, 95, 450);
		g.drawLine(215, 430, 215, 450);
		g.drawLine(335, 430, 335, 450);
		g.drawString(cd.getErasure(), 105, 430);
		g.drawString(clear, 220, 430);
		g.setColor(cd.getCurrColor());
		g.fillOval(65, 430, 18, 18);
		g.setColor(Color.white);
		
		for (int i = cc.getMessageSize() - 1 - currMsgPos, j = 0; i >= 0; i--, j++) {
			g.drawString(cc.getMessage(i), 675, 369 - j * 30);
			if (j == 7) break;
		}
		
		int i = 0;
		for (Player p : cc.getPlayers())
			g.drawString((i + 1) + "     " + p.toString(), 680, 10 + i++ * 20);
		
		Graphics canvasGraphics = canvas.getGraphics();
		Color c = (erasing) ? Color.white : cd.getCurrColor();
		canvasGraphics.setColor(c);
	
		if(cd.isDrawer()) {
			if (inWhiteboard) gc.setMouseCursor(cursor, 0, 0);
			else gc.setDefaultMouseCursor();
			if (drawing) {
				canvasGraphics.fillOval(mouseX - 3, mouseY - 3, cd.getCurrRadius(), cd.getCurrRadius());
				cd.draw(c, mouseX, mouseY);
			}
		} else {
			if (circle != null) {
				canvasGraphics.setColor(circle.getColor());
				canvasGraphics.fillOval(
						circle.getX(),
						circle.getY(), 
						circle.getRadius(), circle.getRadius());
			}
		}

		g.setColor(orgColor);
	}

	@Override
	public void update(GameContainer gc , StateBasedGame sbg, int delta)
			throws SlickException {
		if (cd == null) {
			cd = cc.getCanvasDisplay();
			cc.addObserver(this);
		}
		Input input = gc.getInput();
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
		if (cd.isDrawer()) {
			if (mouseY >= 430 && mouseY <= 439 + cd.getCurrRadius()) 
				if (mouseX >= 20 && mouseX <= 30 + (cd.getCurrRadius() / 2) && 
						input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
					cd.radiusChanged();
				else if (mouseX >= 65 && mouseX <= 65 + 18 && 
						input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
					cd.colorChanged();
			
			if (mouseX >= 11 && mouseX <= 649 && mouseY >= 11 && mouseY <= 420) {
				inWhiteboard = true;
				if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) drawing = true;
				else drawing = false;
			} else inWhiteboard = false;

			if (mouseX >= 105 && mouseX <= 210 && mouseY >= 430 && mouseY <= 458 &&
					input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				erasing = !erasing;
				cd.erasingState();
			}
			
			if (mouseX >= 215 && mouseX <= 330 && mouseY >= 430 && mouseY <= 458 &&
					input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				clearCanvas();
			}
				
		}
		
		if (mouseX >= 825 && mouseX <= 895 &&
				mouseY >= 422 && mouseY <= 453) 
			sendOn = true;
		else sendOn = false;
		
		if (sendOn && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
			sendText();
		if (mouseX >= 865 && mouseX <= 880) {
			if (mouseY >= 145 && mouseY <= 169)
				upOn = true;
			else upOn = false;
			if (mouseY >= 370 && mouseY <= 396)
				downOn = true;
			else downOn = false;
		} else {
			upOn = false;
			downOn = false;
		}
		
		if (upOn && input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
			if (cc.getMessageSize() >= 8 && cc.getMessageSize() - currMsgPos - 1 >= 8)
				currMsgPos++;
		if (downOn && input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
			currMsgPos = (currMsgPos == 0) ? 0 : currMsgPos - 1;
		
		if (mouseX >= 600 && mouseX <= 645 && mouseY >= 425 && mouseY <= 458 &&
				input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
			status.switchBg();
		
		if (mouseX >= 666 && mouseX <= 820 && mouseY >= 425 && mouseY <= 455) {
			inputField.setFocus(true); 
			if (input.isKeyPressed(Input.KEY_ENTER)) sendText();
		}
		else if ((mouseX < 666 || mouseX > 820 || mouseY < 425 || mouseY > 455) &&
				input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
			inputField.setFocus(false);
			
	}
	
	private void sendText() {
		if (cd.isDrawer()) return;
		String text = inputField.getText();
		if (text.trim().length() == 0) return;
		ClientController.sending(new Message(Message.TEXT, ClientController.ID + ": " + text));
		inputField.setText("");		
	}
	
	private void clearCanvas() {
		try {
			canvas = ImageLoader.canvasGPUI();
		} catch (Exception e) {}
		cd.clear();
	}	

	@Override
	public int getID() {
		return stateID;
	}

	@Override
	public void update(Observable ob, Object obj) {
		if (obj instanceof Circle) 
			circle = (Circle) obj;
		else 
			clearCanvas();
	}
}