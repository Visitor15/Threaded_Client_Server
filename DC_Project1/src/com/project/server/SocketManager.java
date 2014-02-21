package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class SocketManager {
	
	private static SocketManager instance;
	
	private static DatagramSocket datagramSendSocket;
	
	private SocketManager() {
		try {
			datagramSendSocket = new DatagramSocket(6868);
			instance = this;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static SocketManager getInstance() {
		if(instance == null) {
			new SocketManager();
		}
		
		return instance;
	}

	public synchronized boolean sendDatagram(final DatagramPacket dataGram) {
		try {
			datagramSendSocket.send(dataGram);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
}
