package server;

import messages.Message;

public class DirectedMessage {
	public Message message;
	public ClientInfo client;
	public DirectedMessage(Message m, ClientInfo c) {
		message = m;
		client = c;
	}
}
