package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.project.server.router.RoutingTable;
import com.project.server.router.Server;

public class ServerDiscoveryServlet extends DCServlet {

	private DatagramSocket dataGramSocket;

	public ServerDiscoveryServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.REGISTRATION_SERVLET, autoStart, callback);

		if (autoStart) {
			startServlet();
		}

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

	int MY_PORT = 4242;

	@Override
	public void execute() {

		int count = 0;

		try {
			dataGramSocket = new DatagramSocket(MY_PORT);
			dataGramSocket.setBroadcast(true);
			dataGramSocket.setReuseAddress(true);
			dataGramSocket.setSoTimeout(3000);
		} catch (SocketException e) {
			MY_PORT = MY_PORT + 100;
			execute();
			e.printStackTrace();
		}

		byte[] buf = new byte[1024];
		DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
		DatagramPacket sendingPacket;

		System.out.println("Looking for server");

		do {
			try {
				count++;
				System.out.println("Here we go.");
				String listeningPort = String.valueOf(MY_PORT);

				String toServerStr = (InetAddress.getLocalHost().getHostName()
						+ "|" + listeningPort);
				buf = toServerStr.getBytes();

				sendingPacket = new DatagramPacket(buf, buf.length,
						InetAddress.getByName("255.255.255.255"), 1337);
				// dataGramSocket.setBroadcast(true);
				// dataGramSocket.setReuseAddress(true);
				dataGramSocket.send(sendingPacket);
				dataGramSocket.receive(receivedPacket);

				String localHost = InetAddress.getLocalHost().getHostAddress();
				buf = localHost.getBytes();

				InetAddress address = receivedPacket.getAddress();
				int port = receivedPacket.getPort();

				String serverData = new String(receivedPacket.getData());

				String serverPort = "NULL";
				String serverAddress = "NULL";

				serverAddress = serverData
						.substring(0, serverData.indexOf("|"));
				serverPort = serverData.substring(serverData.indexOf("|") + 1,
						serverData.indexOf("|") + 5);

				boolean register = false;
				if (serverAddress.equalsIgnoreCase(InetAddress.getLocalHost()
						.getHostName())) {
//					System.out.println("I don't need my own kind around here.");
					// onFinished();
//					continue;

				} else {
					register = true;
				}

				if (register) {
					Server foundServer = new Server(serverAddress,
							Integer.parseInt(serverPort));

					if (RoutingTable.getInstance().registerServer(foundServer)) {
						System.out.println("Server already registered.");
						continue;
					}

					System.out.println("Found server: " + foundServer.getId()
							+ ":" + foundServer.getPort());

					if (RoutingTable.getInstance().registerServer(foundServer)) {
						System.out.println("Server already registered.");
					}
				}

				Thread.sleep(5000);

			} catch (SocketTimeoutException e) {

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// } while (isExecuting());
		} while (count < 5);

		onFinished();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		System.out.println("FINISHED!");
		this.stopServlet();
	}

}
