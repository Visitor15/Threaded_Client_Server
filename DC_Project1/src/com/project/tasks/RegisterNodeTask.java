package com.project.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.ServerReceiverServlet;
import com.project.server.SocketManager;
import com.project.server.router.Client;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;
import com.project.server.router.Server;

public class RegisterNodeTask extends SimpleAbstractTask {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = -6074291907488939311L;

	public static final int BUFFER_SIZE = 64;

	public static final int SENDING_PORT = 11235;

	private DatagramPacket dataGram;

	private byte[] buffer;

	private Server selfServer;

	private Client selfClient;

	private Server server;

	private Client client;

	private Node node;

	public RegisterNodeTask(final Node n) {
		setTaskId("RegisterClientTask");
		node = n;
	}

	@Override
	public void executeTask() {

		/* Redundant code here in the event Server and Client start diverting. */
		try {
			/* Casting to appropriate object. */
			switch (node.NODE) {
			case SERVER: {
				server = (Server) node;

				if (RoutingTable.getInstance().registerServer(server)) {
					System.out.println("Registered server: "
							+ node.getHostname());

					selfClient = new Client();
					selfClient.setCurrentIP(InetAddress.getLocalHost()
							.getHostAddress());
					selfClient.setHostname(InetAddress.getLocalHost()
							.getHostName());
					selfClient.setPort(ServerReceiverServlet.LISTENING_PORT);
					selfClient.setUsername("Client "
							+ DCServer.getLocalHostname());
					selfClient.COMMAND = COMMAND_TYPE.REGISTER_NODE;

					buffer = selfClient.toBytes();

					dataGram = new DatagramPacket(buffer, buffer.length);
					dataGram.setPort(server.getCurrentPort());
					dataGram.setAddress(InetAddress.getByName(server
							.getCurrentIP()));
				}

				break;
			}
			case CLIENT: {
				client = (Client) node;

				if (RoutingTable.getInstance().registerClient(client)) {
					System.out.println("Registered client: "
							+ node.getHostname());

					selfServer = new Server();
					selfServer.setCurrentIP(InetAddress.getLocalHost()
							.getHostAddress());
					selfServer.setHostname(InetAddress.getLocalHost()
							.getHostName());
					selfServer.setPort(ServerReceiverServlet.LISTENING_PORT);
					selfServer.setUsername("Server "
							+ DCServer.getLocalHostname());

					selfServer.COMMAND = COMMAND_TYPE.REGISTER_NODE;

					buffer = selfServer.toBytes();

					dataGram = new DatagramPacket(buffer, buffer.length);
					dataGram.setPort(client.getCurrentPort());
					dataGram.setAddress(InetAddress.getByName(client
							.getCurrentIP()));
				}

				break;
			}
			case NODE: {
				break;
			}
			default: {
				break;
			}
			}

			/* NULL indicates no node was registered. */
			if (dataGram != null) {
				SocketManager.getInstance().sendDatagram(dataGram);
			}
		} catch (IOException e) {
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
//		System.out.println("Node registered*");
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
