package com.project.server.router;

public class Server {

	private String IP;

	private String message;
	
	private String hostName;

	private int PORT;

	public Server(final String ipAddress, final String serverName, final int port) {
		hostName = serverName;
		IP = ipAddress;
		PORT = port;
	}

	public String getIP() {
		return IP;
	}
	
	public void setHostname(final String hostName) {
		this.hostName = hostName;
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
	
	public int getPort() {
		return PORT;
	}
	
	public String getHostname() {
		return hostName;
	}
}