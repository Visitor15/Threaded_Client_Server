package com.project.dc_server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.project.framework.Task;
import com.project.tasks.SimpleAbstractTask;

public class ServerLoadBalancerTask extends SimpleAbstractTask {

	private DatagramSocket clientN;

	public ServerLoadBalancerTask() {
		setTaskId("ServerLoadBalancerTask");
	}

	@Override
	public void executeTask() {
		try {
			String temp = "@@@" + DCServerTask.serverID + ":"
					+ DCServerTask.numClients;
			byte[] senddata = temp.getBytes(); // prepare valid client data for
												// sending to server
			System.out.println("blasting out to all servers!");
			clientN = new DatagramSocket(); // create a new socket
			clientN.setBroadcast(true); // enable broadcasting

			try { // send to the highest order broadcast address
				DatagramPacket sendPacket = new DatagramPacket(senddata,
						senddata.length,
						InetAddress.getByName("255.255.255.255"), 6785);
				clientN.send(sendPacket);
			} catch (Exception excep) // this should never fail
			{
				System.out.println("Failed to broadcast to : 255.255.255.255");
			}
			// next load all network interfaces into a list
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) // for all network interfaces
			{
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback() || !networkInterface.isUp())// if
																				// its
																				// a
																				// 127.0.0.1
																				// (local
																				// address)
																				// or
																				// not
																				// connected
					continue; // skip it
				for (InterfaceAddress interfaceAddress : networkInterface
						.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) // if broadcast isnt allowed
						continue; // skip it
					try {
						DatagramPacket sendPacket = new DatagramPacket(
								senddata, senddata.length, broadcast, 6785);
						clientN.send(sendPacket); // send the packet to the
													// broadcast on all valid
													// interfaces

					} catch (Exception excep) {
						System.out
								.println("Error broadcast to : rest of broadcast pools");
					}
				}
			}
		} catch (Exception excep) {
			System.out.println("failed on something major : " + excep);
		}
		clientN.close();
	}

	@Override
	public Task fromBytes(byte[] byteArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub

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

//	public class serverLoads {
//		public int idNum;
//		public int numClients;
//		String serverIP;
//	}
}
