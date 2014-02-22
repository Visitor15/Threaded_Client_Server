package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.project.framework.Task;
import com.project.io.SynchedInOut;
import com.project.server.router.RoutingTable;
import com.project.server.router.Server;
import com.project.tasks.ThreadHelper;

public class ServerDiscoveryServlet extends DCServlet {

	private DatagramSocket dataGramSocket;

	public ServerDiscoveryServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.REGISTRATION_SERVLET, autoStart, callback);

		setTaskId("ServerDiscoveryServlet");
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

	int MY_PORT = 5555;

	@Override
	public void executeTask() {
		waitForDefaultGateway();
		String defGateway = DCServer.GetDefaultGateway();

		System.out.println("=====================================");
		System.out.println("Looking for server");
		System.out.println("Default gateway: " + defGateway);
		System.out.println("=====================================");

		int count = 0;

		do {
			try {
				dataGramSocket = new DatagramSocket(MY_PORT);
				dataGramSocket.setBroadcast(true);
				dataGramSocket.setReuseAddress(true);
				dataGramSocket.setSoTimeout(5);
			} catch (SocketException e) {
				MY_PORT = MY_PORT + 100;
				executeTask();
				e.printStackTrace();
			}

			byte[] buf = new byte[1024];
			DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
			DatagramPacket sendingPacket;

			String ipPiece = defGateway.substring(0, defGateway.length() - 1);

			String[] ipPieces = defGateway.split("\\.");

			ipPiece = ipPieces[0] + "." + ipPieces[1] + ".";

			System.out.println("Scan: " + ++count);
			System.out.println("Scanning....");

			/*
			 * This is rather slow. Multithread this.
			 */

			for (int i = 0; i < 2; i++) {

				for (int j = 0; j < 255; j++) {

					String listeningPort = String.valueOf(MY_PORT);

					String toServerStr;
					try {
						toServerStr = (InetAddress.getLocalHost().getHostName()
								+ "|" + listeningPort);

						buf = toServerStr.getBytes();

						String builtIP = ipPiece + Integer.toString(i) + "."
								+ Integer.toString(j);

						// System.out.println("BUILT IP: " + builtIP);

						sendingPacket = new DatagramPacket(buf, buf.length,
								InetAddress.getByName(builtIP), 1337);
						dataGramSocket.send(sendingPacket);
						dataGramSocket.receive(receivedPacket);

						String localHost = InetAddress.getLocalHost()
								.getHostAddress();
						buf = localHost.getBytes();

						InetAddress address = receivedPacket.getAddress();
						String serverData = new String(receivedPacket.getData());

						String serverPort = "NULL";
						String serverAddress = "NULL";
						String hostName = address.getHostName();

						/* Extracting server data */
						serverAddress = serverData.substring(0,
								serverData.indexOf("|"));
						serverPort = serverData.substring(
								serverData.indexOf("|") + 1,
								serverData.indexOf("|") + 5);

						System.out.println("Discovered server: " + hostName);
//						registerServer(new Server(serverAddress, hostName,
//								Integer.parseInt(serverPort)));

					} catch (SocketTimeoutException e) {

					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				// if ((i % 5) == 0) {
				// System.out.println("%" + ((i / 254.0) * 100));
				// }
			}
			
			dataGramSocket.close();

			String userOpt = SynchedInOut.getInstance()
					.postMessageForUserInput("Scan finished. Rescan? (y/n): ");

			if (userOpt.equalsIgnoreCase("y")) {
				executeTask();
			}

			else {
				stopTask();
			}

//			pauseServerDiscoveryTask();
			

		} while (isExecuting());

		stopTask();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		System.out.println("FINISHED!");
		// this.stopServlet();
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

	private void waitForDefaultGateway() {
		System.out.print("Waiting for default gateway to resolve...");
		do {
			ThreadHelper.sleepThread(1000);
		} while (DCServer.GetDefaultGateway().equalsIgnoreCase("NULL"));
	}

	private boolean registerServer(final Server server) {
		try {
			if (server.getHostname().equalsIgnoreCase(
					InetAddress.getLocalHost().getHostName())) {
				return false;
			}
		} catch (UnknownHostException e) {
			return false;
		}

		if (RoutingTable.getInstance().registerServer(server)) {
			System.out.println("Server already registered.");
			return false;
		}

//		System.out.println("Found server: " + server.getHostname() + ":"
//				+ server.getPort());

		return true;
	}

	private void pauseServerDiscoveryTask() {
		System.out.println("10 PAUSE - DISCOVERY TASK");

		ThreadHelper.sleepThread(10000);
	}
}
