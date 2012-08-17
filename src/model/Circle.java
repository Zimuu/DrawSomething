package model;

import java.io.Serializable;

import org.newdawn.slick.Color;

public class Circle implements Serializable {

	private static final long serialVersionUID = -49706600478214460L;
	
	private final int x;
	private final int y;
	private final int radius;
	private final Color color;
	
	public Circle(int x, int y, int radius, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public Color getColor() {
		return color;
	}
}
