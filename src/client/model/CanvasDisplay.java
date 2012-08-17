package client.model;

import model.Circle;
import model.Message;

import org.newdawn.slick.Color;

import client.control.ClientController;
public class CanvasDisplay {
	
	private boolean drawer;
	
	private Color[] color;
	private int[] radius;
	
	private int x;
	private int y;
	
	private int currColor;
	private int currRadius;
	private String erasure = "Erasure Off";
	
	private static CanvasDisplay instance;
	
	private CanvasDisplay() {
		color = new Color[] {
				Color.red, Color.orange, Color.yellow, Color.green, 
				Color.cyan, Color.blue, Color.magenta, Color.black
		};
		radius = new int[] {
				3, 6, 9, 12, 15, 18
		};
		currColor = 7;
		currRadius = 2;
	}
	
	public static CanvasDisplay newInstance() {
		if (instance == null)
			instance = new CanvasDisplay();
		return instance;
	}
	
	public void radiusChanged() {
		currRadius = (currRadius == radius.length - 1) ?  0 : currRadius + 1;
		ClientController.sending(new Message(Message.RADIUS, null));
	}
	
	public void colorChanged() {
		currColor = (currColor == color.length - 1) ? 0 : currColor + 1;
		ClientController.sending(new Message(Message.COLOR, null));
	}
	
	public void erasingState() {
		if (erasure.equals("Erasure Off"))
			erasure = "Erasure On";
		else 
			erasure = "Erasure Off";
		ClientController.sending(new Message(Message.ERASURE, erasure));
	}
	
	public void draw(Color c, int x, int y) {
		if (x == 0 || y == 0) return;
		if (this.x == x && this.y == y) return;
		this.x = x;
		this.y = y;
		ClientController.sending(new Message(
				Message.IMAGE, new Circle(x, y, getCurrRadius(), c)));
	}
	
	public void clear() {
		ClientController.sending(new Message(Message.CLEAR, null));
	}

	public Color getCurrColor() {
		return color[currColor];
	}

	public int getCurrRadius() {
		return radius[currRadius];
	}
	
	public boolean isDrawer() {
		return drawer;
	}

	public String getErasure() {
		return erasure;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
