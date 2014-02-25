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

	public COMMAND_TYPE SERVER_COMMAND;
	
	public COMMAND_TYPE ROUTERTABLE_COMMAND;

	public String username;

	public String message;

	public String currentIP;

	public String hostname;
	
	public String destIP;
	
	public String destUsername;
	
	public String destHostname;
	
	public int destinationPort;

	public int currentPort;

	Node() {
		username = "NULL";
		hostname = "NULL";
		currentIP = "NULL";
		message = "NULL";
		destIP = "NULL";
		destHostname = "NULL";
		destUsername = "NULL";
		currentPort = -1;

		NODE = NODE_TYPE.NODE;
		SERVER_COMMAND = COMMAND_TYPE.NULL;
		ROUTERTABLE_COMMAND = COMMAND_TYPE.NULL;
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
	
	public void setDestinationIP(final String IP) {
		destIP = IP;
	}
	
	public void setDestinationUsername(final String destUser) {
		destUsername = destUser;
	}
	
	public String getDestinationUsername() {
		return destUsername;
	}
	
	public void setDestinationHostname(final String hostName) {
		hostname = hostName;
	}
	
	public String getDestinationHostname() {
		return destHostname;
	}

	public String getDestinationIP() {
		return destIP;
	}
	
	public int getDestinationPort() {
		return destinationPort;
	}
	
	public void setDestinationPort(final int port) {
		destinationPort = port;
	}

	public byte[] toBytes() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream out;

		try {
			out = new ObjectOutputStream(os);
			out.writeUTF(getUsername());
			out.writeUTF(getHostname());
			out.writeUTF(getCurrentIP());
			out.writeInt(getDestinationPort());
			out.writeUTF(getDestinationIP());
			out.writeUTF(getDestinationHostname());
			out.writeUTF(getDestinationUsername());
			out.writeUTF(getStringMessage());
			out.writeInt(getCurrentPort());
			out.writeInt(NODE.ordinal());
			out.writeInt(SERVER_COMMAND.ordinal());
			out.writeInt(ROUTERTABLE_COMMAND.ordinal());
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
			node.setDestinationPort(in.readInt());
			node.setDestinationIP(in.readUTF());
			node.setDestinationHostname(in.readUTF());
			node.setDestinationUsername(in.readUTF());
			node.addStringMessage(in.readUTF());
			node.setPort(in.readInt());
			node.NODE = NODE_TYPE.values()[in.readInt()];
			node.SERVER_COMMAND = COMMAND_TYPE.values()[in.readInt()];
			node.ROUTERTABLE_COMMAND = COMMAND_TYPE.values()[in.readInt()];
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
			client.SERVER_COMMAND = node.SERVER_COMMAND;
			client.ROUTERTABLE_COMMAND = node.ROUTERTABLE_COMMAND;
			
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
			server.SERVER_COMMAND = node.SERVER_COMMAND;
			server.ROUTERTABLE_COMMAND = node.ROUTERTABLE_COMMAND;
			
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
