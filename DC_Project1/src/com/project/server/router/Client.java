package com.project.server.router;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client {

	private String username;

	private String message;

	private String currentIP;

	private String hostname;

	private int currentPort;

	public Client() {
		username = "NULL";
		hostname = "NULL";
		currentIP = "NULL";
		message = "NULL";
		currentPort = -1;
	}

	public String getUsername() {
		return username;
	}

	public void setPort(final int port) {
		currentPort = port;
	}

	public int getCurrentPort() {
		return currentPort;
	}

	public void addStringMessage(String mMessage) {
		message = mMessage;
	}

	public String getStringMessage() {
		return message;
	}

	public boolean hasMessage() {
		return ((message != null) && (message.length() > 0));
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public void setCurrentIP(final String ip) {
		this.currentIP = ip;
	}

	public void setUsername(final String userName) {
		username = userName;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(final String hostName) {
		hostname = hostName;
	}

	public byte[] toBytes() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream out;

		try {
			out = new ObjectOutputStream(os);
			out.writeUTF(getUsername());
			out.writeUTF(getHostname());
			out.writeUTF(getCurrentIP());
			out.writeUTF(getStringMessage());
			out.writeInt(getCurrentPort());
			out.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final byte[] res = os.toByteArray();

		return res;
	}
	
	public static Client fromBytes(final byte[] byteArray) {
		Client client = new Client();
		ByteArrayInputStream is;
		ObjectInputStream in;

		try {
			is = new ByteArrayInputStream(byteArray);
			in = new ObjectInputStream(is);
			
			client.setUsername(in.readUTF());
			client.setHostname(in.readUTF());
			client.setCurrentIP(in.readUTF());
			client.addStringMessage(in.readUTF());
			client.setPort(in.readInt());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return client;
	}
}
