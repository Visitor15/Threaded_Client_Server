package com.project.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.project.framework.Task;
import com.project.server.router.Node;
import com.project.server.router.RoutingTable;

public class SendStringMessageTask extends SimpleAbstractTask implements
		ITaskCallback {

	private Node node;

	private Node clientNode;

	private boolean toServer;

	private String message;

	public SendStringMessageTask(final Node client, boolean toServer) {
		super();
		setTaskId("SendStringMessagesTask");

		this.toServer = toServer;
		clientNode = client;
	}

	public SendStringMessageTask(final Node node) {
		super();
		setTaskId("SendStringMessagesTask");

		this.node = node;
	}

	@Override
	public void executeTask() {
		/*
		 * the following code will connect to the server router and then open a
		 * file for sending the data
		 */

		try {

			if (toServer || node == null) {
				if (clientNode.getDestinationUsername()
						.equalsIgnoreCase("NULL")) {
					TaskManager.DoTaskOnCurrentThread(
							new QueryRoutingTableTask(clientNode
									.getDestinationUsername(), false), this);
				} else {
					TaskManager.DoTaskOnCurrentThread(
							new QueryRoutingTableTask(clientNode
									.getDestinationIP(), true), this);
				}
			}

			Socket clientSocket = new Socket(node.getCurrentIP(),
					node.getCurrentPort()); // port needs to be
											// serverrouters port
			// network output stream
			DataOutputStream send = new DataOutputStream(
					clientSocket.getOutputStream());
			// network input stream
			BufferedReader receive = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			// get a lowercase message from the user (no verification or
			// anything)
			// String message = fileName.getText();

			// String message = "NULL";

			if (node != null) {
				message = node.getStringMessage();
			}

			// InputStream file = new FileInputStream(fileName.getText());
			InputStream file = new FileInputStream(message);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					file));
			String line = null;

//			send.writeBytes(clientNode.message);

			/*
			 * in the while loop below we need to add the statistics for average
			 * length of each line we send the average round trip time of each
			 * message.
			 */

			while ((line = reader.readLine()) != null) // loop to end of file
			// sending every line
			{
				// outputWindow.append("Sending TCP: " + line + "\n"); //we may
				// want to only output statistics if its a long file
				// convert to bytes and write to stream
				send.writeBytes(line + '\n');
				// receive the message back from the server
				String modifiedMsg = receive.readLine();
				System.out.println("Server TCP: " + modifiedMsg);
				// outputWindow.append("Server TCP: " + modifiedMsg + "\n");
				// we are done here close the socket!

			}
			clientSocket.close(); // we are done here

		} catch (IOException e) {
			e.printStackTrace();
		}

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

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(Task task) {
		node = RoutingTable.getInstance().getClientByUsername(
				task.getStringData());
	}
}
