package com.project.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.project.framework.Task;
import com.project.io.SynchedInOut;
import com.project.server.DCServer;
import com.project.server.ServerReceiverServlet;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.router.Client;

public class ServerFinderTask extends SimpleAbstractTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1587055987565457377L;

	public static final int SENDING_PORT = 5813;

	private DatagramSocket sendingSocket;

	private DatagramPacket dataGram;

	private byte[] buffer;

	private Client client;

	public ServerFinderTask() {

	}
	
	public ServerFinderTask(final ITaskCallback callback) {
		super(callback);
		
		setTaskId("ServerFinderTask");
	}

	@Override
	public void executeTask() {
		try {
			sendingSocket = new DatagramSocket(SENDING_PORT);
			sendingSocket.setBroadcast(true);

			client = new Client();
			client.setCurrentIP(InetAddress.getLocalHost().getHostAddress());
			client.setHostname(InetAddress.getLocalHost().getHostName());
			client.setPort(ServerReceiverServlet.LISTENING_PORT);
			client.setUsername("Client " + client.getHostname());
			
			client.COMMAND = COMMAND_TYPE.REGISTER_NODE;

			buffer = client.toBytes();

			String defGateway = DCServer.GetDefaultGateway();

			String ipPiece = defGateway.substring(0, defGateway.length() - 1);

			String[] ipPieces = defGateway.split("\\.");

			ipPiece = ipPieces[0] + "." + ipPieces[1] + ".";

			for (int i = 0; i < 255; i++) {
				for (int j = 0; j < 255; j++) {
					String builtIP = ipPiece + Integer.toString(i) + "."
							+ Integer.toString(j);

					dataGram = new DatagramPacket(buffer, buffer.length);
					dataGram.setPort(ServerReceiverServlet.LISTENING_PORT);
					dataGram.setAddress(InetAddress.getByName(builtIP));

					sendingSocket.send(dataGram);
				}
			}
			
			sendingSocket.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String userInput = SynchedInOut.getInstance().postMessageForUserInput("Network scan finished. Scan again? (y/n): ");
//		
//		if(userInput.equalsIgnoreCase("y")) {
//			executeTask();
//		}
		
		stopTask();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		System.out.println("Server scan finished");
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
