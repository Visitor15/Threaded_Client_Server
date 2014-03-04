package com.project.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

	private DataInputStream inDataStream;

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
			selfServer.setReceivingPort(ReceiveRemoteMessagesTask.LISTEN_PORT);
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

			send = new DataOutputStream(m_RecievingSocket.getOutputStream());
			// network input stream
			// receive = new BufferedReader(new InputStreamReader(
			// m_RecievingSocket.getInputStream()));

			inDataStream = new DataInputStream(
					m_RecievingSocket.getInputStream());

			do {

				// receivedMessage = receive.readLine();
				receivedMessage = inDataStream.readUTF();
				System.out.println("Received: " + receivedMessage);

				String returnMessage = receivedMessage.toUpperCase(Locale
						.getDefault());

				System.out.println("Sending message: " + returnMessage);
				send.writeUTF(returnMessage);

				if (receivedMessage.equalsIgnoreCase("q")) {
					System.out.println("Recieved QUIT command. Closing.");
					break;
				}

			} while (!receivedMessage.equalsIgnoreCase("q"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			send.flush();
			send.close();
			inDataStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stopTask();
	}

	@Override
	public Task fromBytes(byte[] byteArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}
