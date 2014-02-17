package com.project.tasks;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
	public void execute() {

		System.out.println("Listening for remote messages...");

		String message = "NULL";

		do {

			// try {
			// m_SendingSocket = new ServerSocket(LISTEN_PORT);
			//
			// m_RecievingSocket = m_SendingSocket.accept();
			//
			// InputStream sockInputStream = m_RecievingSocket.getInputStream();
			//
			//
			// byte[] objLength = new byte[Long.SIZE / 8];
			//
			// sockInputStream.read(objLength, 0, (Long.SIZE / 8));
			//
			// ByteArrayInputStream is = new ByteArrayInputStream(objLength);
			// ObjectInputStream in = new ObjectInputStream(is);
			//
			// long properLength = in.readLong();
			//
			//
			// //
			// // long byteArrayLength = in.readLong();
			// // tmpStringData = in.readUTF();
			// byte[] messageTaskBytes = new byte[(int) properLength];
			//
			// is.read(messageTaskBytes, objLength.length, (int)properLength);
			//
			
			// PostMessageTask.fromNewBytes(messageTaskBytes);
			//
			// // PrintWriter out = new PrintWriter(
			// // m_RecievingSocket.getOutputStream(), true);
			// // BufferedReader in = new BufferedReader(
			// // new InputStreamReader(
			// // m_RecievingSocket.getInputStream()));
			//
			// TaskManager.DO_TASK(task);
			//
			//
			// } catch (UnknownHostException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

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

				bufferSize = m_RecievingSocket.getReceiveBufferSize();
				System.out.println("Buffer size: " + bufferSize);
			} catch (IOException ex) {
				System.out.println("Can't get socket input stream. ");
			}

			// fos = new FileOutputStream("M:\\test2.xml");
			dataOutStream = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(dataOutStream);

			byte[] bytes = new byte[bufferSize];

			int count;

			try {
				while ((count = is.read(bytes)) > 0) {
					bos.write(bytes, 0, count);
				}

				bos.flush();
				bos.close();
				is.close();
				
				task = (PostMessageTask) PostMessageTask.fromNewBytes(dataOutStream.toByteArray());

				TaskManager.DO_TASK(task);
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
