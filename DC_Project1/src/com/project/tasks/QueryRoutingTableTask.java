package com.project.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.RoutingTableServlet;
import com.project.server.ServerReceiverServlet;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.router.Client;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;

public class QueryRoutingTableTask extends SimpleAbstractTask {
	
	public static final int PORT = 57911;
	
	private DatagramSocket dataGramSocket;
	
	private DatagramPacket dataGram;
	
	private DatagramPacket receiveGram;
	
	private String queryUserName;
	
	private boolean byIP = false;
	
	private Client selfClient;
	
	private byte[] buffer;
	
	public QueryRoutingTableTask() {
		
	}
	
	public QueryRoutingTableTask(final String userName, final boolean byIP) {
		queryUserName = userName;
		this.byIP = byIP;
	}

	@Override
	public void executeTask() {
		try {
			dataGramSocket = new DatagramSocket(PORT);
			dataGramSocket.setSoTimeout(3000);		// 3 seconds
			
			selfClient = new Client();
			selfClient.setCurrentIP(InetAddress.getLocalHost()
					.getHostAddress());
			selfClient.setHostname(InetAddress.getLocalHost()
					.getHostName());
			selfClient.setPort(ServerReceiverServlet.LISTENING_PORT);
			selfClient.setUsername("Client "
					+ DCServer.getLocalHostname());
			selfClient.SERVER_COMMAND = COMMAND_TYPE.REGISTER_NODE;
			
			buffer = selfClient.toBytes();
			
			dataGram = new DatagramPacket(buffer, buffer.length);
			dataGram.setPort(RoutingTableServlet.LISTENING_PORT);
			dataGram.setAddress(InetAddress.getByName(queryUserName));
			
			dataGramSocket.send(dataGram);
			dataGramSocket.receive(receiveGram);
			
			Node node = Node.fromBytes(receiveGram.getData());
			
			setStringData(node.getUsername());
			
			stopTask();
			
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
