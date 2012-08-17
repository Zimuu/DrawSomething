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
			cursor = CursorLoader.get().getCursor("cursor.png", 0, 0);
		} catch (Exception e) {}
	}
	
	private void init() {
		status = Status.getInstance();
		cc = ClientController.getInstance();
		cd = cc.getCanvasDisplay();
		cc.addObserver(this);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		drawBGImages();
		drawPlayerPanel(g);
		drawBoardItems(gc, g);
		drawChatPanel(g);
		
		drawOnCanvas(gc, g);
	}
	
	private void drawBGImages() {
		background.draw(0, 0);
		canvas.draw(3, 3);
		
		if (sendOn) send.draw(824, 421);
		if (upOn) upArrow.draw(858, 142);
		else if (downOn) downArrow.draw(857, 369);

		if (status.isBGOn()) soundOn.draw(600, 420);
			else soundOff.draw(600, 420);		
	}
	
	private void drawPlayerPanel(Graphics g) {
		g.setColor(Color.black);
		for (int i = 0; i < 6; i++) {
			Player player = cc.getPlayerAt(i);
			if (player != null) {
				pic.draw(26 + i * 150, 470);
				g.drawString(getBlank(player.getId()) + player.getId(), 20 + i * 150, 570);
			} else nullPic.draw(26 + i * 150, 480);
		}
	}
	
	private String getBlank(String id) {
		String blank = "  ";
		for (int j = id.length(); j < 9; j+=2)
			blank += " ";	
		return blank;
	}
	
	private void drawBoardItems(GameContainer gc, Graphics g) {
		if (inputField.hasFocus()) inputField.render(gc, g);
		for (int i = 0; i < cc.size(); i++)
			if (cc.getPlayerAt(i).isDrawing())
				currentPlayer.draw(i * 150, 460);
		
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
	}
	
	private void drawChatPanel(Graphics g) {
		g.setColor(Color.white);
		for (int i = cc.getMessageSize() - 1 - currMsgPos, j = 0; i >= 0; i--, j++) {
			g.drawString(cc.getMessageAt(i), 675, 369 - j * 30);
			if (j == 7) break;
		}
		
		for (int i = 0; i < cc.size(); i++)
			g.drawString((i + 1) + "     " + cc.getPlayerAt(i).toString(), 680, 10 + i * 20);
	}
	
	private void drawOnCanvas(GameContainer gc, Graphics g) throws SlickException {
		Graphics canvasGraphics = canvas.getGraphics();
		Color c = (erasing) ? Color.white : cd.getCurrColor();
		canvasGraphics.setColor(c);
		if(cd.isDrawer()) {
			if (inWhiteboard) gc.setMouseCursor(cursor, 0, 0);
			else gc.setDefaultMouseCursor();
			if (drawing) {
				canvasGraphics.fillOval(mouseX - 3, mouseY - 3, cd.getCurrRadius(), cd.getCurrRadius());
				cd.draw(c, mouseX - 3, mouseY - 3);
			}
		} else {
			if (circle != null) {
				canvasGraphics.setColor(circle.getColor());
				canvasGraphics.fillOval(circle.getX(), circle.getY(), 
						circle.getRadius(), circle.getRadius());
			}
		}

	}

	@Override
	public void update(GameContainer gc , StateBasedGame sbg, int delta)
			throws SlickException {
		Input input = gc.getInput();
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
		
		if (cd.isDrawer()) 
			drawerAction(mouseX, mouseY,input.isMousePressed(Input.MOUSE_LEFT_BUTTON),
					input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON));	
		
		chatPanelAction(mouseX, mouseY, input.isMousePressed(Input.MOUSE_LEFT_BUTTON),
				input.isKeyPressed(Input.KEY_ENTER));
		
	}
	
	private void drawerAction(int mouseX, int mouseY, boolean imp, boolean imbd) {
		if (mouseY >= 430 && mouseY <= 439 + cd.getCurrRadius()) 
			if (mouseX >= 20 && mouseX <= 30 + (cd.getCurrRadius() / 2) && imp) 
				cd.radiusChanged();
			else if (mouseX >= 65 && mouseX <= 65 + 18 && imp) 
				cd.colorChanged();
		
		if (mouseX >= 11 && mouseX <= 649 && mouseY >= 11 && mouseY <= 420) {
			inWhiteboard = true;
			if (imbd) drawing = true;
			else drawing = false;
		} else inWhiteboard = false;

		if (mouseX >= 105 && mouseX <= 210 && mouseY >= 430 && mouseY <= 458 &&	imp) {
			erasing = !erasing;
			cd.erasingState();
		}
		
		if (mouseX >= 215 && mouseX <= 330 && mouseY >= 430 && mouseY <= 458 && imp) 
			clearCanvas();
	}
	
	private void chatPanelAction(int mouseX, int mouseY, boolean imp, boolean ikp) {
		if (mouseX >= 825 && mouseX <= 895 &&
				mouseY >= 422 && mouseY <= 453) 
			sendOn = true;
		else sendOn = false;
		
		if (sendOn && imp) 
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
		
		if (upOn && imp && 
				cc.getMessageSize() >= 8 &&
				cc.getMessageSize() - currMsgPos - 1 >= 8)
			currMsgPos++;
		if (downOn && imp)
			currMsgPos = (currMsgPos == 0) ? 0 : currMsgPos - 1;
		
		if ((mouseX < 666 || mouseX > 820 || mouseY < 425 || mouseY > 455) && imp)
			inputField.setFocus(false);
		
		if (mouseX >= 600 && mouseX <= 645 && mouseY >= 425 && mouseY <= 458 && imp)
			status.switchMusic();
		
		if (mouseX >= 666 && mouseX <= 820 && mouseY >= 425 && mouseY <= 455) {
			inputField.setFocus(true); 
			if (ikp) sendText();
		}
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