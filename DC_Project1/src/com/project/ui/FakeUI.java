package com.project.ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.RoutingTableServlet;
import com.project.server.ServerReceiverServlet;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.router.Client;
import com.project.tasks.ITaskCallback;
import com.project.tasks.SendStringMessageTask;
import com.project.tasks.TaskManager;

public class FakeUI implements ITaskCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2084838384357345186L;

	public FakeUI() {
		
	}
	
	public void start() {
		try {
			doTempTest();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doTempTest() throws UnknownHostException {
		Scanner input = new Scanner(System.in);
		System.out.print("Are you a ROUTER, SERVER, or CLIENT? (R/C/S): ");
		String userInput = input.nextLine();

		Client selfClient = new Client();
		selfClient.setCurrentIP(InetAddress.getLocalHost().getHostAddress());
		selfClient.setHostname(InetAddress.getLocalHost().getHostName());
		selfClient.setPort(ServerReceiverServlet.LISTENING_PORT);
		selfClient.setUsername("Client " + DCServer.getLocalHostname());
		selfClient.SERVER_COMMAND = COMMAND_TYPE.ROUTE_DATA_TO_SERVER;
		selfClient.ROUTERTABLE_COMMAND = COMMAND_TYPE.PING_NODE;

		if (userInput.equalsIgnoreCase("C")) {
			System.out.print("Router IP: ");
			userInput = input.nextLine();
			
			selfClient.setRouterIP(userInput);
			
			System.out.print("Router Port: ");
			userInput = input.nextLine();
			
			selfClient.setRouterPort(Integer.parseInt(userInput));
			
			System.out.print("Recipient IP: ");
			userInput = input.nextLine();

			selfClient.setDestinationIP(userInput);

			/* *this* is the ITaskCallback interface for task callback in onTaskProgress, onTaskStart, onTaskFinished */
			TaskManager.DoTask(new SendStringMessageTask(selfClient, true, this));
		} else if (userInput.equalsIgnoreCase("R")) {
			TaskManager.DoTask(new RoutingTableServlet());
		} else {
			System.out.print("Router IP: ");
			userInput = input.nextLine();
			
			selfClient.setRouterIP(userInput);
			
			System.out.print("Router Port: ");
			userInput = input.nextLine();
			
			selfClient.setRouterPort(Integer.parseInt(userInput));
			
			// RoutingTable
			TaskManager.DoTask(new ServerReceiverServlet());
		}
		
		input.close();
	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskFinished(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskProgress(Task task) {
		System.out.println("Received: " + task.getStringData());
	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub
		
	}
}
