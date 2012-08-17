package model;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class Player implements Comparable<Player>, Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private int score;
	private boolean isDrawing;
	private Date date;
	
	public Player(String id) {
		this.id = id;
		this.score = 0;
		this.date = new Date(System.currentTimeMillis());
	}
	
	public void add(int score) {
		this.score += score;
	}
	
	public void draw() {
		isDrawing = true;
	}
	
	public void finished() {
		isDrawing = false;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getId() {
		return id;
	}
	
	public String display() {
		String dat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		return toString() + "\r\njoined: " + dat + "      ip:";
	}
	
	public boolean isDrawing() {
		return isDrawing;
	}
	
	@Override
	public String toString() {
		String blank = "      ";
		for (int i = id.length(); i < 9; i++)
			blank += " ";
		return id + blank + score;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player))
			return false;
		return ((Player) obj).id.equals(id);
	}
	
	@Override
	public int compareTo(Player player) {
		if (player == null)
			return 0;
		return (player.score > score) ? 1 : -1;
	}
		
}
