package com.project.dc_server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.project.framework.Task;
import com.project.main.Main;
import com.project.tasks.SimpleAbstractTask;

public class SelfServerFileQueryTask extends SimpleAbstractTask {

	public SelfServerFileQueryTask() {
		setTaskId("SelfServerFileQueryTask");
	}

	@Override
	public void executeTask() {
		try {
			DatagramSocket socket = new DatagramSocket(4111); // UDP socket to
																// listen on,

			while (true) // forever
			{
				try {
					System.out.println("Waiting server file request");
					byte[] recBuffer = new byte[15000];
					DatagramPacket packet = new DatagramPacket(recBuffer,
							recBuffer.length);
					socket.receive(packet); // read an incoming packet
					System.out.println("File request received from "
							+ packet.getAddress().getHostAddress());
					String message = new String(packet.getData()).trim();

					System.out.println("File request received from a server");
					for (int i = 0; i < DCServerTask.clientfiles.size(); ++i) // for
																				// all
																				// of
																				// our
																				// files
																				// on
																				// record
					{
						if (DCServerTask.clientfiles.get(i).fName
								.equals(message)) // if the message send matches
													// a file name
						{ // respond with the first host in the list(ip and
							// port)
							String temp = DCServerTask.clientfiles.get(i).hosts
									.get(0).IPaddress
									+ ":"
									+ DCServerTask.clientfiles.get(i).hosts
											.get(0).port;
							byte[] sendData = temp.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(
									sendData, sendData.length,
									packet.getAddress(), packet.getPort());
							socket.send(sendPacket);
							System.out.println("Responded with "
									+ DCServerTask.clientfiles.get(i).hosts
											.get(0).IPaddress
									+ ":"
									+ DCServerTask.clientfiles.get(i).hosts
											.get(0).port);
							break;
						}
					}
				} catch (Exception e) {
					System.out.println("Error in Server server file query : "
							+ e);
				}

			}

		} catch (Exception excep) {
			System.out.print(excep);
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
