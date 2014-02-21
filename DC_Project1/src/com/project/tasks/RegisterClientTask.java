package com.project.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;

import com.project.framework.Task;
import com.project.server.ServerReceiverServelet;
import com.project.server.SocketManager;
import com.project.server.router.Client;
import com.project.server.router.RoutingTable;

public class RegisterClientTask extends SimpleAbstractTask {

	public static final int BUFFER_SIZE = 64;

	public static final int SENDING_PORT = 11235;

	private DatagramSocket sendingSocket;

	private DatagramPacket dataGram;

	private byte[] buffer;

	private Client client;

	public RegisterClientTask(final Client c) {
		setTaskId("RegisterClientTask");
		client = c;
	}

	@Override
	public void executeTask() {
		if (RoutingTable.getInstance().registerClient(client)) {
			System.out.println("Registered client: " + client.getHostname());
			try {
				client = new Client();
				client.setCurrentIP(InetAddress.getLocalHost().getHostAddress());
				client.setHostname(InetAddress.getLocalHost().getHostName());
				client.setPort(ServerReceiverServelet.LISTENING_PORT);
				client.setUsername("Server " + client.getHostname());
				
				buffer = client.toBytes();

				dataGram = new DatagramPacket(buffer, buffer.length);
				dataGram.setPort(client.getCurrentPort());
				dataGram.setAddress(InetAddress.getByName(client.getCurrentIP()));
				
				SocketManager.getInstance().sendDatagram(dataGram);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		stopTask();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {

	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task fromBytes(byte[] byteArray) {
		// TODO Auto-generated method stub
		return null;
	}
}
