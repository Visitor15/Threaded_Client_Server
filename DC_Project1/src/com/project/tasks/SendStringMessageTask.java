package com.project.tasks;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.router.Client;
import com.project.server.router.Node;

public class SendStringMessageTask extends SimpleAbstractTask implements
		ITaskCallback {

	public static final int PORT = 13135;

	private Node node = null;

	private Node clientNode;

	private boolean toServer;

	private String message;

	private DatagramSocket datagramSocket;

	private DatagramPacket dataGram;

	private byte[] buffer;

	private DataOutputStream send;

	private DataInputStream inDataStream;

	private Socket clientSocket;

	private String receivedMessage;

	private Scanner userInput;

	public SendStringMessageTask(final Node client, boolean toServer) {
		super();
		setTaskId("SendStringMessagesTask");

		this.toServer = toServer;
		clientNode = client;
	}

	public SendStringMessageTask(final Node node) {
		super();
		setTaskId("SendStringMessagesTask");

		this.node = node;
	}

	@Override
	public void executeTask() {
		/*
		 * the following code will connect to the server router and then open a
		 * file for sending the data
		 */

		try {
			userInput = new Scanner(System.in);

			if (toServer || node == null) {
				TaskManager.DoTaskOnCurrentThread(new QueryRoutingTableTask(
						clientNode.getDestinationIP(), true), this);
			}
			clientSocket = new Socket(node.getCurrentIP(),
					node.getCurrentPort());
			// network output stream
			send = new DataOutputStream(clientSocket.getOutputStream());

			inDataStream = new DataInputStream(clientSocket.getInputStream());

			// if (clientNode != null) {
			// message = clientNode.getStringMessage();
			// System.out.println("Sending message: " + message);
			// send.writeUTF(message + "\n");
			// }
			do {
				System.out.print(InetAddress.getLocalHost().getHostName()
						+ ": ");
				message = userInput.nextLine();

				// send.writeBytes(message);
				send.writeUTF(message);
				receivedMessage = inDataStream.readUTF();

				if (message.equalsIgnoreCase("q")
						|| receivedMessage.equalsIgnoreCase("q")) {
					System.out.println("Recieved QUIT command. Closing.");
					break;
				}

				System.out.println("Received: " + receivedMessage);
			} while (!receivedMessage.equalsIgnoreCase("q")
					|| !message.equalsIgnoreCase("q"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			send.flush();
			send.close();
			inDataStream.close();
			clientSocket.close(); // we are done here
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

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(Task task) {
		node = Node.fromBytes(task.getStringData().getBytes());

		try {
			datagramSocket = new DatagramSocket(SendStringMessageTask.PORT);

			Client selfClient = new Client();
			selfClient
					.setCurrentIP(InetAddress.getLocalHost().getHostAddress());
			selfClient.setHostname(InetAddress.getLocalHost().getHostName());
			selfClient.setPort(SendStringMessageTask.PORT);
			selfClient.setUsername("Client " + DCServer.getLocalHostname());
			selfClient.SERVER_COMMAND = COMMAND_TYPE.SEND_STRING_MESSAGE;

			buffer = selfClient.toBytes();

			dataGram = new DatagramPacket(buffer, buffer.length);
			dataGram.setPort(node.getCurrentPort());
			dataGram.setAddress(InetAddress.getByName(node.getCurrentIP()));

			datagramSocket.send(dataGram);

			buffer = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(buffer,
					buffer.length);

			datagramSocket.receive(receivePacket);

			node = Node.fromBytes(receivePacket.getData());
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}
}
