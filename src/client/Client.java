package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.Timer;

import messages.ClientActionMessage;
import messages.ClientAliveMessage;
import messages.ClientConnectedMessage;
import messages.Message;
import shared.SafeMessageQueue;
import shared.Spaceship;

public class Client implements ActionListener {

	private SafeMessageQueue<Message> outMessages;
	private String host;
	private int port;

	private MessageHandler messageHandler;
	private Timer timer;
	
	public Client(String h, int p) throws SocketException {
		outMessages = new SafeMessageQueue<Message>();
		timer = new Timer(10000, this);
		port = p;
		host = h;
		final DatagramSocket sock = new DatagramSocket(port + 1);
		Thread inputThread = new Thread() {
			@Override
			public void run() {
				byte[] buffer = new byte[100000];
				ByteArrayInputStream bais;
				ObjectInputStream ois;
				while (true) {
					try {
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						sock.receive(packet);
						bais = new ByteArrayInputStream(buffer, 0, packet.getLength());
						ois = new ObjectInputStream(bais);
						Message msg = (Message) ois.readObject();
						messageHandler.handleMessage(msg);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread outputThread = new Thread() {
			@Override
			public void run() {
				ByteArrayOutputStream baos;
				ObjectOutputStream oos;
				while (true) {
					Message msg = outMessages.take();
					try {
						baos = new ByteArrayOutputStream();
						oos = new ObjectOutputStream(baos);
						oos.writeObject(msg);
						byte[] data = baos.toByteArray();
						int length = data.length;
						DatagramPacket packet = new DatagramPacket(data, length, InetAddress.getByName(host), port);
						sock.send(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		inputThread.setDaemon(true);
		outputThread.setDaemon(true);
		inputThread.start();
		outputThread.start();
		timer.start();
	}
	
	public void sendMessage(Message m) {
		outMessages.put(m);
	}
	
	public void sendAction(Spaceship.Action a) {
		sendMessage(new ClientActionMessage(a));
	}
	
	public void sendConnectedMessage(String n, int t) {
		sendMessage(new ClientConnectedMessage(n, t));
	}
	
	public void setMessageHandler(MessageHandler h) {
		messageHandler = h;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		sendMessage(new ClientAliveMessage());
	}
}
