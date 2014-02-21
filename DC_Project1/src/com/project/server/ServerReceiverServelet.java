package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.project.framework.Task;
import com.project.server.router.Client;
import com.project.tasks.RegisterClientTask;
import com.project.tasks.TaskManager;

public class ServerReceiverServelet extends DCServlet {

	public static final int LISTENING_PORT = 11337;

	private DatagramSocket receivingSocket;

	private DatagramPacket dataGram;

	private byte[] buffer;

	private Client client;

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 1288745814717362014L;

	public ServerReceiverServelet(final boolean autoStart,
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
				dataGram = new DatagramPacket(new byte[512], 512);

				/* Blocking receive */
				receivingSocket.receive(dataGram);

				buffer = dataGram.getData();

				client = Client.fromBytes(dataGram.getData());

				if (buffer != null && buffer.length > 0) {
					client = Client.fromBytes(dataGram.getData());
					if (!client.getHostname().equalsIgnoreCase(
							InetAddress.getLocalHost().getHostName())) {
						System.out.println("Server received client: "
								+ client.getUsername());
						TaskManager.DoTask(new RegisterClientTask(client));
					}
				} else {
					// Received no data.
				}

			} while (isExecuting());

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
