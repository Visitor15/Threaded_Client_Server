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
import com.project.server.router.Client;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;
import com.project.server.router.Server;

public class RegisterNodeOnRoutingServlet extends SimpleAbstractTask {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = -6074291907488939311L;

	public static final int BUFFER_SIZE = 64;

	public static int LISTENING_PORT = 11235;

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
			/* NULL indicates no node was registered. */
			if (node != null) {
				
				Node mNode = new Client();
				
				switch(node.NODE) {
				case CLIENT:
					
					mNode = RoutingTable.getInstance().getPrimaryServer();
					
					if(RoutingTable.getInstance().registerClient((Client) node)) {
						mNode.addStringMessage("REGISTER_OKAY");
					} else {
						mNode.addStringMessage("REGISTRATION_ERROR");
					}
					
					break;
				case NODE:
					
					mNode.addStringMessage("Node " + node.getCurrentIP() + " did not have a NODE_TYPE");
					
					break;
				case SERVER:
					
					if(RoutingTable.getInstance().registerServer((Server) node)){
						mNode.addStringMessage("REGISTER_OKAY");
					} else {
						mNode.addStringMessage("REGISTRATION_ERROR");
					}
					
					break;
				default:
					break;
				
				}
				
				mNode.setDestinationIP(node.getCurrentIP());
				mNode.setDestinationPort(node.getDestinationPort());
				
				buffer = mNode.toBytes();
				dataGram = new DatagramPacket(buffer, buffer.length);
				dataGram.setPort(mNode.getDestinationPort());
				dataGram.setAddress(InetAddress
						.getByName(mNode.getDestinationIP()));
				SocketManager.getInstance().sendDatagram(dataGram);
			}
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
