package com.project.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.project.framework.Task;
import com.project.io.SynchedInOut;
import com.project.server.router.Client;

public class PostRemoteMessageTask extends SimpleAbstractTask {

	/*
	 * Generated ID for Serializable.
	 */
	private static final long serialVersionUID = -5335728610568586645L;

	private Socket m_SendingSocket;

	private final String mUser;

	private final ArrayList<Client> connectedPeers;

	private static final int LISTEN_PORT = 9898;
	private static final int SEND_PORT = 9797;

	public PostRemoteMessageTask(final String user) {
		mUser = user;
		connectedPeers = new ArrayList<Client>();
	}

	@Override
	public void executeTask() {

		System.out.println("Running " + this.getClass().getSimpleName());

		String message = "NULL";

		do {
			message = "NULL";
			message = SynchedInOut.getInstance().postMessageForUserInput(
					"MESSAGE: ");

			try {
				// m_SendingSocket = new Socket(connectedPeers.get(0).getIP(),
				// connectedPeers.get(0).getMessagePort());

				m_SendingSocket = new Socket("192.168.1.9", 9797);
//
				PostMessageTask task = new PostMessageTask();
				task.setMessage(message);
//				
//				System.out.println("Wrote " + task.toBytes().length + " bytes");
//
//				DataOutputStream dataOutStream = new DataOutputStream(m_SendingSocket.getOutputStream());
//				
//				dataOutStream.writeUTF(getTaskId());
//				dataOutStream.writeUTF(getStringData());
//				dataOutStream.flush();
//				dataOutStream.close();
//				
////				m_SendingSocket.getOutputStream().flush();
////				m_SendingSocket.getOutputStream().close();
//				m_SendingSocket.close();
				
				
//				 File file = new File("M:\\test.xml");
				    // Get the size of the file
//				    long length = file.length();
//				    if (length > Integer.MAX_VALUE) {
//				        System.out.println("File is too large.");
//				    }
				    byte[] bytes = task.toBytes();
//				    FileInputStream fis = new FileInputStream(file);
				    ByteArrayInputStream arrayInStream = new ByteArrayInputStream(bytes);
				    BufferedInputStream bis = new BufferedInputStream(arrayInStream);
				    BufferedOutputStream out = new BufferedOutputStream(m_SendingSocket.getOutputStream());

				    int count;

				    while ((count = bis.read(bytes)) > 0) {
				        out.write(bytes, 0, count);
				    }

				    out.flush();
				    out.close();
//				    fis.close();
				    bis.close();
				    m_SendingSocket.close();
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
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
