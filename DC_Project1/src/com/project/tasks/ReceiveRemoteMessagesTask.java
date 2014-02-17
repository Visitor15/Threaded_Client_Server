package com.project.tasks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.project.framework.Task;
import com.project.io.SynchedInOut;
import com.project.server.router.Client;

public class ReceiveRemoteMessagesTask extends SimpleAbstractTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1192091671636120314L;

	private ServerSocket m_SendingSocket;

	private Socket m_RecievingSocket;

	private final ArrayList<Client> connectedPeers;

	private static final int LISTEN_PORT = 9797;
	private static final int SEND_PORT = 9797;

	public ReceiveRemoteMessagesTask() {
		setTaskId("ReceiveRemoteMessagesTask");
		connectedPeers = new ArrayList<Client>();
	}

	@Override
	public void execute() {
		
		System.out.println("Listening for remote messages...");

		String message = "NULL";

		do {

			try {
				m_SendingSocket = new ServerSocket(LISTEN_PORT);

				m_RecievingSocket = m_SendingSocket.accept();

				InputStream sockInputStream = m_RecievingSocket.getInputStream();
				
				
				byte[] objLength = new byte[Long.SIZE / 8];
				
				sockInputStream.read(objLength, 0, (Long.SIZE / 8));
				
						ByteArrayInputStream is = new ByteArrayInputStream(objLength);
				ObjectInputStream in = new ObjectInputStream(is);
				
				long properLength = in.readLong();
				
				
//
//				long byteArrayLength = in.readLong();
//				tmpStringData = in.readUTF();
				byte[] messageTaskBytes = new byte[(int) properLength];
				
				is.read(messageTaskBytes, objLength.length, (int)properLength);
				
				PostMessageTask task = (PostMessageTask) PostMessageTask.fromNewBytes(messageTaskBytes);
				
//				PrintWriter out = new PrintWriter(
//						m_RecievingSocket.getOutputStream(), true);
//				BufferedReader in = new BufferedReader(
//						new InputStreamReader(
//								m_RecievingSocket.getInputStream()));
				
				TaskManager.DO_TASK(task);
				
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (!message.equalsIgnoreCase("/q"));

		stopTask();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
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
