package com.project.server.router;

public class Client {
	
	private final String ID;
	
	private String message;
	
	private String IP;
	
	private int PORT;
	
	private int messagePort;

	public Client(final String name, final int port) {
		ID = name;
		PORT = port;
	}
	
	public String getId() {
		return ID;
	}
	
	public void setPort(final int port) {
		PORT = port;
	}

	public void addStringMessage(String mMessage) {
		message = mMessage;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean hasMessage() {
		return (message.length() > 0);
	}
	
	public String getIP() {
		return IP;
	}
	
	public int getMessagePort() {
		return messagePort;
	}
}
