package com.project.dc_client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.project.framework.Task;
import com.project.tasks.SimpleAbstractTask;
import com.project.ui.ClientGUITask;

public class ClientSenderTask extends SimpleAbstractTask {

	private Socket socket;

	public ClientSenderTask() {
		setTaskId("ClientSenderTask");
	}

	public ClientSenderTask(final Socket clientSoc) {
		socket = clientSoc;
	}

	@Override
	public void executeTask() {
		try {
			BufferedReader instream = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String temp = instream.readLine();
			System.out.println((new StringBuilder())
					.append("trying to open a file called :")
					.append(ClientGUITask.dirName).append(temp).toString());
			File myFile = new File((new StringBuilder())
					.append(ClientGUITask.dirName).append(temp).toString());
			byte buffer[] = new byte[1024];
			DataOutputStream outData = new DataOutputStream(
					socket.getOutputStream());
			outData.writeLong(myFile.length());
			outData.flush();
			FileInputStream fis = new FileInputStream(myFile);
			int count;
			while ((count = fis.read(buffer)) > 0)
				outData.write(buffer, 0, count);
			fis.close();
			outData.flush();
			outData.close();
		} catch (IOException e) {
			System.err.println("Problem sending file!");
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
