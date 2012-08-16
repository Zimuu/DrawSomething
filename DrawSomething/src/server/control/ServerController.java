package server.control;

import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import server.model.Logger;
import server.model.ServerConnector;

import model.Message;
import model.Player;

public class ServerController extends Observable implements Runnable, Observer {

	private final int HOST_PORT = 6060;
	
	private ServerSocket server;
	
	private Logger logger;
	private List<ServerConnector> connectors;
	private Set<Player> players;
	private boolean running;
	
	private int currPos;
	
	private BlockingQueue<Message> messages;
	
	private static ServerController instance;
	
	private ServerController() {}
	
	public void startServer() {
		logger = Logger.getInstance();
		logger.addObserver(this);
		try {
			server = new ServerSocket(HOST_PORT);
			logger.system("Server started on port: " + server.getLocalPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
		connectors = new ArrayList<ServerConnector>();
		players = new TreeSet<Player>();
		running = true;
		messages = new SynchronousQueue<Message>();
		new Thread(new Sender()).start();
		new Thread(new Cleaner()).start();
	}
	
	public static ServerController getInstance() {
		if (instance == null)
			instance = new ServerController();
		return instance;
	}
	
	@Override
	public void run() {
		try {
			while (running)
				acceptNewConnection();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (server != null) server.close();
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}
	
	private void acceptNewConnection() throws Exception {
		ServerConnector ss = new ServerConnector(this, server.accept());
		new Thread(ss).start();
		//ss.addObserver(this);
		connectors.add(ss);			
	}
	
	@Override
	public void update(Observable o, Object obj) {
		if (obj instanceof Integer)
			if (logger.size() > 8 && logger.size() - currPos >= 8) currPos++;
		if (obj instanceof Message)
			try {
				messages.put((Message) obj);
			} catch (InterruptedException e) {}
	}
	
	public void put(Message msg) {
		if (msg == null) return;
		try {
			messages.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public void removeClient(String id) {
		Iterator<ServerConnector> cit = connectors.iterator();
		while (cit.hasNext()) {
			ServerConnector sc = cit.next();
			if (sc.getId().equals(id)) {
				removeClient(sc);
				break;
			}
		}
	}
	
	private void removeClient(ServerConnector sc) {
		Iterator<Player> pit = players.iterator();
		while (pit.hasNext())  {
			Player player = pit.next();
			if (player.getId().equals(sc.getId())) {
				logger.system(player.getId() + " has left.");
				pit.remove();
			}
		}
		sc.stopConnection();
		Iterator<ServerConnector> cit = connectors.iterator();
		while (cit.hasNext())
			if (cit.next().getId().equals(sc.getId()))
				cit.remove();
		updateList();
	}
	
	public void updateList() {
		put(new Message(Message.LIST, players));
	}
	
	public void sendTo(int pos, String text) {
		Player player = getPlayerAt(pos);
		Message msg = new Message(Message.SERVER_MESSAGE, "Server Message: " + text);
		if (player == null) 
			put(msg);
		
		else {
			for (ServerConnector sc : connectors)
				if (sc.getId().equals(player.getId())) {
					try {
						sc.send(msg);
					} catch (Exception e) {
						logger.warning(e.getMessage());
						return;
					}
				}
		}
		logger.system(msg.toString()); 
	}
	
	public void stopServer() {
		try {
			for (ServerConnector sc : connectors) 
				sc.stopConnection();
			server.close();
		} catch (Exception e) {}
		finally {
			logger.system("Server exit on request");
			logger.save();
			running = false;
		}
	}
	
	public SocketAddress getIp(Player player) {
		for (ServerConnector sc : connectors) 
			if (sc.getId().equals(player.getId())) {
				try {
					return sc.getAddress();
				} catch (NullPointerException e) {
					removeClient(sc);
					return null;
				}
			}
		return null;
	}
	
	public Player getPlayer(int index) {
		int i = 0;
		for (Player player : players) {
			if (index == i) return player;
			i++;
		}
		return null;
	}
	
	public List<ServerConnector> getSocketClients() {
		return connectors;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public Set<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayerAt(int pos) {
		int i = 0;
		for (Player player : players) {
			if (i == pos) return player;
			else i++;
		}
		return null;
	}
	
	public void setPos(int pos) {
		currPos = pos;
	}
	
	public void posChange(int value) {
		currPos += value;
	}
	
	public int getPos() {
		return currPos;
	}
	
	private class Sender implements Runnable {
		@Override
		public void run() {
			while (true) {
				try{
					Message msg = messages.take();
					sendAll(msg);
				} catch(InterruptedException e) {
					logger.warning(e.getMessage());
				} catch(Exception e) {
					break;
				}
			}
		}
		
		private void sendAll(Message msg) {
			Iterator<ServerConnector> cit = connectors.iterator();
			while (cit.hasNext()) {
				ServerConnector sc = cit.next();
				try {
					sc.send(msg);
				} catch (Exception e) { 
					if (msg != null) removeClient(sc);
				}
			}		
		}
	}
	
	private class Cleaner implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(10 * 1000);
					cleanUp();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					logger.info(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		private void cleanUp() {
			Iterator<ServerConnector> cit = connectors.iterator();
			while (cit.hasNext()) {
				ServerConnector sc = cit.next();
				if (!sc.isConnecting()) 
					removeClient(sc);
			}			
		}
	}
	
}
