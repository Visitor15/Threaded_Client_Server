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
import com.project.tasks.RegisterNodeTask;
import com.project.tasks.SendStringMessageTask;
import com.project.tasks.TaskManager;

public class RoutingTableServlet extends DCServlet {

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
	public void respondToRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse() {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeTask() {
		System.out.println("Server is listening");

		

		try {
			/* Registering self as server in routing table. */
			Server selfServer = new Server();
			selfServer.setHostname(InetAddress.getLocalHost().getHostName());
			selfServer.setPort(ServerReceiverServlet.LISTENING_PORT);
			selfServer.setUsername("Server " + DCServer.getLocalHostname());

			RoutingTable.getInstance().registerServer(selfServer);
			
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
							TaskManager.DoTask(new RegisterNodeTask(node));
						}
						break;
					}
					case ROUTE_DATA_TO_SERVER: {

						Server server = RoutingTable.getInstance()
								.getPrimaryServer();

						if (server != null) {
							node.setPort(server.getCurrentPort());
							node.setDestinationIP(server.getCurrentIP());
							node.setDestinationHostname(server.getHostname());
							TaskManager.DoTask(new SendStringMessageTask(node));
						}
						break;
					}
					case ROUTE_DATA_TO_CLIENT: {
						Client client = RoutingTable.getInstance()
								.getClientByUsername(node.getUsername());

						if (client != null) {
							node.setPort(client.getCurrentPort());
							node.setDestinationIP(client.getCurrentIP());
							node.setDestinationHostname(client.getHostname());
							TaskManager.DoTask(new SendStringMessageTask(node));
						}
						break;
					}
					case PING_NODE: {
						System.out.println("Pinging node: " + node.getDestinationHostname());

						selfServer = RoutingTable.getInstance()
								.getServerByHostname(
										node.getDestinationHostname());

						if (selfServer != null) {

							buffer = selfServer.toBytes();

							dataGram = new DatagramPacket(buffer, buffer.length);
							dataGram.setPort(node.getCurrentPort());
							dataGram.setAddress(InetAddress.getByName(node
									.getCurrentIP()));

							receivingSocket.send(dataGram);
						}
						break;

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
	public void onProgressUpdate() {
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
