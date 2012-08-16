package client.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import client.control.ClientController;

import model.Message;

public class ClientModel implements Runnable {

	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	@Override
	public void run() {
		while (socket != null && !socket.isClosed()) {
			try {
				Message msg = (Message) ois.readObject();
				if (msg == null) continue;
				else 
					try {
						ClientController.receivedMessageQueue.put(msg);
					} catch (Exception e) {}
			} catch (SocketException e) {
				stop();
			} catch (Exception e) {}
		}
	}	
	
	public void start() {
		try {
			socket = new Socket(ClientController.IP, Integer.parseInt(ClientController.PORT));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(new Message(Message.PLAYER_JOINED, ClientController.ID));
			ois = new ObjectInputStream(socket.getInputStream());
			oos.flush();
			new Thread(new MessageSender()).start();
		} catch (Exception e) {}
	}
	
	public void stop() {
		try {
			if (ois != null)
				ois.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {}
		finally {
			oos = null;
			ois = null;
			socket = null;
		}
	}
	
	public void send(Message msg) throws Exception {
		oos.writeObject(msg);
		oos.flush();
	}
	
	private class MessageSender implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Message message = ClientController.sendingQueue.take();
					send(message);
				} catch (Exception e) {}
			}
		}
	}
}
