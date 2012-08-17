package model;

import java.io.Serializable;
import java.util.TreeSet;


public class Message implements Serializable {

	private static final long serialVersionUID = 4464740995688682572L;
	
	public static final int IMAGE = 0;
	public static final int LIST = 1;
	public static final int TEXT = 2;
	public static final int SYSTEM = 3;
	public static final int SERVER_MESSAGE = 4;
	public static final int PLAYER_JOINED = 5;
	public static final int ERASURE = 6;
	public static final int COLOR = 7;
	public static final int RADIUS = 8;
	public static final int CLEAR = 10;
	
	private final int type;
	
	private Object content;
	
	@SuppressWarnings("unchecked")
	public Message(int type, Object content) {
		this.type = type;
		switch (type) {
			case COLOR:
			case RADIUS:
				this.content = (Integer) content;
				break;
			case IMAGE:
				this.content = (Circle) content;
				break;
			case LIST:
				this.content = new TreeSet<Player>();
				((TreeSet<Player>) this.content).addAll((TreeSet<Player>) content);
				break;
			case CLEAR:
				break;
			default :
				this.content = (String) content;
		}
	}
	
	public int messageType() {
		return type;
	}
	
	public Object getContent() {
		return content;
	}
	
	@Override
	public String toString() {
		if (content instanceof String)
			return (String) content;
		return null;
	}
	
	public int getInt() {
		if (content instanceof Integer)
			return (Integer) content;
		return -1;
	}
	
	public boolean draw() {
		if (content instanceof Boolean)
			return (Boolean) content;
		return false;
	}

}
