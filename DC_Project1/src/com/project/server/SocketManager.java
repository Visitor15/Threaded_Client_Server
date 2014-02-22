package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.project.tasks.ThreadHelper;

public class SocketManager {
	
	private static SocketManager instance;
	
	private static DatagramSocket datagramSendSocket;
	
	private SocketManager() {
		try {
			datagramSendSocket = new DatagramSocket(6868);
			datagramSendSocket.setBroadcast(true);
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
			ThreadHelper.sleepThread(100);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
}
