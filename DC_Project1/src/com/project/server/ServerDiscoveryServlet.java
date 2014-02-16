package com.project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.project.server.router.RoutingTable;
import com.project.server.router.Server;

public class ServerDiscoveryServlet extends DCServlet {

	private DatagramSocket dataGramSocket;

	public ServerDiscoveryServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.REGISTRATION_SERVLET, autoStart, callback);

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
	public void execute() {

		int count = 0;
		DatagramSocket listeningSocket = null;
		MulticastSocket socket = null;
		do {
			try {

				// socket = new MulticastSocket(1337);
				// socket.setBroadcast(true);

				// InetAddress localHost = Inet4Address.getLocalHost();
				// NetworkInterface networkInterface =
				// NetworkInterface.getByInetAddress(localHost);

				// System.out.println(networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength());

				// for (InterfaceAddress address :
				// networkInterface.getInterfaceAddresses()) {
				// System.out.println(address.getNetworkPrefixLength());
				// }

				// listeningSocket = new DatagramSocket(MY_PORT);
				// listeningSocket.setSoTimeout(3000);
				dataGramSocket = new DatagramSocket(MY_PORT);
				dataGramSocket.setBroadcast(true);
				dataGramSocket.setReuseAddress(true);
				dataGramSocket.setSoTimeout(50);
			} catch (SocketException e) {
				MY_PORT = MY_PORT + 100;
				execute();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			byte[] buf = new byte[1024];
			DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
			DatagramPacket sendingPacket;

			System.out.println("Looking for server");

			RoutingTable.getInstance();

			System.out.print("Waiting for default gateway to resolve...");
			do {
				try {
					System.out.print("...");

					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (RoutingTable.getInstance().getDefaultGetway()
					.equalsIgnoreCase("NULL"));

			String defGateway = RoutingTable.getInstance().getDefaultGetway();

			System.out.println("DEF GATEWAY IS: " + defGateway);

			String ipPiece = defGateway.substring(0, defGateway.length() - 1);

			for (int i = 0; i < 255; i++) {

				String listeningPort = String.valueOf(MY_PORT);

				String toServerStr;
				try {
					toServerStr = (InetAddress.getLocalHost().getHostName()
							+ "|" + listeningPort);

					buf = toServerStr.getBytes();

					String builtIP = ipPiece + Integer.toString(i);

					// System.out.println("BUILT IP: " + builtIP);

					sendingPacket = new DatagramPacket(buf, buf.length,
							InetAddress.getByName(ipPiece + String.valueOf(i)),
							1337);
					// dataGramSocket.setBroadcast(true);
					// dataGramSocket.setReuseAddress(true);
					dataGramSocket.send(sendingPacket);

					dataGramSocket.receive(receivedPacket);

					String localHost = InetAddress.getLocalHost()
							.getHostAddress();
					buf = localHost.getBytes();

					InetAddress address = receivedPacket.getAddress();
					int port = receivedPacket.getPort();

					String serverData = new String(receivedPacket.getData());

					String serverPort = "NULL";
					String serverAddress = "NULL";

					serverAddress = serverData.substring(0,
							serverData.indexOf("|"));
					serverPort = serverData.substring(
							serverData.indexOf("|") + 1,
							serverData.indexOf("|") + 5);

					System.out.println("Got server: " + serverData);

					boolean register = false;
					if (serverAddress.equalsIgnoreCase(InetAddress
							.getLocalHost().getHostName())) {
						// System.out.println("I don't need my own kind around here.");
						// onFinished();
						// continue;

					} else {
						register = true;
					}

					if (register) {
						Server foundServer = new Server(serverAddress,
								Integer.parseInt(serverPort));

						// if
						// (RoutingTable.getInstance().registerServer(foundServer))
						// {
						// System.out.println("Server already registered.");
						// continue;
						// }

						System.out.println("Found server: "
								+ foundServer.getId() + ":"
								+ foundServer.getPort());

						// if
						// (RoutingTable.getInstance().registerServer(foundServer))
						// {
						// System.out.println("Server already registered.");
						// }
					}

					// Thread.sleep(2000);

				} catch (SocketTimeoutException e) {

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			System.out.println("30 PAUSE - DISCOVERY TASK");
			
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (isExecuting());

		// do {
		// try {
		// count++;
		// // System.out.println("Here we go.");
		// String listeningPort = String.valueOf(MY_PORT);
		//
		// String toServerStr = (InetAddress.getLocalHost().getHostName()
		// + "|" + listeningPort);
		// buf = toServerStr.getBytes();
		//
		// sendingPacket = new DatagramPacket(buf, buf.length,
		// InetAddress.getByName("228.5.6.7"), 1337);
		// // dataGramSocket.setBroadcast(true);
		// // dataGramSocket.setReuseAddress(true);
		// dataGramSocket.send(sendingPacket);
		//
		// // System.out.println("SENT!");
		//
		// dataGramSocket.receive(receivedPacket);
		//
		// String localHost = InetAddress.getLocalHost().getHostAddress();
		// buf = localHost.getBytes();
		//
		// InetAddress address = receivedPacket.getAddress();
		// int port = receivedPacket.getPort();
		//
		// String serverData = new String(receivedPacket.getData());
		//
		// String serverPort = "NULL";
		// String serverAddress = "NULL";
		//
		// serverAddress = serverData
		// .substring(0, serverData.indexOf("|"));
		// serverPort = serverData.substring(serverData.indexOf("|") + 1,
		// serverData.indexOf("|") + 5);
		//
		// System.out.println("Got server: " + serverData);
		//
		// boolean register = false;
		// if (serverAddress.equalsIgnoreCase(InetAddress.getLocalHost()
		// .getHostName())) {
		// // System.out.println("I don't need my own kind around here.");
		// // onFinished();
		// // continue;
		//
		// } else {
		// register = true;
		// }
		//
		// if (register) {
		// Server foundServer = new Server(serverAddress,
		// Integer.parseInt(serverPort));
		//
		// // if
		// // (RoutingTable.getInstance().registerServer(foundServer))
		// // {
		// // System.out.println("Server already registered.");
		// // continue;
		// // }
		//
		// System.out.println("Found server: " + foundServer.getId()
		// + ":" + foundServer.getPort());
		//
		// // if
		// // (RoutingTable.getInstance().registerServer(foundServer))
		// // {
		// // System.out.println("Server already registered.");
		// // }
		// }
		//
		// Thread.sleep(2000);
		//
		// } catch (UnknownHostException e) {
		//
		// } catch (SocketTimeoutException e) {
		// continue;
		// // e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// // System.out.println("HEREHER HEER");
		// e.printStackTrace();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// // System.out.println("Ah JEEBUS");
		// e.printStackTrace();
		// }
		//
		// // System.out.println("HIT HIT!!");
		//
		// // } while (isExecuting());
		// } while (count < 25);

		// onFinished();
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

}
