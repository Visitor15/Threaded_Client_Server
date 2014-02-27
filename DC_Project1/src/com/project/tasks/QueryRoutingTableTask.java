package com.project.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.project.framework.Task;
import com.project.main.Main;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.RoutingTableServlet;
import com.project.server.router.Client;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;
import com.project.server.router.Server;

public class QueryRoutingTableTask extends SimpleAbstractTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 503971943938835559L;

	public static final int PORT = 57911;

	private DatagramSocket dataGramSocket;

	private DatagramPacket dataGram;

	private DatagramPacket receiveGram;

	private String queryUserIP;

	private Client selfClient;

	private byte[] buffer;

	int count = 0;

	public QueryRoutingTableTask() {

	}

	public QueryRoutingTableTask(final String userName, final boolean byIP) {
		queryUserIP = userName;
	}

	@Override
	public void executeTask() {

		setTaskId("QueryRoutingTableTask");
		try {
			dataGramSocket = new DatagramSocket(PORT);
			dataGramSocket.setSoTimeout(5000); // 3 seconds

			selfClient = new Client();
			selfClient.setCurrentIP(InetAddress.getLocalHost().getHostName());
			selfClient.setRouterName(queryUserIP);
			selfClient.setHostname(InetAddress.getLocalHost().getHostName());
			selfClient.setPort(PORT);
			selfClient.setRouterPort(RoutingTableServlet.LISTENING_PORT);
			selfClient.setUsername("Client "
					+ InetAddress.getLocalHost().getHostName());
			selfClient.SERVER_COMMAND = COMMAND_TYPE.NULL;
			selfClient.ROUTERTABLE_COMMAND = COMMAND_TYPE.PING_PRIMARY_SERVER_NODE;
			selfClient.addStringMessage(queryUserIP);

			buffer = selfClient.toBytes();

			dataGram = new DatagramPacket(buffer, buffer.length);
			dataGram.setPort(RoutingTableServlet.LISTENING_PORT);
			dataGram.setAddress(InetAddress.getByName(Main.ROUTING_TABLE_IP));

			buffer = new byte[1024];

			receiveGram = new DatagramPacket(buffer, buffer.length);

			dataGramSocket.send(dataGram);
			dataGramSocket.receive(receiveGram);

			buffer = receiveGram.getData();

			Node node = Node.fromBytes(buffer);

			node.setCurrentIP(receiveGram.getAddress().getHostAddress());
			node.setHostname(receiveGram.getAddress().getHostName());

			RoutingTable.getInstance().registerServer((Server) node);

			setStringData(new String(node.toBytes()));

			stopTask();

		} catch (SocketTimeoutException e) {
			dataGramSocket.close();
			if (count == 5) {
				stopTask();
			}
			++count;
			executeTask();
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

		dataGramSocket.close();
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
