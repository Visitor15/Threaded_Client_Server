package com.project.tasks;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.ServerReceiverServlet;
import com.project.server.router.Client;
import com.project.server.router.Node;
import com.project.server.router.Server;

public class ReceiveRemoteMessagesTask extends SimpleAbstractTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1192091671636120314L;

	private ServerSocket m_SendingSocket;

	private Socket m_RecievingSocket;

	private final ArrayList<Client> connectedPeers;

	private static int LISTEN_PORT = 9797;

	private static int SEND_PORT = 15155;

	private Node clientNode;

	private byte[] buffer;

	private DatagramSocket datagramSocket;

	private DatagramPacket dataGram;

	private DataOutputStream send;

	private BufferedReader receive;
	
	private String receivedMessage;

	public ReceiveRemoteMessagesTask() {
		setTaskId("ReceiveRemoteMessagesTask");
		connectedPeers = new ArrayList<Client>();
	}

	public ReceiveRemoteMessagesTask(final Node node) {
		clientNode = node;
		setTaskId("ReceiveRemoteMessagesTask");
		connectedPeers = new ArrayList<Client>();
	}

	@Override
	public void executeTask() {

		try {
			m_SendingSocket = new ServerSocket(LISTEN_PORT);
		} catch (IOException e2) {
			LISTEN_PORT += 1;
			executeTask();
		}

		/* Responding the client to tell them which port to connect to */
		try {
			Server selfServer = new Server();

			selfServer
					.setCurrentIP(InetAddress.getLocalHost().getHostAddress());

			selfServer.setHostname(InetAddress.getLocalHost().getHostName());
			selfServer.setPort(ReceiveRemoteMessagesTask.LISTEN_PORT);
			selfServer.setUsername("Server " + DCServer.getLocalHostname());

			datagramSocket = new DatagramSocket(SEND_PORT);
			datagramSocket.setReuseAddress(true);

			buffer = selfServer.toBytes();

			dataGram = new DatagramPacket(buffer, buffer.length);
			dataGram.setPort(clientNode.getCurrentPort());
			dataGram.setAddress(InetAddress.getByName(clientNode.getCurrentIP()));

			System.out.println("Datagram: PORT: " + clientNode.getCurrentPort()
					+ " IP: " + clientNode.getCurrentIP());

			System.out.println("Sending datagram");

			datagramSocket.send(dataGram);

		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Listening for remote messages...");

		try {
			m_RecievingSocket = m_SendingSocket.accept();
			System.out.println("Received socket");
		} catch (IOException ex) {
			System.out.println("Can't accept client connection. ");
		}
		do {
			try {
				send = new DataOutputStream(m_RecievingSocket.getOutputStream());
				// network input stream
				receive = new BufferedReader(new InputStreamReader(
						m_RecievingSocket.getInputStream()));

				receivedMessage = receive.readLine();
				System.out.println("Received: " + receivedMessage);

				String returnMessage = receivedMessage.toUpperCase(Locale
						.getDefault());

				System.out.println("Sending message: " + returnMessage);
				send.writeUTF(returnMessage + "\n");
			} catch (IOException ex) {
				System.out.println("Can't get socket input stream. ");
			}
		} while (!receivedMessage.equalsIgnoreCase("/q"));

		try {
			send.flush();

			send.close();
			receive.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stopTask();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub

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
