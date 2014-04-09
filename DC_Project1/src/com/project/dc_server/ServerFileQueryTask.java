package com.project.dc_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.project.framework.Task;
import com.project.main.Main;
import com.project.tasks.SimpleAbstractTask;

public class ServerFileQueryTask extends SimpleAbstractTask {

	byte[] sendData;
	DatagramPacket sendPacket;
	DatagramPacket packet;
	byte[] recBuffer;

	public ServerFileQueryTask() {

	}

	@Override
	public void executeTask() {
		try {
			DatagramSocket ssFQ = new DatagramSocket(4112); // create a new
															// socket
			DatagramSocket socket = new DatagramSocket(6666); // UDP socket to
																// listen on,
			String requestedFile;
			InetAddress clientIP;
			int clientPort;
			while (true) // forever
			{
				try {
					socket.setSoTimeout(10000);
					System.out.println("Waiting for file request");
					recBuffer = new byte[15000];
					packet = new DatagramPacket(recBuffer, recBuffer.length);
					socket.receive(packet); // read an incoming packet
					clientIP = packet.getAddress();
					clientPort = packet.getPort();
					System.out.println("File request received from "
							+ packet.getAddress().getHostAddress());
					String message = new String(packet.getData()).trim();
					requestedFile = message;

					boolean found = false;
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
							sendData = temp.getBytes();
							sendPacket = new DatagramPacket(sendData,
									sendData.length, packet.getAddress(),
									packet.getPort());
							socket.send(sendPacket);
							System.out.println("Responded with "
									+ DCServerTask.clientfiles.get(i).hosts
											.get(0).IPaddress
									+ ":"
									+ DCServerTask.clientfiles.get(i).hosts
											.get(0).port);
							found = true;
							break;
						}
					}
					if (!found) // it not listed on this server
					{
						for (int i = 0; i < DCServerTask.servers.size(); ++i)// ask
																				// all
																				// servers
						{
							if (DCServerTask.servers.get(i).idNum != DCServerTask.serverID) // ask
																							// all
																							// servers
																							// for
																							// the
																							// file
																							// name
							{
								sendData = requestedFile.getBytes();
								sendPacket = new DatagramPacket(sendData,
										sendData.length,
										InetAddress
												.getByName(DCServerTask.servers
														.get(i).serverIP), 4111);
								ssFQ.send(sendPacket); // blast it out this port
							}
						}
						ssFQ.receive(packet); // read an incoming packet i hope
												// its correct i'm not going to
												// check it!
						message = new String(packet.getData()).trim();
						sendData = message.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(
								sendData, sendData.length, clientIP, clientPort);
						socket.send(sendPacket); // forward anything recieved
													// back to the client... it
													// had better be correct

					}

				} catch (Exception e) {
					System.out.println("Error in file query thread :" + e);
				}
			}
		} catch (IOException except) {
			System.out.println("Something went wrong: " + except);
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
