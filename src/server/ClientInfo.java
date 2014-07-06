package server;

import java.net.InetAddress;

public class ClientInfo {
	public InetAddress address;
	public boolean alive;
	public String name;
	public int score;
	public int shipId;
	public int shipType;
	public ClientInfo(InetAddress a) {
		address = a;
		alive = true;
	}
}
