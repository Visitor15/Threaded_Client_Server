package com.project.tasks;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.SocketManager;
import com.project.server.router.Client;
import com.project.server.router.Node;

public class SendStringMessageTask extends SimpleAbstractTask implements
		ITaskCallback {

	/**
	 * Serializable stuff.
	 */
	private static final long serialVersionUID = -3296275676738733533L;

	public static final int PORT = 13135;

	private Node node = null;

	private Node clientNode;

	private boolean toServer;

	private DatagramSocket datagramSocket;

	private DatagramPacket dataGram;

	private byte[] buffer;

	private DataOutputStream send;

	private DataInputStream inDataStream;

	private Socket clientSocket;

	private String receivedMessage;

	private Scanner userInput;

	private String fileText;

	String recipientHostname = "NULL";

	private ArrayList<String> textLines;

	public SendStringMessageTask(final Node node) {
		super();
		setTaskId("SendStringMessagesTask");

		this.node = node;
	}

	public SendStringMessageTask(final Node client, boolean toServer, final ITaskCallback callback) {
		super();
		setTaskId("SendStringMessagesTask");
		this.m_Callback = callback;
		this.toServer = toServer;
		clientNode = client;
	}

	long endLookupTime = 0;
	@Override
	public void executeTask() {
		clientNode.ROUTERTABLE_COMMAND = COMMAND_TYPE.REGISTER_NODE;
		
		try {
			
			buffer = clientNode.toBytes();
			
			DatagramPacket dataGram = new DatagramPacket(buffer, buffer.length);
			dataGram.setPort(clientNode.getRouterPort());
			dataGram.setAddress(InetAddress.getByName(DCServer.ROUTING_TABLE_IP));
			
			SocketManager.getInstance().sendDatagram(dataGram);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*
		 * the following code will connect to the server router and then open a
		 * file for sending the data
		 */

		textLines = new ArrayList<String>();

		try {
			readFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			userInput = new Scanner(System.in);
			long startLookupTime = 0;
			if (toServer || node == null) {
				startLookupTime = System.currentTimeMillis();
				TaskManager.DoTaskOnCurrentThread(new QueryRoutingTableTask(
						clientNode.getRouterName(), true), this);
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
			
			long avgLookupTime = endLookupTime - startLookupTime;
			
			
			
			long totalMsgSize = 0;
			long time = System.currentTimeMillis();

			if (textLines.size() > 0) {
				for (int i = 0; i < textLines.size(); i++) {
					if (textLines.get(i) != null) {
						
						totalMsgSize += textLines.get(i).getBytes().length;
						
						send.writeUTF(textLines.get(i));
						receivedMessage = inDataStream.readUTF();
						
						setStringData(receivedMessage);
						
						
						
						m_Callback.onTaskProgress(this);
						
//						System.out.println(recipientHostname + ": "
//								+ receivedMessage);
					}
				}
			}
			
			long finishedTime = System.currentTimeMillis();
			
			long diff = finishedTime - time;
			
			double avgMsgRoundTime = textLines.size() / (double) diff;
			
			long avgMsgSize = totalMsgSize / textLines.size();
			
			System.out.println("Total msgs sent: " + textLines.size());
			System.out.println("Lookup time: " + avgLookupTime);
			System.out.println("Avg msg size: " + avgMsgSize);
			System.out.println("Avg round trip time: " + avgMsgRoundTime);

			send.writeUTF("Q");

			// do {
			// System.out.print(InetAddress.getLocalHost().getHostName()
			// + ": ");
			// message = userInput.nextLine();
			//
			// // send.writeBytes(message);
			// send.writeUTF(message);
			// receivedMessage = inDataStream.readUTF();
			//
			// if (message.equalsIgnoreCase("q")
			// || receivedMessage.equalsIgnoreCase("q")) {
			// System.out.println("Recieved QUIT command. Closing.");
			// break;
			// }
			//
			// System.out.println(node.getHostname() + ": " + receivedMessage);
			// } while (!receivedMessage.equalsIgnoreCase("q")
			// || !message.equalsIgnoreCase("q"));

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
	public Task fromBytes(byte[] byteArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

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
	public void onTaskFinished(Task task) {
		endLookupTime = System.currentTimeMillis();
		node = Node.fromBytes(task.getStringData().getBytes());

		recipientHostname = node.getHostname();

		try {
			datagramSocket = new DatagramSocket(SendStringMessageTask.PORT);

			Client selfClient = new Client();
			selfClient
					.setCurrentIP(InetAddress.getLocalHost().getHostAddress());
			selfClient.setHostname(InetAddress.getLocalHost().getHostName());
			selfClient.setRouterName(node.getRouterName());
			selfClient.setPort(SendStringMessageTask.PORT);
			selfClient.setRouterPort(node.getRouterPort());
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

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	public void readFile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("moby10b.txt"));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
				textLines.add(line);
			}
			fileText = sb.toString();
		} finally {
			br.close();
		}
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}
}
