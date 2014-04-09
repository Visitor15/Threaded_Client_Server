package com.project.dc_client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;

import com.project.framework.Task;
import com.project.tasks.SimplePersistentTask;
import com.project.ui.ClientGUITask;

public class ClientTask extends SimplePersistentTask {

	public ClientTask() {
		setTaskId("ClientTask");
	}

	@Override
	public void executeTask() {
		try {
			System.out.println("preparing UDP broadcast");
			byte senddata[] = "$$$".getBytes();
			DatagramSocket clientN = new DatagramSocket();
			clientN.setBroadcast(true);
			try {
				DatagramPacket sendPacket = new DatagramPacket(senddata,
						senddata.length,
						InetAddress.getByName("255.255.255.255"), 6785);
				clientN.send(sendPacket);
				System.out.println("broadcast to : 255.255.255.255");
			} catch (Exception excep) {
				System.out.println("Failed to broadcast to : 255.255.255.255");
			}
			for (Enumeration interfaces = NetworkInterface
					.getNetworkInterfaces(); interfaces.hasMoreElements();) {
				NetworkInterface networkInterface = (NetworkInterface) interfaces
						.nextElement();
				if (!networkInterface.isLoopback() && networkInterface.isUp()) {
					Iterator i$ = networkInterface.getInterfaceAddresses()
							.iterator();
					while (i$.hasNext()) {
						InterfaceAddress interfaceAddress = (InterfaceAddress) i$
								.next();
						InetAddress broadcast = interfaceAddress.getBroadcast();
						if (broadcast != null)
							try {
								DatagramPacket sendPacket = new DatagramPacket(
										senddata, senddata.length, broadcast,
										6785);
								clientN.send(sendPacket);
								System.out.println((new StringBuilder())
										.append("broadcast to : ")
										.append(broadcast).toString());
							} catch (Exception excep) {
								System.out
										.println("Error broadcast to : rest of broadcast pools");
							}
					}
				}
			}

			System.out.println("Finished broadcasting");
			byte recBuffer[] = new byte[15000];
			DatagramPacket receivePacket = new DatagramPacket(recBuffer,
					recBuffer.length);
			clientN.receive(receivePacket);
			String temp = (new String(receivePacket.getData())).trim();
			if (temp.substring(0, 3).equals("%%%")) {
				ClientGUITask.serverAddy = receivePacket.getAddress()
						.getHostAddress();
				System.out.println((new StringBuilder())
						.append("Found server at ")
						.append(ClientGUITask.serverAddy).append(":6666")
						.toString());
			} else {
				System.out.println((new StringBuilder()).append("recieved : ")
						.append(temp).toString());
			}
			clientN.close();
		} catch (Exception excep) {
			System.out.println("failed on something major");
		}
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

}
