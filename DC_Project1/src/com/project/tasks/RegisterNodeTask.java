package com.project.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import com.project.framework.Task;
import com.project.main.Main;
import com.project.server.RoutingTableServlet;
import com.project.server.SocketManager;
import com.project.server.router.Node;

public class RegisterNodeTask extends SimpleAbstractTask {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = -6074291907488939311L;

	public static final int BUFFER_SIZE = 64;

	public static final int SENDING_PORT = 11235;

	private DatagramPacket dataGram;

	private byte[] buffer;

	private Node node;

	public RegisterNodeTask(final Node n) {
		setTaskId("RegisterClientTask");
		node = n;
	}

	@Override
	public void executeTask() {

		/* Redundant code here in the event Server and Client start diverting. */
		try {

			/* NULL indicates no node was registered. */
			if (node != null) {
				buffer = node.toBytes();

				dataGram = new DatagramPacket(buffer, buffer.length);
				dataGram.setPort(RoutingTableServlet.LISTENING_PORT);
				dataGram.setAddress(InetAddress
						.getByName(Main.ROUTING_TABLE_IP));
				SocketManager.getInstance().sendDatagram(dataGram);
			}
		} catch (IOException e) {
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
