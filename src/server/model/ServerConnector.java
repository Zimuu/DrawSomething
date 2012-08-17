package server.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import model.Message;
import model.Player;

import server.control.ServerController;

public class ServerConnector implements Runnable {

	private Socket socket;
	private String id;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private static ServerController controller;
	
	private static Logger logger;
	
	public ServerConnector(ServerController sc,Socket socket) {
		if (controller == null) controller = sc;
		logger = controller.getLogger();
		this.socket = socket;
		try {
			this.ois = new ObjectInputStream(socket.getInputStream());
			this.oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {}
	}
	
	@Override
	public void run() {
		try {
			while (socket.isConnected()) {
				Object obj = ois.readObject();
				if (obj == null) continue;
				
				Message msg = (Message) obj;
				Message newMsg = null;
				switch (msg.messageType()) {
					case Message.PLAYER_JOINED:
						if (id == null) 
							newMsg = createId(msg);
						break;
					case Message.TEXT:
						logger.info(msg.toString());
						newMsg = msg;
						break;
					default :
						newMsg = msg;
				}
				controller.put(newMsg);
			}
		} catch (SocketException e) { 
			controller.removeClient(id);
		} catch (NullPointerException e) {
			controller.removeClient(id);
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		finally {
			stopConnection();
		}
	}
	
	public void send(Message message) {
		
	}
	
	
	private Message createId(Message msg) {
		id = msg.toString();
		logger.system(id + " has joined.");
		controller.addPlayer(new Player(id));
		controller.updateList();
		return new Message(Message.SYSTEM, id + " has joined.");
	}
	
	public boolean isConnecting() {
		return socket.isConnected() ||
				!socket.isClosed() ||
				!socket.isOutputShutdown();
	}
	
	public void stopConnection() {
		try {
			if (ois != null) ois.close();
			if (oos != null) oos.close();
			if (socket != null) socket.close();
		} catch (Exception e) {} 
		finally {
			ois = null;
			oos = null;
			socket = null;
		}
		
	}
	
	public SocketAddress getAddress() {
		return socket.getRemoteSocketAddress();
	}
	
	public String getId() {
		return id;
	}
}
