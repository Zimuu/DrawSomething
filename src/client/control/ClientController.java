package client.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import client.model.CanvasDisplay;
import client.model.ClientModel;

import model.Circle;
import model.Message;
import model.Player;

public class ClientController extends Observable {

	public static String ID = "anonymous";
	public static String IP = "127.0.0.1";
	public static String PORT = "6060";
	
	private CanvasDisplay cd;
	private ClientModel clientModel;
	
	public static BlockingQueue<Message> receivedMessageQueue;
	public static BlockingQueue<Message> sendingQueue;
	private List<String> messages;
	private Set<Player> players;
	
	private static ClientController instance;
	
	public void start() {
		receivedMessageQueue = new SynchronousQueue<Message>();
		sendingQueue = new SynchronousQueue<Message>();
		messages = new ArrayList<String>();
		players = new TreeSet<Player>();
		cd = CanvasDisplay.newInstance();
		clientModel = new ClientModel();
		new Thread(new MessageReader()).start();
		clientModel.start();
		new Thread(clientModel).start();
	}
	
	public void stop() {
		try {
			receivedMessageQueue.clear();
			sendingQueue.clear();
			clientModel.stop();
		} catch (Exception e) {}
	}
	
	public static ClientController getInstance() {
		if (instance == null)
			instance = new ClientController();
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	private void parseMessage(Message msg) {
		switch (msg.messageType()) {
			case Message.CLEAR:
				setChanged();
				notifyObservers();
				break;
			case Message.LIST:
				players = (TreeSet<Player>) msg.getContent();
				break;
			case Message.COLOR:
				cd.colorChanged();
				break;
			case Message.RADIUS:
				cd.radiusChanged();
				break;
			case Message.IMAGE:
				setChanged();
				notifyObservers((Circle) msg.getContent());
				break;
			default:
				parseText(msg);
		}
	}
	
	private void parseText(Message msg) {
		String text = msg.toString();
		switch(msg.messageType()) {
			case Message.TEXT:
				if (text.length() > 20) {
					String[] ptt = new String[3];
					int i = 0;
					while (text.length() > 20) {
						ptt[i] = text.substring(0, 20);
						text = text.substring(20);
						i++;
					}
					ptt[i] = text;
					for (int j = 0; j < ptt.length; j++)
						if (ptt[j] != null)
						messages.add(ptt[j]);
				} else messages.add(text);
				break;
			case Message.ERASURE:
				cd.erasingState();
				break;
			default:
				messages.add(text);
		}
	}
	
	public static void sending(Message message) {
		if (message == null) return;
		try {
			sendingQueue.put(message);
		} catch (InterruptedException e) {}
	}
	
	public int getMessageSize() {
		return messages.size();
	}
	
	public String getMessage(int index) {
		return messages.get(index);
	}
	
	public Set<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayer(int index) {
		if (index < 0 || index >= players.size())
			return null;
		int i = 0;
		for (Player player : players) 
			if (i == index) return player;
			else i++;
		return null;
	}
	
	public CanvasDisplay getCanvasDisplay() {
		return cd;
	}

	private class MessageReader implements Runnable {
		@Override
		public void run() {
			while (true) {
				try{
					Message msg = receivedMessageQueue.take();
					if (msg == null) continue;
					parseMessage(msg);
				} catch(InterruptedException e) {} 
				catch(Exception e) { break; }
			}
		}
	}
}
