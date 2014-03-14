package com.project.tasks;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.RoutingTableServlet;
import com.project.server.ServerReceiverServlet;
import com.project.server.SocketManager;
import com.project.server.router.Node;

public class RegisterNodeTask extends SimpleAbstractTask {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = -6074291907488939311L;

	public static final int BUFFER_SIZE = 64;

	public int LISTENING_PORT = 11235;

	private static DatagramSocket datagramSocket;

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
				System.out.println("NODE ROUTER COMMAND: "
						+ node.ROUTERTABLE_COMMAND.name());
//				node.setReceivingPort(LISTENING_PORT);
				node.setDestinationPort(LISTENING_PORT);
				buffer = node.toBytes();

				datagramSocket = new DatagramSocket(LISTENING_PORT);
				datagramSocket.setSoTimeout(5000);

				dataGram = new DatagramPacket(buffer, buffer.length);
				dataGram.setPort(RoutingTableServlet.LISTENING_PORT);

				dataGram.setAddress(InetAddress
						.getByName(DCServer.ROUTING_TABLE_IP));
				SocketManager.getInstance().sendDatagram(dataGram);

				System.out.println("Routing Table: "
						+ DCServer.ROUTING_TABLE_IP);
				
				buffer = new byte[1024];
				dataGram = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(dataGram);

				Node mNode = Node.fromBytes(dataGram.getData());
				DCServer.CURRENT_PRIMARY_SERVER = mNode.getCurrentIP();

				String returnMessage = mNode.getStringMessage();

				if (returnMessage.equalsIgnoreCase("REGISTER_OKAY")) {
					System.out
							.println("Registered on routing table succesfully!");
				} else {
					System.out.println("EXCEPTION: " + returnMessage);
				}

				setStringData("Primary server found at: "
						+ DCServer.CURRENT_PRIMARY_SERVER);
				m_Callback.onTaskProgress(this);

				datagramSocket.close();
			}
		} catch(BindException e) {
			e.printStackTrace();
			LISTENING_PORT++;
			executeTask();
		}
		catch (SocketTimeoutException e) {
			e.printStackTrace();
			stopTask();
		} catch (IOException e) {
			e.printStackTrace();
			stopTask();
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
