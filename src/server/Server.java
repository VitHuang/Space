package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.Timer;

import shared.*;
import messages.*;

public class Server implements ActionListener {

	private World world;
	private ArrayList<ClientInfo> clients;
	private SafeMessageQueue<DirectedMessage> outMessages;
	private Timer timer;
	private int timerCount;

	public Server(int p) throws SocketException {
		world = new World(this);
		clients = new ArrayList<ClientInfo>();
		outMessages = new SafeMessageQueue<DirectedMessage>();
		world.addAsteroids(World.MAX_ASTEROIDS);
		timer = new Timer(250, this);
		final int port = p;
		final DatagramSocket sock = new DatagramSocket(port);
		try {
			System.out.println("Starting server at address " + InetAddress.getLocalHost() + ", port " + port);
		} catch (UnknownHostException e1) {
			System.out.println("Could not determine host - use a tool like ifconfig to check");
		}
		Thread inputThread = new Thread() {
			@Override
			public void run() {
				byte[] buffer = new byte[100000];
				ByteArrayInputStream bais;
				ObjectInputStream ois;
				while (true) {
					try {
						DatagramPacket packet = new DatagramPacket(buffer,
								buffer.length);
						sock.receive(packet);
						bais = new ByteArrayInputStream(buffer, 0,
								packet.getLength());
						ois = new ObjectInputStream(bais);
						Message msg = (Message) ois.readObject();
						handleMessage(msg, packet.getAddress());
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
					DirectedMessage msg = outMessages.take();
					if (clients.size() > 0) {
						try {
							baos = new ByteArrayOutputStream();
							oos = new ObjectOutputStream(baos);
							oos.writeObject(msg.message);
							byte[] data = baos.toByteArray();
							int length = data.length;
							DatagramPacket packet = new DatagramPacket(data,
									length, null, port + 1);
							if (msg.client == null) {
								synchronized (clients) {
									for (ClientInfo client : clients) {
										packet.setAddress(client.address);
										sock.send(packet);
										// System.out.println("Sending " +
										// msg.message + " to " +
										// client.address);
									}
								}
							} else {
								packet.setAddress(msg.client.address);
								sock.send(packet);
								// System.out.println("Sending " + msg.message +
								// " to " + msg.client.address);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		inputThread.start();
		outputThread.start();
		timer.start();
	}

	public void handleMessage(Message msg, InetAddress clientAddress) {
		// System.out.println("Received message " + msg + " from address " +
		// clientAddress);
		ClientInfo client = null;
		synchronized (clients) {
			for (ClientInfo c : clients) {
				if (c.address.equals(clientAddress)) {
					client = c;
					break;
				}
			}
		}
		if (client == null) {
			if (msg instanceof ClientConnectedMessage) {
				System.out.println(((ClientConnectedMessage) msg).name + " connected from " + clientAddress);
				handleClientConnectedMessage((ClientConnectedMessage) msg,
						new ClientInfo(clientAddress));
			}
			return;
		}
		if (msg instanceof ClientConnectedMessage) {
			disconnectClient(client);
			handleClientConnectedMessage((ClientConnectedMessage) msg, client);
		} else if (msg instanceof ClientActionMessage) {
			handleClientActionMessage((ClientActionMessage) msg, client);
		} else if (msg instanceof ClientAliveMessage) {
			client.alive = true;
		}
	}

	public void handleClientConnectedMessage(ClientConnectedMessage msg,
			ClientInfo client) {
		// System.out.println("Handling client connection");
		client.name = msg.name;
		client.shipType = msg.shipType;
		client.shipId = world.addNewPlayer(msg.name, msg.shipType);
		clients.add(client);
		sendMessage(new SetPlayerShipMessage(client.shipId), client);
		sendAllObjects(client);
	}

	public void handleClientActionMessage(ClientActionMessage msg,
			ClientInfo client) {
		// System.out.println("Handling client action message " + msg);
		Spaceship ship = world.getShip(client.shipId);
		if (ship != null) {
			switch (msg.action) {
			case START_ACCELERATING:
				ship.startAccelerating();
				break;
			case STOP_ACCELERATING:
				ship.stopAccelerating();
				break;
			case START_ROTATING_CLOCKWISE:
				ship.startRotatingClockwise();
				break;
			case START_ROTATING_ANTICLOCKWISE:
				ship.startRotatingAnticlockwise();
				break;
			case STOP_ROTATING:
				ship.stopRotating();
				break;
			case START_FIRING:
				ship.startFiring();
				break;
			case STOP_FIRING:
				ship.stopFiring();
				break;
			}
		}
		sendObject(ship, null);
	}

	public void disconnectClient(ClientInfo client) {
		world.removeObject(client.shipId);
		clients.remove(client);
	}

	public void sendMessage(Message msg, ClientInfo client) {
		// System.out.println("Sending message " + msg + " to " + client);
		outMessages.put(new DirectedMessage(msg, client));
	}

	public void sendObject(PhysicalObject obj, ClientInfo client) {
		if (obj == null) {
			return;
		}
		sendMessage(new ObjectStatusMessage(obj), client);
	}

	public void sendRemove(int id) {
		sendMessage(new RemoveObjectMessage(id), null);
	}

	public void sendCollision(PhysicalObject a, PhysicalObject b) {
		sendMessage(new CollisionMessage(a, b), null);
	}

	public void sendExplosion(PhysicalObject o) {
		sendMessage(new ExplosionMessage(o), null);
	}

	public void sendPowerup(Powerup p) {
		sendMessage(new PowerupMessage(p), null);
	}

	public ClientInfo findClientByShip(Spaceship ship) {
		synchronized (clients) {
			for (ClientInfo client : clients) {
				if (client.shipId == ship.id) {
					return client;
				}
			}
		}
		return null;
	}

	public ClientInfo findClientById(int id) {
		synchronized (clients) {
			for (ClientInfo client : clients) {
				//System.out.println("Searching for " + id + ", found " + client.shipId);
				if (client.shipId == id) {
					return client;
				}
			}
		}
		return null;
	}
	
	public void sendScore(ClientInfo client) {
		sendMessage(new ScoreMessage(client.score), client);
	}
	
	public void setScore(int score, int clientId) {
		ClientInfo client = findClientById(clientId);
		client.score = score;
		sendScore(client);
	}
	
	public void addScore(int score, int clientId) {
		ClientInfo client = findClientById(clientId);
		if (client != null) {
			client.score += score;
			sendScore(client);
		}
	}
	
	public void sendLiveness(ClientInfo client) {
		ArrayList<Integer> ids = world.getLiveIds();
		sendMessage(new LivenessMessage(ids), client);
	}

	public void playerDestroyed(Spaceship ship, PhysicalObject other) {
		final ClientInfo client = findClientByShip(ship);
		ClientInfo otherClient = null;
		if (other instanceof Spaceship) {
			otherClient = findClientByShip((Spaceship) other);
		} else if (other instanceof Bullet) {
			otherClient = findClientByShip(world
					.getShip(((Bullet) other).owner));
		}
		if (otherClient != null) {
			addScore(client.score / 4, other.id);
		}
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					// continue
				}
				client.score = 0;
				sendMessage(new ScoreMessage(client.score), client);
				client.shipId = world
						.addNewPlayer(client.name, client.shipType);
				sendMessage(new SetPlayerShipMessage(client.shipId), client);
			}
		};
		t.start();
	}

	public void sendAllObjects(ClientInfo client) {
		sendLiveness(client);
		for (int i = 0; i < world.maxObjects(); i++) {
			PhysicalObject object = world.getObject(i);
			// System.out.println("World object " + i + ": " + object);
			if (object != null) {
				sendObject(object, client);
			}
		}
	}

	public void sendPlayerShips() {
		synchronized (clients) {
			for (ClientInfo c : clients) {
				sendObject(world.getObject(c.shipId), c);
			}
		}
	}

	public static void main(String[] args) {
		try {
			new Server(Integer.parseInt(args[0]));
		} catch (NumberFormatException e) {
			System.err.println("Usage: server <port> <numAsteroids>");
			e.printStackTrace();
			return;
		} catch (SocketException e) {
			System.err.println("Failed to open socket "
					+ Integer.parseInt(args[0]));
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ((timerCount % 8) == 0) {
			sendAllObjects(null);
		}
		if (timerCount >= 40) {
			int i = 0;
			synchronized (clients) {
				while (i < clients.size()) {
					ClientInfo c = clients.get(i);
					if (!c.alive) {
						System.out.println("Disconnecting " + c.name + " due to inactivity");
						disconnectClient(c);
					} else {
						c.alive = false;
						i++;
					}
				}
			}
			timerCount = 0;
		} else {
			sendPlayerShips();
		}
		timerCount++;
	}

}
