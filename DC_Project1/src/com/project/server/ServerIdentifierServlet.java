package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.project.server.router.Client;

public class ServerIdentifierServlet extends DCServlet {

	private DatagramSocket dataGramSocket;

	public ServerIdentifierServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.SERVER_IDENTIFIER_SERVLET, autoStart, callback);

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
	public void execute() {
		MulticastSocket socket = null;
//		DatagramSocket returnSocket = null;
		try {
			System.out.println("HIT");

			socket = new MulticastSocket(1337);
			socket.setBroadcast(true);
			// socket.joinGroup(InetAddress.getByName("255.255.255.255"));

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
					System.out.println("BEGIN");
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
//					System.out
//							.println("I don't need my own kind around here. (SERVER)");
					// continue;
				} else {
					register = true;
				}

				System.out.println("Got client: " + clientData);
				
				Client client = new Client(clientAddress,
						Integer.parseInt(clientPort));

				if (register) {
//					if (!RoutingTable.getInstance().registerClient(client)) {
//						System.out.println("Client already added");
//						// continue;
//					}
					
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

				sendingPacket = new DatagramPacket(buf, buf.length, receivedPacket.getAddress(),
						Integer.parseInt(clientPort));

//				socket.setBroadcast(false);
//				socket.send(sendingPacket);
				
				socket.disconnect();
				
				DatagramSocket returnSocket = new DatagramSocket(Integer.parseInt(clientPort));
				returnSocket.send(sendingPacket);
				returnSocket.disconnect();
				returnSocket.close();

			} catch (SocketTimeoutException e) {

			} catch (IOException e) {
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

}
