package server.model;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.newdawn.slick.Color;

public class Log {

	public static final int INFO = 0;
	public static final int WARNING = 1;
	public static final int SYSTEM = 2;
	
	private int level;
	private String text;
	private Date date;
	
	public Log(int level, String text) {
		this.level = level;
		this.text = text;
		this.date = new Date(System.currentTimeMillis());
	}
	
	public Color level() {
		switch (level) {
			case 1:
				return Color.red;
			case 2:
				return Color.green;
			default :
				return Color.white;
		}
	}
	
	@Override
	public String toString() {
		String dat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		String lvl = "[" + ((level == 1) ? "Warning" : ((level == 2) ? "System" : "Message")) + "]";
		String txt = (text.length() > 80) ? text.substring(0, 80) + "\r\n" + text.substring(80) : text;
		return dat + "\r\n" + lvl +  txt;
	}
}
