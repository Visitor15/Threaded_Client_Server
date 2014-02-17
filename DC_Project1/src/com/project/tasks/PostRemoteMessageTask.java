package com.project.tasks;

import java.io.IOException;
import java.net.ServerSocket;
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
	public void execute() {

		String message = "NULL";

		do {
			message = "NULL";
			message = SynchedInOut.getInstance().postMessageForUserInput(
					"MESSAGE: ");

			try {
				m_SendingSocket = new Socket(connectedPeers.get(0).getIP(),
						connectedPeers.get(0).getMessagePort());

				PostMessageTask task = new PostMessageTask();
				task.setMessage(message);

				m_SendingSocket.getOutputStream().write(task.toBytes());

				m_SendingSocket.close();
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
