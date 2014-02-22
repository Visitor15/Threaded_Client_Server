package com.project.server.router;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.DCServer.NODE_TYPE;

public class Node {

	public NODE_TYPE NODE;

	public COMMAND_TYPE COMMAND;

	public String username;

	public String message;

	public String currentIP;

	public String hostname;

	public int currentPort;

	Node() {
		username = "NULL";
		hostname = "NULL";
		currentIP = "NULL";
		message = "NULL";
		currentPort = -1;

		NODE = NODE_TYPE.NODE;
		COMMAND = COMMAND_TYPE.NULL;
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
			out.writeInt(NODE.ordinal());
			out.writeInt(COMMAND.ordinal());
			out.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final byte[] res = os.toByteArray();

		return res;
	}

	public static Node fromBytes(final byte[] byteArray) {
		Node node = new Node();
		ByteArrayInputStream is;
		ObjectInputStream in;

		Client client;
		Server server;
		
		try {
			is = new ByteArrayInputStream(byteArray);
			in = new ObjectInputStream(is);

			node.setUsername(in.readUTF());
			node.setHostname(in.readUTF());
			node.setCurrentIP(in.readUTF());
			node.addStringMessage(in.readUTF());
			node.setPort(in.readInt());
			node.NODE = NODE_TYPE.values()[in.readInt()];
			node.COMMAND = COMMAND_TYPE.values()[in.readInt()];
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* Casting to appropriate object. */
		switch (node.NODE) {
		case CLIENT: {
			client = new Client();
			
			client.setUsername(node.getUsername());
			client.setHostname(node.getHostname());
			client.setCurrentIP(node.getCurrentIP());
			client.addStringMessage(node.getStringMessage());
			client.setPort(node.getCurrentPort());
			client.NODE = node.NODE;
			client.COMMAND = node.COMMAND;
			
			return client;
		}
		case SERVER: {
			server = new Server();
			
			server.setUsername(node.getUsername());
			server.setHostname(node.getHostname());
			server.setCurrentIP(node.getCurrentIP());
			server.addStringMessage(node.getStringMessage());
			server.setPort(node.getCurrentPort());
			server.NODE = node.NODE;
			server.COMMAND = node.COMMAND;
			
			return server;
		}
		case NODE: {
			return node;
		}
		default: {
			return node;
		}
		}
	}
}
