package com.project.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.project.framework.Task;
import com.project.server.router.Client;

public class PostRemoteMessageTask extends SimpleAbstractTask {
	
	private ServerSocket m_ServerSocket;
	
	private Socket m_RecievingSocket;
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
		
		try {
			m_SendingSocket = new Socket(connectedPeers.get(0).getIP(), connectedPeers.get(0).getMessagePort());
			
			m_SendingSocket.getOutputStream().write(new SimpleAbstractTask("RemoteMessagingTask") {
				/*
				 *	Generated ID for Serializable. 
				 */
				private static final long serialVersionUID = 7775240721976465481L;
				

				@Override
				public void execute() {
					setStringData("Penis Bob");
					
					System.out.println("Message received from " + getStringData());
					
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
				
			}.toBytes());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream out;
		
		try {
			out = new ObjectOutputStream(os);
			out.writeUTF(getTaskId());
			out.writeUTF(getStringData());
			out.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final byte[] res = os.toByteArray();

		return res;
	}

	@Override
	public Task fromBytes(byte[] byteArray) {
		SimpleAbstractTask task = new FindDefaultGatewayTask();
		ByteArrayInputStream is;
		ObjectInputStream in;
		
		String tmpTaskId;
		String tmpStringData;
		
		try {
			is = new ByteArrayInputStream(byteArray);
			in = new ObjectInputStream(is);

			tmpTaskId = in.readUTF();
			tmpStringData = in.readUTF();
			
			task.setTaskId(tmpTaskId);
			task.setStringData(tmpStringData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return task;
	}

}
