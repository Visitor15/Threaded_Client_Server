package com.project.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.ServerReceiverServlet;
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

	private BufferedReader receive;

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

			if (toServer || node == null) {
				TaskManager.DoTaskOnCurrentThread(new QueryRoutingTableTask(
						clientNode.getDestinationIP(), true), this);
			}

			System.out.println("HIT HIT HIT");
			Socket clientSocket = new Socket(node.getCurrentIP(),
					node.getCurrentPort()); // port needs to be
											// serverrouters port
			// network output stream
			send = new DataOutputStream(clientSocket.getOutputStream());
			// network input stream
			receive = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			// get a lowercase message from the user (no verification or
			// anything)
			// String message = fileName.getText();

			// String message = "NULL";

			if (clientNode != null) {
				message = clientNode.getStringMessage();
				System.out.println("Sending message: " + message);
			}

			send.writeUTF(message + "\n");

			String receivedMessage = receive.readLine();

			System.out.println("Received: " + receivedMessage);

			// InputStream file = new FileInputStream(fileName.getText());
			// InputStream file = new FileInputStream(message);
			// BufferedReader reader = new BufferedReader(new InputStreamReader(
			// file));
			// String line = null;

			// send.writeBytes(clientNode.message);

			/*
			 * in the while loop below we need to add the statistics for average
			 * length of each line we send the average round trip time of each
			 * message.
			 */

			// while ((line = reader.readLine()) != null) // loop to end of file
			// // sending every line
			// {
			// // outputWindow.append("Sending TCP: " + line + "\n"); //we may
			// // want to only output statistics if its a long file
			// // convert to bytes and write to stream
			// send.writeBytes(line + '\n');
			// // receive the message back from the server
			// String modifiedMsg = receive.readLine();
			// System.out.println("Server TCP: " + modifiedMsg);
			// // outputWindow.append("Server TCP: " + modifiedMsg + "\n");
			// // we are done here close the socket!
			//
			// }
			clientSocket.close(); // we are done here

		} catch (IOException e) {
			e.printStackTrace();
		}

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
		System.out.println("Hit callback HERE - " + task.getTaskId());
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
