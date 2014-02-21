package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.project.framework.Task;
import com.project.server.router.Client;
import com.project.server.router.RoutingTable;
import com.project.tasks.FindDefaultGatewayTask;
import com.project.tasks.ITaskCallback;
import com.project.tasks.TaskManager;

public class ServerIdentifierServlet extends DCServlet implements ITaskCallback {

	private DatagramSocket dataGramSocket;

	private boolean isLocatingDNS = false;

	public ServerIdentifierServlet(final boolean autoStart,
			final IServletCallback callback) {
		super("ServerIdentifierServlet", SERVLET_TYPE.SERVER_IDENTIFIER_SERVLET, autoStart, callback);

//		setTaskId("ServerIdentifierServlet");
		// if (autoStart) {
		// startServlet();
		// }

		// init();
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

//		executeFindDNSTask();
		System.out.print("Locating DNS...");
//		do {
//			try {
//				Thread.sleep(1000);
//				System.out.print("...");
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} while (isLocatingDNS);

		MulticastSocket socket = null;
		DatagramSocket returnSocket = null;
		try {
			System.out.println("HIT");

			socket = new MulticastSocket(1337);
//			socket.setBroadcast(true);
//			socket.joinGroup(InetAddress.getByName("228.5.6.7"));

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] buf = new byte[1024];
		DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
		DatagramPacket sendingPacket;

		System.out.println("Listening for clients...");

		while (isExecuting()) {
			try {

				if (socket != null) {
					// System.out.println("BEGIN");
					socket.receive(receivedPacket);
				}

				String localHost = InetAddress.getLocalHost().getHostAddress();
				// buf = localHost.getBytes();

				/* If I equal myself */
				if (receivedPacket.getAddress().getHostName()
						.equals(InetAddress.getLocalHost().getHostName())) {
					// continue;
				}

				String clientData = new String(receivedPacket.getData());

				String clientAddress = "NULL";
				String clientPort = "NULL";

				if (clientData != null) {
					clientAddress = clientData.substring(0,
							clientData.indexOf("|"));
					clientPort = clientData.substring(
							(clientData.indexOf("|") + 1),
							(clientData.indexOf("|") + 5));
				}

				boolean register = false;
				if (clientAddress.equalsIgnoreCase(InetAddress.getLocalHost()
						.getHostName())) {
					// System.out
					// .println("I don't need my own kind around here. (SERVER)");
					// continue;
				} else {
					register = true;
				}

				System.out.println("Got client: " + clientData);

				Client client = new Client();
				
				client.setHostname(clientAddress);
				client.setPort(Integer.parseInt(clientPort));

				if (register) {
					if (!RoutingTable.getInstance().registerClient(client)) {
						System.out.println("Client already added");
						register = false;
						// continue;
					}

					System.out.println("Got client hostname: " + clientAddress
							+ " AND port: " + clientPort);
				}

				InetAddress address = receivedPacket.getAddress();
				int port = receivedPacket.getPort();

				String localServerHostAddress = InetAddress.getLocalHost()
						.getHostName();
				String localServerPort = String.valueOf(1337);

				String serverReturnData = (localServerHostAddress + "|" + localServerPort);

				// System.out.println("Returning data: " + serverReturnData);

				buf = serverReturnData.getBytes();

				sendingPacket = new DatagramPacket(buf, buf.length,
						receivedPacket.getAddress(),
						Integer.parseInt(clientPort));

				// socket.setBroadcast(false);
				// socket.send(sendingPacket);

				// socket.disconnect();

				if (register) {
					// if (returnSocket == null || returnSocket.isClosed()) {
					// returnSocket = new DatagramSocket(
					// Integer.parseInt(clientPort));
					// returnSocket.setReuseAddress(true);
					// }
					socket.send(sendingPacket);
				}

				// Thread.sleep(2000);
				// returnSocket.close();
				// returnSocket.disconnect();

				Thread.sleep(2000);

			} catch (SocketTimeoutException e) {

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		onFinished();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub

		System.out.println(this.getClass().getSimpleName() + " finished");
	}

//	private void executeFindDNSTask() {
//		isLocatingDNS = true;
//		TaskManager.DO_TASK(new FindDefaultGatewayTask(this));
//	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(Task task) {
		System.out.println("GOT DNS: " + task.getStringData());

		this.stringData = task.getStringData();

		isLocatingDNS = false;
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
