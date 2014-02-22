package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.project.framework.Task;
import com.project.server.router.Client;
import com.project.server.router.Node;
import com.project.server.router.Server;
import com.project.tasks.RegisterNodeTask;
import com.project.tasks.TaskManager;

public class ServerReceiverServlet extends DCServlet {

	public static final int LISTENING_PORT = 11337;

	private DatagramSocket receivingSocket;

	private DatagramPacket dataGram;

	private byte[] buffer;

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 1288745814717362014L;

	public ServerReceiverServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.REGISTRATION_SERVLET, autoStart, callback);

		setTaskId("ServerReceiverServelet");
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
			receivingSocket = new DatagramSocket(LISTENING_PORT);
			do {
//				System.out.println(("Server listening..."));
				dataGram = new DatagramPacket(new byte[512], 512);

				/* Blocking receive */
				receivingSocket.receive(dataGram);

				buffer = dataGram.getData();
				if (buffer != null || buffer.length > 0) {
					Node node = Node.fromBytes(dataGram.getData());

					node.setCurrentIP(dataGram.getAddress().getHostAddress());
					
//					System.out.println("Got node: " + node.getHostname());
					
					switch (node.COMMAND) {
					case REGISTER_NODE: {
						if (!node.getHostname().equalsIgnoreCase(
								InetAddress.getLocalHost().getHostName())) {
							TaskManager.DoTask(new RegisterNodeTask(node));
						}
						break;
					}
					case EXECUTE_TASK: {
						/* Implement me */
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
	}

	@Override
	public byte[] toBytes() {
		return null;
	}

	@Override
	public Task fromBytes(byte[] byteArray) {
		return null;
	}

}
