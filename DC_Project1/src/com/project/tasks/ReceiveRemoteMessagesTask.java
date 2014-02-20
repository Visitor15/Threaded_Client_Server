package com.project.tasks;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.project.framework.Task;
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
	public void executeTask() {

		try {
			m_SendingSocket = new ServerSocket(LISTEN_PORT);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ObjectInputStream objIn = null;
		
		System.out.println("Listening for remote messages...");

		String message = "NULL";

		do {

			PostMessageTask task;
			InputStream is = null;
			ByteArrayOutputStream dataOutStream = null;
			BufferedOutputStream bos = null;
			int bufferSize = 0;

			try {
				m_RecievingSocket = m_SendingSocket.accept();
			} catch (IOException ex) {
				System.out.println("Can't accept client connection. ");
			}

			try {
				is = m_RecievingSocket.getInputStream();
				
				objIn = new ObjectInputStream(is);
				
				

				bufferSize = m_RecievingSocket.getReceiveBufferSize();
				System.out.println("Buffer size: " + bufferSize);
			} catch (IOException ex) {
				System.out.println("Can't get socket input stream. ");
			}

			byte[] buf = new byte[bufferSize];
			// fos = new FileOutputStream("M:\\test2.xml");
			dataOutStream = new ByteArrayOutputStream();
			
			
			try {
				is.read(buf);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
//			bos = new BufferedOutputStream(dataOutStream);

			byte[] bytes = new byte[bufferSize];

			int count;

			try {
//				while ((count = is.read(bytes)) > 0) {
//					bos.write(bytes, 0, count);
//				}

				dataOutStream.flush();
				dataOutStream.close();
//				bos.flush();
//				bos.close();
//				is.close();
				
				buf = new byte[bufferSize];
				
				objIn.read(buf);
				
				task = (PostMessageTask) PostMessageTask.fromNewBytes(buf);

				TaskManager.DoTask(task);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
