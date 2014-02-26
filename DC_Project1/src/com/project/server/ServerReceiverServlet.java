package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.project.framework.Task;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;
import com.project.server.router.Server;
import com.project.tasks.ReceiveRemoteMessagesTask;
import com.project.tasks.RegisterNodeTask;
import com.project.tasks.TaskManager;

public class ServerReceiverServlet extends DCServlet {

	public static final int LISTENING_PORT = 11337;

	private DatagramSocket receivingSocket;

	private DatagramPacket dataGram;

	private Server selfServer;

	private byte[] buffer;

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 1288745814717362014L;

	public ServerReceiverServlet() {
		super();

		try {
			selfServer = new Server();

			selfServer
					.setCurrentIP(InetAddress.getLocalHost().getHostAddress());

			selfServer.setHostname(InetAddress.getLocalHost().getHostName());
			selfServer.setPort(ServerReceiverServlet.LISTENING_PORT);
			selfServer.setUsername("Server " + DCServer.getLocalHostname());

			RoutingTable.getInstance().registerServer(selfServer);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setTaskId("ServerReceiverServelet");
	}

	public ServerReceiverServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.REGISTRATION_SERVLET, autoStart, callback);

		setTaskId("ServerReceiverServelet");
	}

	@Override
	public void executeTask() {
		System.out.println("Server is listening");
		try {
			receivingSocket = new DatagramSocket(LISTENING_PORT);
			do {
				// System.out.println(("Server listening..."));
				dataGram = new DatagramPacket(new byte[512], 512);

				/* Blocking receive */
				receivingSocket.receive(dataGram);

				System.out.println("Received datagram");

				buffer = dataGram.getData();
				if (buffer != null || buffer.length > 0) {
					Node node = Node.fromBytes(dataGram.getData());

					node.setCurrentIP(dataGram.getAddress().getHostAddress());

					// System.out.println("Got node: " + node.getHostname());

					switch (node.SERVER_COMMAND) {
					case REGISTER_NODE: {
						if (!node.getHostname().equalsIgnoreCase(
								InetAddress.getLocalHost().getHostName())) {
							TaskManager.DoTask(new RegisterNodeTask(node));
						}
						break;
					}
					case SEND_STRING_MESSAGE: {
						TaskManager.DoTask(new ReceiveRemoteMessagesTask(node));

						// Server selfServer = new Server();
						// selfServer.setCurrentIP(InetAddress.getLocalHost()
						// .getHostAddress());
						// selfServer.setHostname(InetAddress.getLocalHost()
						// .getHostName());
						// selfServer.setPort(ServerReceiverServlet.LISTENING_PORT);
						// selfServer.setUsername("Server "
						// + DCServer.getLocalHostname());
						//
						// buffer = selfServer.toBytes();
						//
						// dataGram = new DatagramPacket(buffer, buffer.length);
						// dataGram.setPort(node.getCurrentPort());
						// dataGram.setAddress(InetAddress.getByName(node
						// .getCurrentIP()));
						//
						// receivingSocket.send(dataGram);

						break;
					}
					case PING_NODE: {
						Server selfServer = new Server();
						selfServer.setCurrentIP(InetAddress.getLocalHost()
								.getHostAddress());
						selfServer.setHostname(InetAddress.getLocalHost()
								.getHostName());
						selfServer
								.setPort(ServerReceiverServlet.LISTENING_PORT);
						selfServer.setUsername("Server "
								+ DCServer.getLocalHostname());

						buffer = selfServer.toBytes();

						dataGram = new DatagramPacket(buffer, buffer.length);
						dataGram.setPort(node.getCurrentPort());
						dataGram.setAddress(InetAddress.getByName(node
								.getCurrentIP()));

						receivingSocket.send(dataGram);
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
	public Task fromBytes(byte[] byteArray) {
		return null;
	}

	@Override
	public void onProgressUpdate() {
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
		return null;
	}

}
