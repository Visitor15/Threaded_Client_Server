package com.project.dc_client;

import java.net.ServerSocket;

import com.project.framework.Task;
import com.project.tasks.SimpleAbstractTask;
import com.project.tasks.TaskManager;
import com.project.ui.ClientGUITask;

public class ClientSenderHandlerTask extends SimpleAbstractTask {

	ServerSocket fs;

	public ClientSenderHandlerTask() {

	}

	@Override
	public void executeTask() {
		try {
			fs = new ServerSocket(ClientGUITask.port);
		} catch (Exception e) {
			System.out.println("problem with initial file sender setup");
		}
		try {
			do {
				while (!ClientGUITask.clientSend)
					;
				try {
					if (ClientGUITask.port != fs.getLocalPort()) {
						fs.close();
						System.out.println((new StringBuilder())
								.append("Opened port ")
								.append(ClientGUITask.port)
								.append(" for sending files").toString());
						fs = new ServerSocket(ClientGUITask.port);
					}
					fs.setSoTimeout(0x186a0);
					ClientSenderTask fileserver = new ClientSenderTask(
							fs.accept());
					TaskManager.DoTask(fileserver);
				} catch (Exception e) {
					System.out.println((new StringBuilder())
							.append("Client sender handler : ").append(e)
							.toString());
					System.out
							.println("(handler times out to allow port change)");
				}
			} while (true);
		} catch (Exception e) {
			System.out.println("But this should be impossible!");
		}
		ClientGUITask.clientSenderExists = false;
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
