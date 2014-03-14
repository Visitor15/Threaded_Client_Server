package com.project.tasks;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.project.framework.Task;
import com.project.server.SocketManager;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;

public class RouteDataTask extends SimpleAbstractTask implements ITaskCallback {

	/**
	 * Serializable stuff
	 */
	private static final long serialVersionUID = 5236205344457418583L;

	private DatagramPacket dataGram;

	private Node node;

	private Node destinationNode;
	
	private int serverPort;

	private byte[] buffer;

	private boolean toServer;

	public RouteDataTask(final Node n, final boolean toServer) {
		setTaskId("RouteDataTask");
		this.toServer = toServer;
		node = n;
	}

	@Override
	public void executeTask() {

		try {
			if (toServer) {
				destinationNode = RoutingTable.getInstance()
						.getServerByHostname(node.getDestinationIP());
				destinationNode.SERVER_COMMAND = node.SERVER_COMMAND;
			} else {
				destinationNode = RoutingTable.getInstance().getClientByIP(
						node.getDestinationIP());
			}

			if (destinationNode != null) {
				serverPort = destinationNode.getReceivingPort();
				destinationNode.setReceivingIP(node.getCurrentIP());
				destinationNode.setReceivingPort(node.getReceivingPort());
				buffer = destinationNode.toBytes();
				dataGram = new DatagramPacket(buffer, buffer.length);
			}

			System.out.println("Sending back to node: "
					+ destinationNode.getCurrentIP() + " on port: "
					+ serverPort);

			dataGram.setPort(serverPort);

			dataGram.setAddress(InetAddress.getByName(destinationNode
					.getCurrentIP()));

			SocketManager.getInstance().sendDatagram(dataGram);
		} catch (UnknownHostException e) {
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

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

}
