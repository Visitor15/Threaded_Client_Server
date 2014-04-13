package com.project.dc_client;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.project.framework.Task;
import com.project.tasks.SimpleAbstractTask;
import com.project.ui.ClientGUITask;

public class ClientUpdaterTask extends SimpleAbstractTask {

	public ClientUpdaterTask() {
		setTaskId("ClientUpdaterTask");
	}

	@Override
	public void executeTask() {
		File folder = new File(ClientGUITask.dirName);
		File listOfFiles[] = folder.listFiles();
		int debug = 0;
		do {
			if (debug >= 5)
				break;
			try {
				ClientGUITask.myFiles.clear();
				ClientGUITask.files.clear();
				System.out.println((new StringBuilder()).append("looking at ")
						.append(ClientGUITask.dirName).toString());
				if (listOfFiles.length > 0) {
					for (int i = 0; i < listOfFiles.length; i++)
						if (listOfFiles[i].isFile()
								&& !ClientGUITask.myFiles
										.contains(listOfFiles[i].getName()))
							ClientGUITask.myFiles.add(listOfFiles[i].getName());

				}
				break;
			} catch (Exception excep) {
				debug++;
				System.out.println((new StringBuilder())
						.append("problem with file listing retry :")
						.append(debug).append("out of 5").toString());
			}
		} while (true);
		try {
			Socket localSocket = new Socket(ClientGUITask.serverAddy, 6666);
			localSocket.setSoTimeout(0x186a0);
			BufferedReader Istream = new BufferedReader(new InputStreamReader(
					localSocket.getInputStream()));
			OutputStream Ostream = localSocket.getOutputStream();
			String temp = (new StringBuilder()).append("###")
					.append(ClientGUITask.port).append("\n").toString();
			Ostream.write(temp.getBytes());
			for (int i = 0; i < ClientGUITask.myFiles.size(); i++) {
				temp = (new StringBuilder())
						.append((String) ClientGUITask.myFiles.get(i))
						.append("\n").toString();
				Ostream.write(temp.getBytes());
			}

			System.out.println("done Sending files...");
			temp = "EOF\n";
			Ostream.write(temp.getBytes());

			do {
				System.out.println("sent EOF");
//				if (Istream.ready()) {
					temp = Istream.readLine();
					if (temp.equals("EOF")) {
						localSocket.close();
						Istream.close();
						break;
					}
//				} else {
//					break;
//				}
				if (!ClientGUITask.files.contains(temp)) {
					System.out.println("CLIENT ADDING FILE: " + temp);
					ClientGUITask.files.add(temp);
				}
			} while (true);
		} catch (Exception excep) {
		}
		System.out.println("Done with update");

		stopTask();
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
