package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.project.framework.Task;
import com.project.server.router.Client;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;
import com.project.server.router.Server;
import com.project.tasks.RegisterNodeOnRoutingServlet;
import com.project.tasks.SendStringMessageTask;
import com.project.tasks.TaskManager;

public class RoutingTableServlet extends DCServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 940351058304132103L;

	public static final int LISTENING_PORT = 13145;

	private DatagramSocket receivingSocket;

	private DatagramPacket dataGram;

	private byte[] buffer;

	public RoutingTableServlet() {
		super();

		setTaskId("RoutingTableServlet");
	}

	public RoutingTableServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.CLIENT_RESPONDER_SERVLET, autoStart, callback);

		setTaskId("RoutingTableServlet");
	}

	@Override
	public void executeTask() {
		System.out.println("Server is listening");

		try {
			/* Registering self as server in routing table. */
			Server selfServer = new Server();
			Client client;
//			selfServer.setHostname(InetAddress.getLocalHost().getHostName());
//			selfServer.setPort(ServerReceiverServlet.LISTENING_PORT);
//			selfServer.setUsername("Server " + DCServer.getLocalHostname());

//			RoutingTable.getInstance().registerServer(selfServer);

			receivingSocket = new DatagramSocket(LISTENING_PORT);
			do {
				System.out.println(("RoutingServer listening."));
				dataGram = new DatagramPacket(new byte[1024], 1024);

				/* Blocking receive */
				receivingSocket.receive(dataGram);

				System.out.println("Got data");

				buffer = dataGram.getData();
				if (buffer != null || buffer.length > 0) {
					Node node = Node.fromBytes(dataGram.getData());

					node.setCurrentIP(dataGram.getAddress().getHostAddress());
					node.setHostname(dataGram.getAddress().getHostName());

					System.out.println("Got node: " + node.getUsername());

					switch (node.ROUTERTABLE_COMMAND) {
					case REGISTER_NODE: {
						if (!node.getHostname().equalsIgnoreCase(
								InetAddress.getLocalHost().getHostName())) {
							TaskManager.DoTask(new RegisterNodeOnRoutingServlet(node));
						}
						break;
					}
					case ROUTE_DATA_TO_SERVER: {

						Server server = RoutingTable.getInstance()
								.getPrimaryServer();

						if (server != null) {
							node.setReceivingPort(server.getReceivingPort());
							node.setDestinationIP(server.getCurrentIP());
							node.setDestinationHostname(server.getHostname());
							TaskManager.DoTask(new SendStringMessageTask(node));
						}
						break;
					}
					case ROUTE_DATA_TO_CLIENT: {
						client = RoutingTable.getInstance()
								.getClientByIP(node.getDestinationIP());

						if (client != null) {
							node.setReceivingIP(dataGram.getAddress().getHostAddress());
							node.setReceivingPort(client.getReceivingPort());
							node.setDestinationIP(client.getCurrentIP());
							node.setDestinationHostname(client.getHostname());
							TaskManager.DoTask(new SendStringMessageTask(node));
						}
						break;
					}
					case PING_PRIMARY_SERVER_NODE: {
						System.out.println("Pinging primary server");

						selfServer = RoutingTable.getInstance().getPrimaryServer();

						if (selfServer != null) {
							buffer = selfServer.toBytes();
							dataGram = new DatagramPacket(buffer, buffer.length);
						} else {
							buffer = new Server().toBytes();
							dataGram = new DatagramPacket(buffer, buffer.length);
						}

						dataGram.setPort(node.getReceivingPort());
						dataGram.setAddress(InetAddress.getByName(node
								.getCurrentIP()));
						receivingSocket.send(dataGram);

						break;

					}
					case PING_CLIENT_NODE: {
						System.out.println("Pinging node: "
								+ node.getDestinationHostname());

						client = RoutingTable.getInstance().getClientByIP(node.getDestinationIP());

						if (client != null) {
							buffer = client.toBytes();

							dataGram = new DatagramPacket(buffer, buffer.length);
							dataGram.setPort(node.getReceivingPort());
							dataGram.setAddress(InetAddress.getByName(node
									.getCurrentIP()));
						} else {
							buffer = new Server().toBytes();

							dataGram = new DatagramPacket(buffer, buffer.length);
							dataGram.setPort(node.getReceivingPort());
							dataGram.setAddress(InetAddress.getByName(node
									.getCurrentIP()));
						}

						receivingSocket.send(dataGram);
					}
					default: {
						break;
					}
					}
				}
			} while (isExecuting());

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Task fromBytes(byte[] byteArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void respondToRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse() {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}
