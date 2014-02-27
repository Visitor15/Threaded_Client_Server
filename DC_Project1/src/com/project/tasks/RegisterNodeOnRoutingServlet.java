package com.project.tasks;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.RoutingTableServlet;
import com.project.server.SocketManager;
import com.project.server.router.Node;

public class RegisterNodeOnRoutingServlet extends SimpleAbstractTask {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = -6074291907488939311L;

	public static final int BUFFER_SIZE = 64;

	public static int LISTENING_PORT = 11235;
	
	private DatagramSocket datagramSocket;

	private DatagramPacket dataGram;

	private byte[] buffer;

	private Node node;

	public RegisterNodeOnRoutingServlet(final Node n) {
		setTaskId("RegisterClientTask");
		node = n;
	}

	@Override
	public void executeTask() {
		

		/* Redundant code here in the event Server and Client start diverting. */
		try {
			datagramSocket = new DatagramSocket(LISTENING_PORT);
			/* NULL indicates no node was registered. */
			if (node != null) {
				
				switch(node.NODE) {
				case CLIENT:
					break;
				case NODE:
					break;
				case SERVER:
					break;
				default:
					break;
				
				}
				
				buffer = node.toBytes();
				
				dataGram = new DatagramPacket(buffer, buffer.length);
				dataGram.setPort(RoutingTableServlet.LISTENING_PORT);
				dataGram.setAddress(InetAddress
						.getByName(DCServer.ROUTING_TABLE_IP));
				SocketManager.getInstance().sendDatagram(dataGram);
				
				buffer = new byte[1024];
				dataGram = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(dataGram);
				
				Node node = Node.fromBytes(dataGram.getData());
				DCServer.ROUTING_TABLE_IP = node.getCurrentIP();
				
				datagramSocket.close();
			}
		} catch (BindException e) {
			LISTENING_PORT += 1;
		}
		catch (IOException e) {
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
		// System.out.println("Node registered*");
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
