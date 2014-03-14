package com.project.tasks;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.project.framework.Task;
import com.project.server.SocketManager;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;
import com.project.server.router.Server;

public class RouteDataTask extends SimpleAbstractTask implements ITaskCallback {

	/**
	 * 	Serializable stuff
	 */
	private static final long serialVersionUID = 5236205344457418583L;

	private DatagramPacket dataGram;

	private Node node;

	private Server currentServer;

	private byte[] buffer;

	public RouteDataTask(final Node n) {
		node = n;
	}

	@Override
	public void executeTask() {

		try {
			currentServer = RoutingTable.getInstance().getPrimaryServer();

			if (currentServer != null) {
				buffer = currentServer.toBytes();
				dataGram = new DatagramPacket(buffer, buffer.length);
			} else {
				buffer = new Server().toBytes();
				dataGram = new DatagramPacket(buffer, buffer.length);
			}

			dataGram.setPort(node.getReceivingPort());

			dataGram.setAddress(InetAddress.getByName(node.getCurrentIP()));

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
